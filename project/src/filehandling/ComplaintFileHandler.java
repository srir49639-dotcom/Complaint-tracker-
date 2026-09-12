package filehandling;

import interfaces.FileStorage;
import model.Complaint;
import model.ComplaintStatus;
import model.EscalationLevel;
import model.PriorityLevel;
import datastructures.CustomArrayList;

import java.io.*;

public class ComplaintFileHandler implements FileStorage<Complaint> {

    private static final String FILE_NAME = "complaints.txt";
    private static final String DELIMITER = "\t";
    private final File file;

    public ComplaintFileHandler() {
        FileManager.initializeDirectories();
        this.file = new File(FileManager.DATA_DIR, FILE_NAME);
    }

    @Override
    public CustomArrayList<Complaint> loadAll() throws IOException {
        CustomArrayList<Complaint> list = new CustomArrayList<>();
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                Complaint cmp = parseLine(line);
                if (cmp != null) {
                    list.add(cmp);
                }
            }
        }
        return list;
    }

    @Override
    public synchronized void saveAll(CustomArrayList<Complaint> items) throws IOException {
        File tempFile = new File(FileManager.DATA_DIR, FILE_NAME + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            for (int i = 0; i < items.size(); i++) {
                writer.write(formatLine(items.get(i)));
                writer.newLine();
            }
        }

        // Atomic replace
        if (file.exists()) {
            file.delete();
        }
        tempFile.renameTo(file);
    }

    @Override
    public synchronized void append(Complaint item) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(formatLine(item));
            writer.newLine();
        }
    }

    private String formatLine(Complaint c) {
        return escape(c.getComplaintId()) + DELIMITER +
                escape(c.getTrackingId()) + DELIMITER +
                escape(c.getCustomerId()) + DELIMITER +
                escape(c.getCustomerName()) + DELIMITER +
                escape(c.getCustomerEmail()) + DELIMITER +
                escape(c.getCustomerPhone()) + DELIMITER +
                escape(c.getTitle()) + DELIMITER +
                escape(c.getDescription()) + DELIMITER +
                escape(c.getCategory()) + DELIMITER +
                escape(c.getLocation()) + DELIMITER +
                c.getPriority().name() + DELIMITER +
                c.getStatus().name() + DELIMITER +
                escape(c.getDepartmentId()) + DELIMITER +
                escape(c.getDepartmentName()) + DELIMITER +
                escape(c.getAssignedStaffId()) + DELIMITER +
                escape(c.getAssignedStaffName()) + DELIMITER +
                escape(c.getTags()) + DELIMITER +
                escape(c.getSupportingInfo()) + DELIMITER +
                c.getCreatedTimestamp() + DELIMITER +
                c.getUpdatedTimestamp() + DELIMITER +
                c.getExpectedResolutionTimestamp() + DELIMITER +
                c.getActualResolvedTimestamp() + DELIMITER +
                c.getEscalationLevel().getLevel() + DELIMITER +
                c.getReopenCount() + DELIMITER +
                c.getDynamicPriorityScore() + DELIMITER +
                c.getEstimatedEffortHours();
    }

    private Complaint parseLine(String line) {
        try {
            String[] parts = line.split(DELIMITER, -1);
            if (parts.length < 26) return null;

            String complaintId = unescape(parts[0]);
            String trackingId = unescape(parts[1]);
            String customerId = unescape(parts[2]);
            String customerName = unescape(parts[3]);
            String customerEmail = unescape(parts[4]);
            String customerPhone = unescape(parts[5]);
            String title = unescape(parts[6]);
            String description = unescape(parts[7]);
            String category = unescape(parts[8]);
            String location = unescape(parts[9]);
            PriorityLevel priority = PriorityLevel.fromString(parts[10]);
            ComplaintStatus status = ComplaintStatus.fromString(parts[11]);
            String departmentId = unescape(parts[12]);
            String departmentName = unescape(parts[13]);
            String assignedStaffId = unescape(parts[14]);
            String assignedStaffName = unescape(parts[15]);
            String tags = unescape(parts[16]);
            String supportingInfo = unescape(parts[17]);
            long createdTimestamp = Long.parseLong(parts[18]);
            long updatedTimestamp = Long.parseLong(parts[19]);
            long expectedResolutionTimestamp = Long.parseLong(parts[20]);
            long actualResolvedTimestamp = Long.parseLong(parts[21]);
            EscalationLevel escalationLevel = EscalationLevel.fromInt(Integer.parseInt(parts[22]));
            int reopenCount = Integer.parseInt(parts[23]);
            double dynamicPriorityScore = Double.parseDouble(parts[24]);
            int estimatedEffortHours = Integer.parseInt(parts[25]);

            return new Complaint(complaintId, trackingId, customerId, customerName,
                    customerEmail, customerPhone, title, description, category, location,
                    priority, status, departmentId, departmentName, assignedStaffId, assignedStaffName,
                    tags, supportingInfo, createdTimestamp, updatedTimestamp,
                    expectedResolutionTimestamp, actualResolvedTimestamp, escalationLevel,
                    reopenCount, dynamicPriorityScore, estimatedEffortHours);
        } catch (Exception e) {
            // Graceful handling of corrupted/invalid records
            return null;
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
    }

    private String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t").replace("\\\\", "\\");
    }

    @Override
    public String getFilePath() { return file.getAbsolutePath(); }
    @Override
    public long getFileSize() { return file.exists() ? file.length() : 0; }
    @Override
    public long getLastModified() { return file.exists() ? file.lastModified() : 0; }
    @Override
    public int getRecordCount() {
        try {
            return loadAll().size();
        } catch (IOException e) {
            return 0;
        }
    }
}
