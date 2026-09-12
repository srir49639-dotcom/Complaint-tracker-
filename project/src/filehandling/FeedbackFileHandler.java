package filehandling;

import interfaces.FileStorage;
import model.Feedback;
import datastructures.CustomArrayList;

import java.io.*;

public class FeedbackFileHandler implements FileStorage<Feedback> {

    private static final String FILE_NAME = "feedback.txt";
    private static final String DELIMITER = "\t";
    private final File file;

    public FeedbackFileHandler() {
        FileManager.initializeDirectories();
        this.file = new File(FileManager.DATA_DIR, FILE_NAME);
    }

    @Override
    public CustomArrayList<Feedback> loadAll() throws IOException {
        CustomArrayList<Feedback> list = new CustomArrayList<>();
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                Feedback f = parseLine(line);
                if (f != null) list.add(f);
            }
        }
        return list;
    }

    @Override
    public synchronized void saveAll(CustomArrayList<Feedback> items) throws IOException {
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
    public synchronized void append(Feedback item) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(formatLine(item));
            writer.newLine();
        }
    }

    private String formatLine(Feedback f) {
        return escape(f.getFeedbackId()) + DELIMITER +
                escape(f.getComplaintId()) + DELIMITER +
                escape(f.getCustomerId()) + DELIMITER +
                f.getRating() + DELIMITER +
                escape(f.getComments()) + DELIMITER +
                f.isSatisfactionFlag() + DELIMITER +
                f.getSubmittedTimestamp();
    }

    private Feedback parseLine(String line) {
        try {
            String[] p = line.split(DELIMITER, -1);
            if (p.length < 7) return null;
            return new Feedback(
                    unescape(p[0]), unescape(p[1]), unescape(p[2]),
                    Integer.parseInt(p[3]), unescape(p[4]),
                    Boolean.parseBoolean(p[5]), Long.parseLong(p[6])
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
