package services;

import algorithms.string.AhoCorasickAlgorithm;
import algorithms.string.KMPAlgorithm;
import algorithms.string.RabinKarpAlgorithm;
import algorithms.string.ZAlgorithm;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;
import model.Complaint;
import model.PriorityLevel;
import model.ComplaintStatus;

public class PatternSegregationService {

    private static PatternSegregationService instance;

    private final KMPAlgorithm kmpAlgorithm = new KMPAlgorithm();
    private final RabinKarpAlgorithm rabinKarpAlgorithm = new RabinKarpAlgorithm();
    private final ZAlgorithm zAlgorithm = new ZAlgorithm();

    public static synchronized PatternSegregationService getInstance() {
        if (instance == null) {
            instance = new PatternSegregationService();
        }
        return instance;
    }

    public static class CategoryStat {
        public final String categoryName;
        public int count;
        public double percentage;
        public int newOrOpenCount;
        public int inProgressCount;
        public int resolvedCount;
        public int criticalHighCount;

        public CategoryStat(String categoryName) {
            this.categoryName = categoryName;
            this.count = 0;
            this.percentage = 0.0;
            this.newOrOpenCount = 0;
            this.inProgressCount = 0;
            this.resolvedCount = 0;
            this.criticalHighCount = 0;
        }
    }

    public static class PatternCluster {
        public final String clusterName;
        public final String icon;
        public final String[] keywords;
        public final CustomArrayList<Complaint> matchingComplaints;

        public PatternCluster(String clusterName, String icon, String[] keywords) {
            this.clusterName = clusterName;
            this.icon = icon;
            this.keywords = keywords;
            this.matchingComplaints = new CustomArrayList<>();
        }
    }

    /**
     * Segregates all complaints strictly by Category and generates analytical statistics.
     */
    public CustomArrayList<CategoryStat> getCategoryBreakdown(CustomArrayList<Complaint> complaints) {
        CustomHashTable<String, CategoryStat> map = new CustomHashTable<>(32);
        CustomArrayList<String> categoryOrder = new CustomArrayList<>();

        if (complaints == null || complaints.isEmpty()) {
            return new CustomArrayList<>();
        }

        int total = complaints.size();

        for (int i = 0; i < complaints.size(); i++) {
            Complaint c = complaints.get(i);
            String cat = c.getCategory() != null && !c.getCategory().trim().isEmpty() ? c.getCategory().trim() : "Uncategorized";

            CategoryStat stat = map.get(cat);
            if (stat == null) {
                stat = new CategoryStat(cat);
                map.put(cat, stat);
                categoryOrder.add(cat);
            }

            stat.count++;

            if (c.getStatus() == ComplaintStatus.NEW || c.getStatus() == ComplaintStatus.ASSIGNED || c.getStatus() == ComplaintStatus.REOPENED) {
                stat.newOrOpenCount++;
            } else if (c.getStatus() == ComplaintStatus.IN_PROGRESS || c.getStatus() == ComplaintStatus.WAITING_FOR_CUSTOMER || c.getStatus() == ComplaintStatus.ESCALATED) {
                stat.inProgressCount++;
            } else if (c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED) {
                stat.resolvedCount++;
            }

            if (c.getPriority() == PriorityLevel.CRITICAL || c.getPriority() == PriorityLevel.HIGH) {
                stat.criticalHighCount++;
            }
        }

        CustomArrayList<CategoryStat> results = new CustomArrayList<>();
        for (int i = 0; i < categoryOrder.size(); i++) {
            CategoryStat stat = map.get(categoryOrder.get(i));
            if (stat != null) {
                stat.percentage = total > 0 ? ((double) stat.count / total) * 100.0 : 0.0;
                results.add(stat);
            }
        }

        // Sort descending by count
        results.sort((a, b) -> Integer.compare(b.count, a.count));
        return results;
    }

    /**
     * Filters complaints belonging to a specific category.
     */
    public CustomArrayList<Complaint> getComplaintsByCategory(CustomArrayList<Complaint> complaints, String category) {
        CustomArrayList<Complaint> list = new CustomArrayList<>();
        if (complaints == null) return list;

        for (int i = 0; i < complaints.size(); i++) {
            Complaint c = complaints.get(i);
            if (category == null || category.equalsIgnoreCase("All Categories") ||
                    (c.getCategory() != null && c.getCategory().equalsIgnoreCase(category))) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Discovers and segregates complaints into semantic issue clusters using Aho-Corasick Multi-Pattern Trie Matching.
     */
    public CustomArrayList<PatternCluster> getPredefinedPatternClusters(CustomArrayList<Complaint> complaints) {
        CustomArrayList<PatternCluster> clusters = new CustomArrayList<>();

        clusters.add(new PatternCluster("Wi-Fi & Network Outages", "🌐", new String[]{
                "wifi", "wi-fi", "router", "network", "internet", "signal", "disconnect", "packet loss", "dns", "lan", "bandwidth", "ethernet"
        }));

        clusters.add(new PatternCluster("Water Leaks & Pipe Damage", "💧", new String[]{
                "leak", "leakage", "pipe", "tap", "drain", "water", "clog", "flush", "plumb", "basin", "faucet", "overflow", "sewage"
        }));

        clusters.add(new PatternCluster("Power Cuts & Electrical Faults", "⚡", new String[]{
                "power", "electricity", "outage", "spark", "socket", "short circuit", "voltage", "switch", "mcb", "fuse", "blackout", "tripped"
        }));

        clusters.add(new PatternCluster("Air Conditioning & Cooling", "❄️", new String[]{
                "ac", "air conditioner", "cooling", "thermostat", "blower", "compressor", "heat", "ventilation", "chiller"
        }));

        clusters.add(new PatternCluster("Classroom, Projector & AV", "📽️", new String[]{
                "projector", "mic", "microphone", "speaker", "whiteboard", "hdmi", "podium", "audio", "display", "screen", "marker"
        }));

        clusters.add(new PatternCluster("Hostel & Room Maintenance", "🛏️", new String[]{
                "bed", "mattress", "geyser", "door", "lock", "window", "wardrobe", "curtain", "room", "cupboard", "almirah", "key"
        }));

        clusters.add(new PatternCluster("Fee, Billing & Refund Glitches", "💳", new String[]{
                "fee", "refund", "receipt", "payment", "transaction", "scholarship", "fine", "dues", "challan", "overcharged", "portal"
        }));

        clusters.add(new PatternCluster("Security, CCTV & Infrastructure", "🔒", new String[]{
                "cctv", "camera", "gate", "guard", "elevator", "lift", "stairs", "cracked", "pothole", "roof", "corridor", "boundary"
        }));

        clusters.add(new PatternCluster("Food, Cafeteria & Hygiene", "🍲", new String[]{
                "food", "canteen", "cafeteria", "hygiene", "mess", "taste", "insect", "unclean", "stale", "water cooler"
        }));

        if (complaints == null || complaints.isEmpty()) {
            return clusters;
        }

        // For each cluster, build Aho-Corasick automaton and match across all complaints
        for (int cIdx = 0; cIdx < clusters.size(); cIdx++) {
            PatternCluster cluster = clusters.get(cIdx);
            CustomArrayList<String> dict = new CustomArrayList<>();
            for (String kw : cluster.keywords) {
                dict.add(kw);
            }

            AhoCorasickAlgorithm matcher = new AhoCorasickAlgorithm(dict);

            for (int i = 0; i < complaints.size(); i++) {
                Complaint comp = complaints.get(i);
                String textToSearch = (comp.getTitle() + " " + comp.getDescription() + " " + comp.getCategory() + " " + comp.getLocation()).toLowerCase();

                CustomArrayList<AhoCorasickAlgorithm.MatchResult> matches = matcher.searchAll(textToSearch);
                if (!matches.isEmpty()) {
                    cluster.matchingComplaints.add(comp);
                }
            }
        }

        // Filter out empty clusters and sort by count
        CustomArrayList<PatternCluster> activeClusters = new CustomArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            PatternCluster cl = clusters.get(i);
            if (!cl.matchingComplaints.isEmpty()) {
                activeClusters.add(cl);
            }
        }
        activeClusters.sort((a, b) -> Integer.compare(b.matchingComplaints.size(), a.matchingComplaints.size()));
        return activeClusters;
    }

    /**
     * Performs exact live pattern matching across complaints using user-selected algorithm (KMP, Rabin-Karp, Z-Algorithm, Aho-Corasick).
     */
    public CustomArrayList<Complaint> segregateByCustomPattern(CustomArrayList<Complaint> complaints, String rawPattern, String algorithmChoice) {
        CustomArrayList<Complaint> matches = new CustomArrayList<>();
        if (complaints == null || rawPattern == null || rawPattern.trim().isEmpty()) {
            return complaints != null ? complaints : matches;
        }

        String pattern = rawPattern.trim().toLowerCase();

        for (int i = 0; i < complaints.size(); i++) {
            Complaint comp = complaints.get(i);
            String fullText = (comp.getTitle() + " " + comp.getDescription() + " " + comp.getCategory() + " " + (comp.getLocation() != null ? comp.getLocation() : "")).toLowerCase();

            boolean found = false;

            if ("Knuth-Morris-Pratt (KMP)".equalsIgnoreCase(algorithmChoice) || "Exact Substring Match".equalsIgnoreCase(algorithmChoice)) {
                int[] hits = kmpAlgorithm.search(fullText, pattern);
                found = hits.length > 0;
            } else if ("Rabin-Karp Rolling Hash".equalsIgnoreCase(algorithmChoice) || "Extended Phrase Match".equalsIgnoreCase(algorithmChoice)) {
                int[] hits = rabinKarpAlgorithm.search(fullText, pattern);
                found = hits.length > 0;
            } else if ("Z-Algorithm Linear Scan".equalsIgnoreCase(algorithmChoice) || "Fast Linear Scan".equalsIgnoreCase(algorithmChoice)) {
                int[] hits = zAlgorithm.search(fullText, pattern);
                found = hits.length > 0;
            } else {
                // Aho-Corasick Multi-Keyword Match
                String[] tokens = pattern.split("[,\\s]+");
                CustomArrayList<String> dict = new CustomArrayList<>();
                for (String t : tokens) {
                    if (!t.trim().isEmpty()) dict.add(t.trim());
                }
                if (dict.isEmpty()) {
                    found = fullText.contains(pattern);
                } else {
                    AhoCorasickAlgorithm ac = new AhoCorasickAlgorithm(dict);
                    found = !ac.searchAll(fullText).isEmpty();
                }
            }

            if (found) {
                matches.add(comp);
            }
        }

        return matches;
    }
}
