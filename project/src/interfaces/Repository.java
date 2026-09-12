package interfaces;

import datastructures.CustomArrayList;

public interface Repository<T, ID> {
    void add(T item);
    T findById(ID id);
    CustomArrayList<T> findAll();
    boolean update(T item);
    boolean deleteById(ID id);
    boolean existsById(ID id);
    int count();
    void syncToFile();
    void reloadFromFile();
}
