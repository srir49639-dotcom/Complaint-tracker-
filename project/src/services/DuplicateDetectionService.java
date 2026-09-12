package services;

import algorithms.dp.WagnerFischerEditDistance;
import algorithms.string.RabinKarpAlgorithm;
import model.Complaint;
import datastructures.CustomArrayList;

public class DuplicateDetectionService {

    private static DuplicateDetectionService instance;
    private final WagnerFischerEditDistance editDistance = new WagnerFischerEditDistance();
    private final RabinKarpAlgorithm rabinKarp = new RabinKarpAlgorithm();

    public static synchronized DuplicateDetectionService getInstance() {
        if (instance == null) {
            instance = new DuplicateDetectionService();
        }
        return instance;
    }

    public static class SimilarityMatch {
        public final Complaint existingComplaint;
        public final double similarityScore; // 0.0 to 1.0
        public final String similarityTier;  // "Very High", "High", "Medium"

        public SimilarityMatch(Complaint existingComplaint, double similarityScore, String similarityTier) {
            this.existingComplaint = existingComplaint;
            this.similarityScore = similarityScore;
            this.similarityTier = similarityTier;
        }
    }

    /**
     * Identifies potential duplicate or strongly similar complaints in the system.
     */
    public CustomArrayList<SimilarityMatch> findSimilarComplaints(Complaint target, CustomArrayList<Complaint> existingComplaints, double threshold) {
        CustomArrayList<SimilarityMatch> results = new CustomArrayList<>();
        if (target == null || existingComplaints == null || existingComplaints.isEmpty()) {
            return results;
        }

        String targetText = (target.getTitle() + " " + target.getDescription()).trim().toLowerCase();

        for (int i = 0; i < existingComplaints.size(); i++) {
            Complaint existing = existingComplaints.get(i);
            if (existing.getComplaintId().equals(target.getComplaintId())) continue;

            // Must match same or related category / location to be relevant
            boolean categoryMatch = target.getCategory().equalsIgnoreCase(existing.getCategory());
            boolean locationMatch = target.getLocation() != null && existing.getLocation() != null &&
                    (target.getLocation().equalsIgnoreCase(existing.getLocation()) ||
                            target.getLocation().toLowerCase().contains(existing.getLocation().toLowerCase()) ||
                            existing.getLocation().toLowerCase().contains(target.getLocation().toLowerCase()));

            if (!categoryMatch && !locationMatch) continue;

            String existingText = (existing.getTitle() + " " + existing.getDescription()).trim().toLowerCase();

            // Calculate similarity score via Wagner-Fischer edit distance
            double sim = editDistance.computeSimilarity(targetText, existingText);

            // Also check title overlap
            double titleSim = editDistance.computeSimilarity(target.getTitle().toLowerCase(), existing.getTitle().toLowerCase());
            double combinedScore = Math.max(sim, titleSim * 0.8 + sim * 0.2);

            if (combinedScore >= threshold) {
                String tier = "Medium";
                if (combinedScore >= 0.80) {
                    tier = "Very High";
                } else if (combinedScore >= 0.60) {
                    tier = "High";
                }
                results.add(new SimilarityMatch(existing, combinedScore, tier));
            }
        }

        // Sort highest similarity first
        results.sort((m1, m2) -> Double.compare(m2.similarityScore, m1.similarityScore));
        return results;
    }
}
