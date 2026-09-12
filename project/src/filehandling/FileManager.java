package filehandling;

import datastructures.CustomArrayList;
import java.io.File;
import java.io.IOException;

public class FileManager {
    public static final String DATA_DIR = "data";
    public static final String BACKUP_DIR = "backup";

    public static void initializeDirectories() {
        File dataFolder = new File(DATA_DIR);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File backupFolder = new File(BACKUP_DIR);
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }
    }

    public static class FileInfo {
        public final String fileName;
        public final String filePath;
        public final long sizeBytes;
        public final long lastModified;
        public final int recordCount;
        public final String status;

        public FileInfo(String fileName, String filePath, long sizeBytes, long lastModified, int recordCount, String status) {
            this.fileName = fileName;
            this.filePath = filePath;
            this.sizeBytes = sizeBytes;
            this.lastModified = lastModified;
            this.recordCount = recordCount;
            this.status = status;
        }
    }

    public static CustomArrayList<FileInfo> getAllDataFilesInfo() {
        initializeDirectories();
        CustomArrayList<FileInfo> list = new CustomArrayList<>();

        String[] files = new String[]{
                "complaints.txt", "customers.txt", "staff.txt", "departments.txt",
                "complaint_history.txt", "responses.txt", "assignments.txt",
                "feedback.txt", "notifications.txt", "users.txt", "analytics.txt", "settings.txt"
        };

        for (String fName : files) {
            File f = new File(DATA_DIR, fName);
            long sz = f.exists() ? f.length() : 0;
            long mod = f.exists() ? f.lastModified() : 0;
            int count = 0;
            if (f.exists()) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f))) {
                    while (br.readLine() != null) count++;
                } catch (IOException ignored) {}
            }
            String st = f.exists() ? "Active / Healthy" : "Missing / Initializing";
            list.add(new FileInfo(fName, f.getAbsolutePath(), sz, mod, count, st));
        }

        return list;
    }
}
