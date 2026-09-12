package repository;

import filehandling.DepartmentFileHandler;
import interfaces.Repository;
import model.Department;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

import java.io.IOException;

public class DepartmentRepository implements Repository<Department, String> {

    private static DepartmentRepository instance;
    private final DepartmentFileHandler fileHandler;
    private final CustomHashTable<String, Department> idIndex;
    private final CustomHashTable<String, Department> nameIndex;
    private final CustomArrayList<Department> departmentsList;

    public DepartmentRepository() {
        this.fileHandler = new DepartmentFileHandler();
        this.idIndex = new CustomHashTable<>(32);
        this.nameIndex = new CustomHashTable<>(32);
        this.departmentsList = new CustomArrayList<>(32);
        reloadFromFile();
    }

    public static synchronized DepartmentRepository getInstance() {
        if (instance == null) {
            instance = new DepartmentRepository();
        }
        return instance;
    }

    @Override
    public synchronized void add(Department department) {
        if (department == null || department.getDepartmentId() == null) return;
        if (idIndex.containsKey(department.getDepartmentId())) {
            update(department);
            return;
        }

        departmentsList.add(department);
        idIndex.put(department.getDepartmentId(), department);
        if (department.getName() != null) {
            nameIndex.put(department.getName().toLowerCase(), department);
        }

        try {
            fileHandler.append(department);
        } catch (IOException e) {
            System.err.println("Error appending department: " + e.getMessage());
        }
    }

    @Override
    public synchronized Department findById(String id) {
        return (id != null) ? idIndex.get(id) : null;
    }

    public synchronized Department findByName(String name) {
        return (name != null) ? nameIndex.get(name.trim().toLowerCase()) : null;
    }

    @Override
    public synchronized CustomArrayList<Department> findAll() {
        CustomArrayList<Department> copy = new CustomArrayList<>(departmentsList.size());
        copy.addAll(departmentsList);
        return copy;
    }

    @Override
    public synchronized boolean update(Department item) {
        if (item == null || item.getDepartmentId() == null) return false;
        Department existing = idIndex.get(item.getDepartmentId());
        if (existing == null) return false;

        int idx = departmentsList.indexOf(existing);
        if (idx != -1) {
            departmentsList.set(idx, item);
        }
        idIndex.put(item.getDepartmentId(), item);
        if (item.getName() != null) {
            nameIndex.put(item.getName().toLowerCase(), item);
        }
        syncToFile();
        return true;
    }

    @Override
    public synchronized boolean deleteById(String id) {
        Department existing = idIndex.get(id);
        if (existing == null) return false;

        departmentsList.remove(existing);
        idIndex.remove(id);
        if (existing.getName() != null) {
            nameIndex.remove(existing.getName().toLowerCase());
        }
        syncToFile();
        return true;
    }

    @Override
    public synchronized boolean existsById(String id) {
        return id != null && idIndex.containsKey(id);
    }

    @Override
    public synchronized int count() {
        return departmentsList.size();
    }

    @Override
    public synchronized void syncToFile() {
        try {
            fileHandler.saveAll(departmentsList);
        } catch (IOException e) {
            System.err.println("Error syncing departments: " + e.getMessage());
        }
    }

    @Override
    public synchronized void reloadFromFile() {
        try {
            departmentsList.clear();
            idIndex.clear();
            nameIndex.clear();
            CustomArrayList<Department> loaded = fileHandler.loadAll();
            departmentsList.addAll(loaded);
            for (int i = 0; i < departmentsList.size(); i++) {
                Department d = departmentsList.get(i);
                idIndex.put(d.getDepartmentId(), d);
                if (d.getName() != null) {
                    nameIndex.put(d.getName().toLowerCase(), d);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading departments: " + e.getMessage());
        }
    }
}
