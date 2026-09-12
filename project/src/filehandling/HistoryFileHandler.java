package filehandling;

import interfaces.FileStorage;
import model.ComplaintHistory;
import model.ComplaintStatus;
import model.PriorityLevel;
import datastructures.CustomArrayList;

import java.io.*;

public class HistoryFileHandler implements FileStorage<ComplaintHistory> {

    private static final String FILE_NAME = "complaint_history.txt";
    private static final String DELIMITER = "\t";
    private final File file;

    public HistoryFileHandler() {
        FileManager.initializeDirectories();
        this.file = new File(FileManager.DATA_DIR, FILE_NAME);
    }

    @Override
    public CustomArrayList<ComplaintHistory> loadAll() throws IOException {
        CustomArrayList<ComplaintHistory> list = new CustomArrayList<>();
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                ComplaintHistory h = parseLine(line);
                if (h != null) list.add(h);
            }
        }
        return list;
    }

    @Override
    public synchronized void saveAll(CustomArrayList<ComplaintHistory> items) throws IOException {
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
    public synchronized void append(ComplaintHistory item) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(formatLine(item));
            writer.newLine();
        }
    }

    private String formatLine(ComplaintHistory h) {
        return escape(h.getHistoryId()) + DELIMITER +
                escape(h.getComplaintId()) + DELIMITER +
                escape(h.getAction()) + DELIMITER +
                (h.getPreviousStatus() != null ? h.getPreviousStatus().name() : "") + DELIMITER +
                (h.getNewStatus() != null ? h.getNewStatus().name() : "") + DELIMITER +
                (h.getPreviousPriority() != null ? h.getPreviousPriority().name() : "") + DELIMITER +
                (h.getNewPriority() != null ? h.getNewPriority().name() : "") + DELIMITER +
                escape(h.getPerformedByUserId()) + DELIMITER +
                escape(h.getPerformedByUserName()) + DELIMITER +
                h.getTimestamp() + DELIMITER +
                escape(h.getComment());
    }

    private ComplaintHistory parseLine(String line) {
        try {
            String[] p = line.split(DELIMITER, -1);
            if (p.length < 11) return null;
            return new ComplaintHistory(
                    unescape(p[0]), unescape(p[1]), unescape(p[2]),
                    p[3].isEmpty() ? null : ComplaintStatus.fromString(p[3]),
                    p[4].isEmpty() ? null : ComplaintStatus.fromString(p[4]),
                    p[5].isEmpty() ? null : PriorityLevel.fromString(p[5]),
                    p[6].isEmpty() ? null : PriorityLevel.fromString(p[6]),
                    unescape(p[7]), unescape(p[8]),
                    Long.parseLong(p[9]), unescape(p[10])
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
