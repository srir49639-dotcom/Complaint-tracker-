package interfaces;

import datastructures.CustomArrayList;
import java.io.IOException;

public interface FileStorage<T> {
    CustomArrayList<T> loadAll() throws IOException;
    void saveAll(CustomArrayList<T> items) throws IOException;
    void append(T item) throws IOException;
    String getFilePath();
    long getFileSize();
    long getLastModified();
    int getRecordCount();
}
