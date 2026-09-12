package filehandling;

import interfaces.FileStorage;
import model.Notification;
import datastructures.CustomArrayList;

import java.io.*;

public class NotificationFileHandler implements FileStorage<Notification> {

    private static final String FILE_NAME = "notifications.txt";
    private static final String DELIMITER = "\t";
    private final File file;

    public NotificationFileHandler() {
        FileManager.initializeDirectories();
        this.file = new File(FileManager.DATA_DIR, FILE_NAME);
    }

    @Override
    public CustomArrayList<Notification> loadAll() throws IOException {
        CustomArrayList<Notification> list = new CustomArrayList<>();
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                Notification n = parseLine(line);
                if (n != null) list.add(n);
            }
        }
        return list;
    }

    @Override
    public synchronized void saveAll(CustomArrayList<Notification> items) throws IOException {
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
    public synchronized void append(Notification item) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(formatLine(item));
            writer.newLine();
        }
    }

    private String formatLine(Notification n) {
        return escape(n.getNotificationId()) + DELIMITER +
                escape(n.getRecipientUserId()) + DELIMITER +
                escape(n.getRecipientRole()) + DELIMITER +
                escape(n.getComplaintId()) + DELIMITER +
                escape(n.getTitle()) + DELIMITER +
                escape(n.getMessage()) + DELIMITER +
                n.getTimestamp() + DELIMITER +
                n.isRead();
    }

    private Notification parseLine(String line) {
        try {
            String[] p = line.split(DELIMITER, -1);
            if (p.length < 8) return null;
            return new Notification(
                    unescape(p[0]), unescape(p[1]), unescape(p[2]), unescape(p[3]),
                    unescape(p[4]), unescape(p[5]),
                    Long.parseLong(p[6]), Boolean.parseBoolean(p[7])
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
