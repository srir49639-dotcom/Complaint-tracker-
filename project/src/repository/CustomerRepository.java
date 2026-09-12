package repository;

import filehandling.CustomerFileHandler;
import interfaces.Repository;
import model.Customer;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

import java.io.IOException;

public class CustomerRepository implements Repository<Customer, String> {

    private static CustomerRepository instance;
    private final CustomerFileHandler fileHandler;
    private final CustomHashTable<String, Customer> idIndex;
    private final CustomHashTable<String, Customer> usernameIndex;
    private final CustomArrayList<Customer> customersList;

    public CustomerRepository() {
        this.fileHandler = new CustomerFileHandler();
        this.idIndex = new CustomHashTable<>(64);
        this.usernameIndex = new CustomHashTable<>(64);
        this.customersList = new CustomArrayList<>(64);
        reloadFromFile();
    }

    public static synchronized CustomerRepository getInstance() {
        if (instance == null) {
            instance = new CustomerRepository();
        }
        return instance;
    }

    @Override
    public synchronized void add(Customer customer) {
        if (customer == null || customer.getCustomerId() == null) return;
        if (idIndex.containsKey(customer.getCustomerId())) {
            update(customer);
            return;
        }

        customersList.add(customer);
        idIndex.put(customer.getCustomerId(), customer);
        if (customer.getUsername() != null) {
            usernameIndex.put(customer.getUsername().toLowerCase(), customer);
        }

        try {
            fileHandler.append(customer);
        } catch (IOException e) {
            System.err.println("Error appending customer: " + e.getMessage());
        }
    }

    @Override
    public synchronized Customer findById(String id) {
        return (id != null) ? idIndex.get(id) : null;
    }

    public synchronized Customer findByUsername(String username) {
        return (username != null) ? usernameIndex.get(username.trim().toLowerCase()) : null;
    }

    @Override
    public synchronized CustomArrayList<Customer> findAll() {
        CustomArrayList<Customer> copy = new CustomArrayList<>(customersList.size());
        copy.addAll(customersList);
        return copy;
    }

    @Override
    public synchronized boolean update(Customer item) {
        if (item == null || item.getCustomerId() == null) return false;
        Customer existing = idIndex.get(item.getCustomerId());
        if (existing == null) return false;

        int idx = customersList.indexOf(existing);
        if (idx != -1) {
            customersList.set(idx, item);
        }
        idIndex.put(item.getCustomerId(), item);
        if (item.getUsername() != null) {
            usernameIndex.put(item.getUsername().toLowerCase(), item);
        }
        syncToFile();
        return true;
    }

    @Override
    public synchronized boolean deleteById(String id) {
        Customer existing = idIndex.get(id);
        if (existing == null) return false;

        customersList.remove(existing);
        idIndex.remove(id);
        if (existing.getUsername() != null) {
            usernameIndex.remove(existing.getUsername().toLowerCase());
        }
        syncToFile();
        return true;
    }

    @Override
    public synchronized boolean existsById(String id) {
        return id != null && idIndex.containsKey(id);
    }

    public synchronized boolean existsByUsername(String username) {
        return username != null && usernameIndex.containsKey(username.trim().toLowerCase());
    }

    @Override
    public synchronized int count() {
        return customersList.size();
    }

    @Override
    public synchronized void syncToFile() {
        try {
            fileHandler.saveAll(customersList);
        } catch (IOException e) {
            System.err.println("Error syncing customers: " + e.getMessage());
        }
    }

    @Override
    public synchronized void reloadFromFile() {
        try {
            customersList.clear();
            idIndex.clear();
            usernameIndex.clear();
            CustomArrayList<Customer> loaded = fileHandler.loadAll();
            customersList.addAll(loaded);
            for (int i = 0; i < customersList.size(); i++) {
                Customer c = customersList.get(i);
                idIndex.put(c.getCustomerId(), c);
                if (c.getUsername() != null) {
                    usernameIndex.put(c.getUsername().toLowerCase(), c);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }
}
