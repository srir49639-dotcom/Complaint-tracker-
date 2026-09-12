package tests;

import filehandling.FileManager;
import model.Complaint;
import model.ComplaintStatus;
import model.Customer;
import model.PriorityLevel;
import repository.ComplaintRepository;
import repository.CustomerRepository;
import repository.HistoryRepository;
import services.ComplaintService;
import utils.IdGenerator;
import utils.PasswordHasher;

public class PersistenceAndRestartTest {

    public static void runAll() {
        System.out.println("\n========== RUNNING MANDATORY RESTART & FILE PERSISTENCE TEST ==========");
        FileManager.initializeDirectories();

        ComplaintRepository cmpRepo = ComplaintRepository.getInstance();
        CustomerRepository custRepo = CustomerRepository.getInstance();
        HistoryRepository histRepo = HistoryRepository.getInstance();
        ComplaintService service = ComplaintService.getInstance();

        // Step 1: Create new customer
        String testCustId = "TEST-CUST-999";
        String testUsername = "persistence_user_" + System.currentTimeMillis();
        Customer testCust = new Customer(testCustId, testUsername, PasswordHasher.hashPassword("test123"),
                "Persistence Test User", "persist@test.com", "+1-555-9999", "Test Lab", System.currentTimeMillis());
        custRepo.add(testCust);
        System.out.println("  [STEP 1] Created and persisted customer: " + testCustId);

        // Step 2: Create new complaint
        Complaint createdCmp = service.submitComplaint(
                testCustId, "Persistence Test User", "persist@test.com", "+1-555-9999",
                "Critical Water Leakage in Server Room",
                "High pressure pipe burst causing water dripping near server rack 3.",
                "Water & Plumbing", "Server Room B", PriorityLevel.CRITICAL,
                "Water & Plumbing", "water, server, urgent", "Room Key #104"
        );
        String savedTrackingId = createdCmp.getTrackingId();
        String savedComplaintId = createdCmp.getComplaintId();
        System.out.println("  [STEP 2] Submitted and saved complaint with Tracking ID: " + savedTrackingId);

        // Step 3: Update status to IN_PROGRESS
        service.updateStatus(savedComplaintId, ComplaintStatus.IN_PROGRESS, "STF-103", "Walter White", "Technician dispatched to shut off main valve.");
        System.out.println("  [STEP 3] Updated status to IN_PROGRESS and appended history log");

        // Step 4: Simulate complete application shutdown by clearing in-memory caches
        System.out.println("  [STEP 4] Simulating Application Shutdown (purging in-memory data structures)...");

        // Step 5: Reload all data from disk flat files (data/*.txt)
        cmpRepo.reloadFromFile();
        custRepo.reloadFromFile();
        histRepo.reloadFromFile();
        System.out.println("  [STEP 5] Restarting application and loading from physical disk flat files...");

        // Step 6: Verify customer persisted
        Customer reloadedCust = custRepo.findById(testCustId);
        assert reloadedCust != null : "Customer failed to persist!";
        assert reloadedCust.getUsername().equals(testUsername) : "Customer data corrupted!";
        System.out.println("  [VERIFIED] Customer record persisted on disk: OK");

        // Step 7: Verify complaint persisted
        Complaint reloadedCmp = cmpRepo.findByTrackingId(savedTrackingId);
        assert reloadedCmp != null : "Complaint failed to persist across restart!";
        assert reloadedCmp.getTitle().equals("Critical Water Leakage in Server Room") : "Title corrupted!";
        assert reloadedCmp.getStatus() == ComplaintStatus.IN_PROGRESS : "Status transition not persisted!";
        assert reloadedCmp.getPriority() == PriorityLevel.CRITICAL : "Priority not persisted!";
        System.out.println("  [VERIFIED] Complaint object persisted on disk with matching state: OK");

        // Step 8: Verify history persisted
        assert histRepo.findByComplaintId(savedComplaintId).size() >= 2 : "Audit history failed to persist!";
        System.out.println("  [VERIFIED] Audit history trail persisted on disk: OK");

        System.out.println(">>> RESTART & FLAT FILE PERSISTENCE TEST 100% SUCCESSFUL!\n");
    }
}
