package utils;

import model.*;
import model.Staff.StaffStatus;
import repository.*;
import services.PriorityEngineService;
import datastructures.CustomArrayList;

import java.io.File;

public class SampleDataGenerator {

    public static synchronized void generateSampleDataIfEmpty() {
        DepartmentRepository deptRepo = DepartmentRepository.getInstance();
        StaffRepository staffRepo = StaffRepository.getInstance();
        CustomerRepository custRepo = CustomerRepository.getInstance();
        ComplaintRepository cmpRepo = ComplaintRepository.getInstance();
        HistoryRepository histRepo = HistoryRepository.getInstance();
        ResponseRepository respRepo = ResponseRepository.getInstance();
        FeedbackRepository feedRepo = FeedbackRepository.getInstance();

        if (cmpRepo.count() >= 50) {
            return; // Data already exists
        }

        // 1. Create 12 Departments
        String[][] depts = new String[][]{
                {"DPT-01", "Administration", "General campus administration and executive operations", "Dr. Vance", "50", "48"},
                {"DPT-02", "Internet / Wi-Fi", "Network infrastructure, routers, and campus internet", "Eng. David", "60", "24"},
                {"DPT-03", "Electricity / Power", "Power distribution, substations, lighting, and wiring", "Eng. Marcus", "50", "12"},
                {"DPT-04", "Water & Plumbing", "Plumbing pipelines, water purification, and sanitation", "Eng. Walter", "50", "24"},
                {"DPT-05", "Hostel Facilities", "Hostel room maintenance, furniture, and resident welfare", "Warden Sarah", "80", "48"},
                {"DPT-06", "Academic / Classroom", "Lecture halls, smart boards, projector equipment, grading", "Dean Thompson", "60", "36"},
                {"DPT-07", "Finance & Fees", "Tuition fees, scholarship disbursement, and accounts", "Officer Rachel", "40", "72"},
                {"DPT-08", "Security & Access", "Campus security, gate access control, CCTV surveillance", "Capt. Miller", "50", "8"},
                {"DPT-09", "Transport / Bus", "Campus shuttles, transit scheduling, and fleet maintenance", "Supervisor Paul", "40", "24"},
                {"DPT-10", "Library Services", "Digital catalogue, book returns, reading room facilities", "Librarian Elena", "40", "48"},
                {"DPT-11", "Food Services / Canteen", "Dining hall hygiene, meal quality, and cafeteria vending", "Chef Anthony", "50", "12"},
                {"DPT-12", "Infrastructure & Maintenance", "Civil works, elevators, HVAC air conditioning, roofing", "Eng. Brian", "70", "48"}
        };

        for (String[] d : depts) {
            Department dept = new Department(d[0], d[1], d[2], "", d[3], Integer.parseInt(d[4]), 0, Integer.parseInt(d[5]), "DPT-01");
            deptRepo.add(dept);
        }

        // 2. Create Staff Members
        String[][] staffData = new String[][]{
                {"STF-101", "david_it", "David Kumar", "david@smarttracker.org", "+1-555-0101", "DPT-02", "Internet / Wi-Fi", "Network Routing & Switches", "AVAILABLE", "2", "8", "4.8"},
                {"STF-102", "marcus_elec", "Marcus Vance", "marcus@smarttracker.org", "+1-555-0102", "DPT-03", "Electricity / Power", "High Voltage & Backup Generators", "AVAILABLE", "3", "8", "4.7"},
                {"STF-103", "walter_plumb", "Walter White", "walter@smarttracker.org", "+1-555-0103", "DPT-04", "Water & Plumbing", "Pipe Leakage & Drainage", "AVAILABLE", "1", "8", "4.9"},
                {"STF-104", "sarah_hostel", "Sarah Jenkins", "sarah@smarttracker.org", "+1-555-0104", "DPT-05", "Hostel Facilities", "Room Allotment & Infrastructure", "AVAILABLE", "4", "8", "4.6"},
                {"STF-105", "elena_lib", "Elena Rostova", "elena@smarttracker.org", "+1-555-0105", "DPT-10", "Library Services", "Digital Repository & Book Loans", "AVAILABLE", "1", "6", "4.9"},
                {"STF-106", "anthony_food", "Anthony Bourdain", "anthony@smarttracker.org", "+1-555-0106", "DPT-11", "Food Services / Canteen", "Hygiene Inspection & Kitchen Ops", "AVAILABLE", "2", "8", "4.5"},
                {"STF-107", "brian_civil", "Brian O'Connor", "brian@smarttracker.org", "+1-555-0107", "DPT-12", "Infrastructure & Maintenance", "HVAC, Air Conditioning & Elevators", "AVAILABLE", "3", "8", "4.8"},
                {"STF-108", "rachel_fin", "Rachel Green", "rachel@smarttracker.org", "+1-555-0108", "DPT-07", "Finance & Fees", "Fee Reconciliation & Refunds", "AVAILABLE", "2", "6", "4.7"}
        };

        for (String[] s : staffData) {
            Staff st = new Staff(s[0], s[1], PasswordHasher.hashPassword("staff123"), s[2], s[3], s[4],
                    s[5], s[6], s[7], StaffStatus.valueOf(s[8]), Integer.parseInt(s[9]),
                    Integer.parseInt(s[10]), Double.parseDouble(s[11]), UserRole.STAFF);
            staffRepo.add(st);
        }

        // 3. Create Customers
        String[][] custData = new String[][]{
                {"CUST-01", "john_doe", "John Doe", "john@university.edu", "+1-555-1101", "Hostel Block A, Room 302"},
                {"CUST-02", "alice_smith", "Alice Smith", "alice@university.edu", "+1-555-1102", "Hostel Block B, Room 104"},
                {"CUST-03", "robert_brown", "Robert Brown", "robert@university.edu", "+1-555-1103", "Hostel Block C, Room 215"},
                {"CUST-04", "emily_clark", "Emily Clark", "emily@university.edu", "+1-555-1104", "Engineering Block 2, Lab 4"},
                {"CUST-05", "michael_scott", "Michael Scott", "michael@university.edu", "+1-555-1105", "Management Tower, Office 12"}
        };

        for (String[] c : custData) {
            Customer cust = new Customer(c[0], c[1], PasswordHasher.hashPassword("cust123"), c[2], c[3], c[4], c[5], System.currentTimeMillis() - (30L * 86400000L));
            custRepo.add(cust);
        }

        // 4. Generate 100+ Realistic Complaints with Repeated Keywords for Search, Fuzzy Matching & Duplicates
        String[] sampleTitles = new String[]{
                "Wi-Fi connection continuously dropping in Hostel Block A",
                "High latency and intermittent network outage in library reading hall",
                "Water leakage in 3rd floor bathroom near corridor 4",
                "Water pressure extremely low in hostel block B washrooms",
                "Power socket burning smell and flickering electricity in classroom 204",
                "Air conditioner not cooling in auditorium hall B",
                "Elevator stuck between floor 2 and 3 in Engineering tower",
                "Broken window latch and cold draft in hostel block A room 302",
                "Mess food quality deteriorated and cold meals served in dinner canteen",
                "Tuition fee double deduction occurred during online payment portal transaction",
                "Projector display flickering with green tint in Lecture Hall 3",
                "Campus shuttle bus delayed by over 45 minutes on route 4",
                "Library digital portal book renewal button returning error code 500",
                "Water pipe leakage near electrical distribution board in basement",
                "Ceiling fan making loud clicking noise in hostel block C",
                "Broken study table and damaged chair in library 2nd floor",
                "Cafeteria drinking water dispenser filter expired and smelling foul",
                "Streetlight not working along north pathway near security gate 2",
                "CCTV camera offline near main administration vehicle parking lot",
                "WiFi access point authentication failing for student portal credentials"
        };

        String[] sampleCategories = new String[]{
                "Internet / Wi-Fi", "Internet / Wi-Fi", "Water & Plumbing", "Water & Plumbing",
                "Electricity / Power", "Infrastructure & Maintenance", "Infrastructure & Maintenance",
                "Hostel Facilities", "Food Services / Canteen", "Finance & Fees",
                "Academic / Classroom", "Transport / Bus", "Library Services",
                "Water & Plumbing", "Hostel Facilities", "Library Services",
                "Food Services / Canteen", "Electricity / Power", "Security & Access", "Internet / Wi-Fi"
        };

        String[] locations = new String[]{
                "Hostel Block A", "Central Library", "Hostel Block B", "Hostel Block C",
                "Engineering Block", "Main Auditorium", "Science Tower", "Dining Hall Mess",
                "Administration Block", "North Campus Gateway", "Management Building", "Sports Complex"
        };

        PriorityLevel[] prios = new PriorityLevel[]{
                PriorityLevel.HIGH, PriorityLevel.MEDIUM, PriorityLevel.CRITICAL, PriorityLevel.LOW,
                PriorityLevel.CRITICAL, PriorityLevel.MEDIUM, PriorityLevel.HIGH, PriorityLevel.LOW
        };

        ComplaintStatus[] statuses = new ComplaintStatus[]{
                ComplaintStatus.NEW, ComplaintStatus.ASSIGNED, ComplaintStatus.IN_PROGRESS,
                ComplaintStatus.RESOLVED, ComplaintStatus.CLOSED, ComplaintStatus.ESCALATED,
                ComplaintStatus.WAITING_FOR_CUSTOMER, ComplaintStatus.REOPENED
        };

        PriorityEngineService prioEngine = PriorityEngineService.getInstance();
        long now = System.currentTimeMillis();

        for (int i = 1; i <= 105; i++) {
            int idx = (i - 1) % sampleTitles.length;
            String cid = "CMP-" + (100 + i);
            String tid = "CMP-2026-" + String.format("%06d", 100 + i);

            int custIdx = (i - 1) % custData.length;
            String custId = custData[custIdx][0];
            String custName = custData[custIdx][2];
            String custEmail = custData[custIdx][3];
            String custPhone = custData[custIdx][4];

            String title = sampleTitles[idx] + " [Unit " + (i % 15 + 1) + "]";
            String category = sampleCategories[idx];
            String loc = locations[i % locations.length];
            PriorityLevel prio = prios[i % prios.length];
            ComplaintStatus st = statuses[i % statuses.length];

            String deptName = category;
            String staffId = "";
            String staffName = "";

            if (st != ComplaintStatus.NEW) {
                int sIdx = (i - 1) % staffData.length;
                staffId = staffData[sIdx][0];
                staffName = staffData[sIdx][2];
            }

            long created = now - ((long) (105 - i) * 6L * 3600L * 1000L); // spread across past 25 days
            long updated = created + (3600L * 1000L * 4);
            long deadline = created + (prio.getSlaHours() * 3600L * 1000L);
            long resolvedTime = (st == ComplaintStatus.RESOLVED || st == ComplaintStatus.CLOSED) ? (created + (12L * 3600L * 1000L)) : 0;

            EscalationLevel esc = (st == ComplaintStatus.ESCALATED) ? EscalationLevel.LEVEL_1 : EscalationLevel.LEVEL_0;
            int reopenCnt = (st == ComplaintStatus.REOPENED) ? 1 : 0;
            int effort = (i % 6) + 2;

            String desc = "Detailed incident report regarding " + title.toLowerCase() + ". The issue occurred at " + loc + " causing inconvenience to residents and staff members. Immediate inspection and resolution requested.";

            Complaint cmp = new Complaint(
                    cid, tid, custId, custName, custEmail, custPhone,
                    title, desc, category, loc, prio, st,
                    "", deptName, staffId, staffName,
                    "urgent, maintenance, " + loc.toLowerCase().replace(" ", "-"),
                    "Reported via campus web desk.", created, updated, deadline, resolvedTime,
                    esc, reopenCnt, 50.0, effort
            );

            cmp.setDynamicPriorityScore(prioEngine.calculateDynamicScore(cmp));
            cmpRepo.add(cmp);

            // Add history
            histRepo.add(new ComplaintHistory("HST-" + i, cid, "COMPLAINT_SUBMITTED", null, ComplaintStatus.NEW, null, prio, custId, custName, created, "Initial ticket creation."));
            if (st != ComplaintStatus.NEW) {
                histRepo.add(new ComplaintHistory("HST-" + (i + 500), cid, "STATUS_UPDATE", ComplaintStatus.NEW, st, prio, prio, "STAFF-AUTO", "System Coordinator", updated, "Status updated to " + st.getDisplayName()));
            }

            // Add response
            if (i % 3 == 0) {
                respRepo.add(new ComplaintResponse("RSP-" + i, cid, custId, custName, UserRole.CUSTOMER, "Could you please confirm the estimated arrival time for inspection?", created + 7200000L, false));
                respRepo.add(new ComplaintResponse("RSP-" + (i + 1000), cid, "STF-101", "David Kumar", UserRole.STAFF, "Our technician has received the ticket and is scheduled to inspect today afternoon.", created + 14400000L, false));
            }

            // Add feedback for resolved items
            if (st == ComplaintStatus.RESOLVED || st == ComplaintStatus.CLOSED) {
                int rating = (i % 2 == 0) ? 5 : 4;
                feedRepo.add(new Feedback("FDB-" + i, cid, custId, rating, "Prompt response and clean resolution. Thank you!", true, resolvedTime + 3600000L));
            }
        }
    }
}
