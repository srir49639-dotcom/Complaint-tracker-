package tests;

public class ComprehensiveTestSuite {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("            SMART COMPLAINT TRACKER — COMPREHENSIVE VERIFICATION SUITE          ");
        System.out.println("================================================================================");

        try {
            DSATests.runAll();
            AlgorithmsTestSuite.runAll();
            PersistenceAndRestartTest.runAll();

            System.out.println("================================================================================");
            System.out.println("   >>> ALL SYSTEMS VERIFIED: 100% PASS RATE ACROSS ALL CO1-CO6 MODULES <<<   ");
            System.out.println("================================================================================");
        } catch (Throwable t) {
            System.err.println("\n[FAILED] Test failure detected: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }
}
