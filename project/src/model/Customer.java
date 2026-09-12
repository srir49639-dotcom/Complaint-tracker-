package model;

public class Customer extends User {
    private static final long serialVersionUID = 1L;

    private String address;
    private long registeredTimestamp;

    public Customer() {
        super();
        this.role = UserRole.CUSTOMER;
    }

    public Customer(String customerId, String username, String passwordHash, String fullName, String email, String phone, String address, long registeredTimestamp) {
        super(customerId, username, passwordHash, fullName, email, phone, UserRole.CUSTOMER);
        this.address = address;
        this.registeredTimestamp = registeredTimestamp;
    }

    public String getCustomerId() { return getUserId(); }
    public void setCustomerId(String customerId) { setUserId(customerId); }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public long getRegisteredTimestamp() { return registeredTimestamp; }
    public void setRegisteredTimestamp(long registeredTimestamp) { this.registeredTimestamp = registeredTimestamp; }
}
