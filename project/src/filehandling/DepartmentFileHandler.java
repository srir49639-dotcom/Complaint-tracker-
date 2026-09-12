package filehandling;

import interfaces.FileStorage;
import model.Department;
import datastructures.CustomArrayList;

import java.io.*;

public class DepartmentFileHandler implements FileStorage<Department> {

    private static final String FILE_NAME = "departments.txt";
    private static final String DELIMITER = "\t";
    private final File file;

    public DepartmentFileHandler() {
        FileManager.initializeDirectories();
        this.file = new File(FileManager.DATA_DIR, FILE_NAME);
    }

    @Override
    public CustomArrayList<Department> loadAll() throws IOException {
        CustomArrayList<Department> list = new CustomArrayList<>();
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                Department d = parseLine(line);
                if (d != null) list.add(d);
            }
        }
        return list;
    }

    @Override
    public synchronized void saveAll(CustomArrayList<Department> items) throws IOException {
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
    public synchronized void append(Department item) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(formatLine(item));
            writer.newLine();
        }
    }

    private String formatLine(Department d) {
        return escape(d.getDepartmentId()) + DELIMITER +
                escape(d.getName()) + DELIMITER +
                escape(d.getDescription()) + DELIMITER +
                escape(d.getHeadStaffId()) + DELIMITER +
                escape(d.getHeadName()) + DELIMITER +
                d.getCapacity() + DELIMITER +
                d.getActiveComplaints() + DELIMITER +
                d.getDefaultSlaHours() + DELIMITER +
                escape(d.getParentDepartmentId());
    }

    private Department parseLine(String line) {
        try {
            String[] p = line.split(DELIMITER, -1);
            if (p.length < 9) return null;
            return new Department(
                    unescape(p[0]), unescape(p[1]), unescape(p[2]),
                    unescape(p[3]), unescape(p[4]),
                    Integer.parseInt(p[5]), Integer.parseInt(p[6]),
                    Integer.parseInt(p[7]), unescape(p[8])
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
