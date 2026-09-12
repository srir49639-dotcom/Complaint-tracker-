package filehandling;

import datastructures.CustomArrayList;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupManager {

    public static class BackupInfo {
        public final String folderName;
        public final String fullPath;
        public final long timestamp;
        public final int fileCount;

        public BackupInfo(String folderName, String fullPath, long timestamp, int fileCount) {
            this.folderName = folderName;
            this.fullPath = fullPath;
            this.timestamp = timestamp;
            this.fileCount = fileCount;
        }
    }

    public static String createBackup() throws IOException {
        FileManager.initializeDirectories();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File backupSubFolder = new File(FileManager.BACKUP_DIR, "backup_" + timeStamp);
        if (!backupSubFolder.exists()) {
            backupSubFolder.mkdirs();
        }

        File dataFolder = new File(FileManager.DATA_DIR);
        File[] files = dataFolder.listFiles();
        int copied = 0;

        if (files != null) {
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(".txt")) {
                    File dest = new File(backupSubFolder, f.getName());
                    Files.copy(f.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    copied++;
                }
            }
        }

        return backupSubFolder.getAbsolutePath();
    }

    public static boolean restoreBackup(String backupFolderName) throws IOException {
        File backupSubFolder = new File(FileManager.BACKUP_DIR, backupFolderName);
        if (!backupSubFolder.exists() || !backupSubFolder.isDirectory()) {
            return false;
        }

        File[] files = backupSubFolder.listFiles();
        if (files != null) {
            File dataFolder = new File(FileManager.DATA_DIR);
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(".txt")) {
                    File dest = new File(dataFolder, f.getName());
                    Files.copy(f.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        return true;
    }

    public static CustomArrayList<BackupInfo> listBackups() {
        FileManager.initializeDirectories();
        CustomArrayList<BackupInfo> list = new CustomArrayList<>();
        File backupDir = new File(FileManager.BACKUP_DIR);
        File[] subDirs = backupDir.listFiles();

        if (subDirs != null) {
            for (File d : subDirs) {
                if (d.isDirectory() && d.getName().startsWith("backup_")) {
                    File[] txtFiles = d.listFiles((dir, name) -> name.endsWith(".txt"));
                    int count = txtFiles != null ? txtFiles.length : 0;
                    list.add(new BackupInfo(d.getName(), d.getAbsolutePath(), d.lastModified(), count));
                }
            }
        }

        // Sort latest first
        list.sort((b1, b2) -> Long.compare(b2.timestamp, b1.timestamp));
        return list;
    }
}
