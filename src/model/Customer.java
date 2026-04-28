package model;

public class Customer extends BaseEntity {
    private String name;
    private String address;
    private String phone;
    private String email;

    public Customer() {}
    public Customer(int id, String name, String address, String phone, String email) {
        this.id = id; this.name = name; this.address = address;
        this.phone = phone; this.email = email;
    }

    public String getName()    { return name; }
    public String getAddress() { return address; }
    public String getPhone()   { return phone; }
    public String getEmail()   { return email; }

    public void setName(String v)    { name = v; }
    public void setAddress(String v) { address = v; }
    public void setPhone(String v)   { phone = v; }
    public void setEmail(String v)   { email = v; }

    @Override public String getDisplayLabel() {
        return name + (phone != null && !phone.isEmpty() ? "  |  " + phone : "");
    }
}
