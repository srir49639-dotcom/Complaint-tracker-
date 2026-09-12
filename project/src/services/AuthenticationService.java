package services;

import model.Customer;
import model.Staff;
import model.User;
import model.UserRole;
import repository.CustomerRepository;
import repository.StaffRepository;
import utils.IdGenerator;
import utils.PasswordHasher;

public class AuthenticationService {

    private static AuthenticationService instance;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private User currentSessionUser;

    public AuthenticationService() {
        this.customerRepository = CustomerRepository.getInstance();
        this.staffRepository = StaffRepository.getInstance();
    }

    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public User authenticateCustomer(String username, String plainPassword) {
        if (username == null || plainPassword == null) return null;
        Customer c = customerRepository.findByUsername(username);
        if (c != null && PasswordHasher.verifyPassword(plainPassword, c.getPasswordHash())) {
            this.currentSessionUser = c;
            return c;
        }
        return null;
    }

    public User authenticateStaffOrAdmin(String username, String plainPassword) {
        if (username == null || plainPassword == null) return null;
        Staff s = staffRepository.findByUsername(username);
        if (s != null && PasswordHasher.verifyPassword(plainPassword, s.getPasswordHash())) {
            this.currentSessionUser = s;
            return s;
        }
        // Check default admin fallback
        if (username.equalsIgnoreCase("admin") && (plainPassword.equals("admin123") || PasswordHasher.verifyPassword(plainPassword, PasswordHasher.hashPassword("admin123")))) {
            Staff admin = new Staff("ADMIN-01", "admin", PasswordHasher.hashPassword("admin123"), "System Administrator", "admin@smarttracker.org", "+1-800-ADMIN", "DPT-01", "Administration", "Management", Staff.StaffStatus.AVAILABLE, 0, 100, 5.0, UserRole.ORGANIZATION_ADMIN);
            this.currentSessionUser = admin;
            return admin;
        }
        return null;
    }

    public Customer registerCustomer(String fullName, String username, String plainPassword, String email, String phone, String address) {
        if (customerRepository.existsByUsername(username)) {
            return null; // Username already taken
        }

        String cid = IdGenerator.generateCustomerId();
        String hash = PasswordHasher.hashPassword(plainPassword);
        Customer customer = new Customer(cid, username, hash, fullName, email, phone, address, System.currentTimeMillis());

        customerRepository.add(customer);
        return customer;
    }

    public User getCurrentSessionUser() {
        return currentSessionUser;
    }

    public void logout() {
        this.currentSessionUser = null;
    }
}
