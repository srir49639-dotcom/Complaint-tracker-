package filehandling;

import interfaces.FileStorage;
import model.Staff;
import model.Staff.StaffStatus;
import model.UserRole;
import datastructures.CustomArrayList;

import java.io.*;

public class StaffFileHandler implements FileStorage<Staff> {

    private static final String FILE_NAME = "staff.txt";
    private static final String DELIMITER = "\t";
    private final File file;

    public StaffFileHandler() {
        FileManager.initializeDirectories();
        this.file = new File(FileManager.DATA_DIR, FILE_NAME);
    }

    @Override
    public CustomArrayList<Staff> loadAll() throws IOException {
        CustomArrayList<Staff> list = new CustomArrayList<>();
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                Staff s = parseLine(line);
                if (s != null) list.add(s);
            }
        }
        return list;
    }

    @Override
    public synchronized void saveAll(CustomArrayList<Staff> items) throws IOException {
        File temp = new File(FileManager.DATA_DIR, FILE_NAME + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            for (int i = 0; i < items.size(); i++) {
                writer.write(formatLine(items.get(i)));
                writer.newLine();
            }
        }
        if (file.exists()) file.delete();
        temp.renameTo(file);
    }

    @Override
    public synchronized void append(Staff item) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(formatLine(item));
            writer.newLine();
        }
    }

    private String formatLine(Staff s) {
        return escape(s.getStaffId()) + DELIMITER +
                escape(s.getUsername()) + DELIMITER +
                escape(s.getPasswordHash()) + DELIMITER +
                escape(s.getFullName()) + DELIMITER +
                escape(s.getEmail()) + DELIMITER +
                escape(s.getPhone()) + DELIMITER +
                escape(s.getDepartmentId()) + DELIMITER +
                escape(s.getDepartmentName()) + DELIMITER +
                escape(s.getSpecialization()) + DELIMITER +
                s.getStatus().name() + DELIMITER +
                s.getCurrentWorkload() + DELIMITER +
                s.getMaxWorkload() + DELIMITER +
                s.getEfficiencyRating() + DELIMITER +
                s.getRole().name();
    }

    private Staff parseLine(String line) {
        try {
            String[] p = line.split(DELIMITER, -1);
            if (p.length < 14) return null;
            return new Staff(
                    unescape(p[0]), unescape(p[1]), unescape(p[2]), unescape(p[3]),
                    unescape(p[4]), unescape(p[5]), unescape(p[6]), unescape(p[7]),
                    unescape(p[8]), StaffStatus.valueOf(p[9]),
                    Integer.parseInt(p[10]), Integer.parseInt(p[11]),
                    Double.parseDouble(p[12]), UserRole.fromString(p[13])
            );
        } catch (Exception e) {
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
        try { return loadAll().size(); } catch (IOException e) { return 0; }
    }
}
