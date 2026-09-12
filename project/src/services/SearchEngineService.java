package services;

import algorithms.dp.WagnerFischerEditDistance;
import algorithms.string.*;
import interfaces.SearchAlgorithm;
import model.Complaint;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

public class SearchEngineService {

    private static SearchEngineService instance;

    private final KMPAlgorithm kmpAlgorithm = new KMPAlgorithm();
    private final ZAlgorithm zAlgorithm = new ZAlgorithm();
    private final RabinKarpAlgorithm rabinKarpAlgorithm = new RabinKarpAlgorithm();
    private final NaivePatternSearch naivePatternSearch = new NaivePatternSearch();
    private final WagnerFischerEditDistance editDistance = new WagnerFischerEditDistance();
    private final CustomArrayList<String> standardDictionary = new CustomArrayList<>();

    public SearchEngineService() {
        initializeDictionary();
    }

    public static synchronized SearchEngineService getInstance() {
        if (instance == null) {
            instance = new SearchEngineService();
        }
        return instance;
    }

    private void initializeDictionary() {
        String[] words = new String[]{
                "electricity", "water", "internet", "network", "wifi", "hostel",
                "academic", "maintenance", "leakage", "plumbing", "sanitation",
                "security", "transport", "library", "food", "cafeteria", "canteen",
                "infrastructure", "administration", "finance", "fee", "refund",
                "projector", "air conditioner", "elevator", "cleaning", "broken",
                "urgent", "damage", "reimbursement", "grading", "professor", "course"
        };
        for (String w : words) {
            standardDictionary.add(w);
        }
    }

    public static class SearchResponse {
        public final CustomArrayList<Complaint> results;
        public final String typoSuggestion; // e.g. "Did you mean: electricity?" or null
        public final String statusMessage;  // Business message e.g. "Search completed — 5 complaints found."
        public final String internalStrategySelected; // For academic evaluation only

        public SearchResponse(CustomArrayList<Complaint> results, String typoSuggestion, String statusMessage, String internalStrategySelected) {
            this.results = results;
            this.typoSuggestion = typoSuggestion;
            this.statusMessage = statusMessage;
            this.internalStrategySelected = internalStrategySelected;
        }
    }

    /**
     * CO1: Problem Classification & Algorithmic Strategy Selection Engine.
     * Evaluates query length, pattern characteristics, dataset size, and selects optimal strategy.
     */
    public SearchAlgorithm selectOptimalStrategy(String pattern, int textLength) {
        if (pattern == null || pattern.isEmpty()) return kmpAlgorithm;

        int m = pattern.length();
        // Strategy selection heuristics based on computational theory:
        if (m <= 3) {
            // Short patterns have negligible preprocessing advantage
            return naivePatternSearch;
        } else if (m > 15 || textLength > 10000) {
            // Long patterns and texts benefit significantly from rolling hash fingerprinting
            return rabinKarpAlgorithm;
        } else if (pattern.chars().distinct().count() < 3) {
            // Repetitive character patterns perform best with Z-function linear scan
            return zAlgorithm;
        } else {
            // Default deterministic linear time search
            return kmpAlgorithm;
        }
    }

    /**
     * Searches complaints transparently using the chosen algorithm strategy and fuzzy typo fallback.
     */
    public SearchResponse search(CustomArrayList<Complaint> dataset, String query) {
        if (dataset == null || dataset.isEmpty() || query == null || query.trim().isEmpty()) {
            return new SearchResponse(dataset != null ? dataset : new CustomArrayList<>(), null, "Showing all complaints", "Default");
        }

        String cleanQuery = query.trim().toLowerCase();
        String typoSuggestion = null;

        // Check dictionary for possible typo suggestions (Wagner-Fischer DP)
        String closestWord = editDistance.findClosestMatch(cleanQuery, standardDictionary, 2);
        if (closestWord != null && !closestWord.equalsIgnoreCase(cleanQuery)) {
            typoSuggestion = closestWord;
        }

        SearchAlgorithm strategy = selectOptimalStrategy(cleanQuery, dataset.size() * 100);
        CustomArrayList<Complaint> matches = new CustomArrayList<>();

        for (int i = 0; i < dataset.size(); i++) {
            Complaint c = dataset.get(i);
            String fullSearchableText = (c.getTitle() + " " + c.getDescription() + " " +
                    c.getCategory() + " " + c.getLocation() + " " +
                    c.getDepartmentName() + " " + c.getTrackingId() + " " +
                    c.getTags() + " " + c.getCustomerName()).toLowerCase();

            // 1. Exact / Pattern match using selected string search algorithm
            int[] occurrences = strategy.search(fullSearchableText, cleanQuery);
            if (occurrences.length > 0) {
                matches.add(c);
            } else if (typoSuggestion != null) {
                // Check if typo corrected version matches
                int[] typoOccurrences = strategy.search(fullSearchableText, typoSuggestion);
                if (typoOccurrences.length > 0) {
                    matches.add(c);
                }
            } else {
                // Fallback fuzzy token matching with Wagner-Fischer
                String[] tokens = fullSearchableText.split("\\s+");
                for (String t : tokens) {
                    if (t.length() >= 4 && editDistance.computeDistance(t, cleanQuery) <= 1) {
                        matches.add(c);
                        break;
                    }
                }
            }
        }

        String msg = "Search completed — " + matches.size() + " matching complaints found.";
        return new SearchResponse(matches, typoSuggestion, msg, strategy.getName());
    }

    public WagnerFischerEditDistance getEditDistanceEngine() {
        return editDistance;
    }
}
