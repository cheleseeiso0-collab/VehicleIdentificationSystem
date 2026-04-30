package com.example.vehicleidentificationsystem.model;

import javafx.beans.property.*;

public class Customer extends BaseModel {
    private final IntegerProperty customerId = new SimpleIntegerProperty();
    private final StringProperty  name       = new SimpleStringProperty();
    private final StringProperty  address    = new SimpleStringProperty();
    private final StringProperty  phone      = new SimpleStringProperty();
    private final StringProperty  email      = new SimpleStringProperty();

    public Customer() { super(); }

    public Customer(int customerId, String name, String address, String phone, String email) {
        super(customerId);
        this.customerId.set(customerId);
        this.name.set(name);
        this.address.set(address);
        this.phone.set(phone);
        this.email.set(email);
    }

    @Override
    public String getSummary() {
        return "Customer #" + getCustomerId() + " | " + getName() + " | " + getPhone();
    }

    @Override
    public String getModuleTag() { return "CUSTOMER"; }


    public IntegerProperty customerIdProperty() { return customerId; }
    public StringProperty  nameProperty()       { return name; }
    public StringProperty  addressProperty()    { return address; }
    public StringProperty  phoneProperty()      { return phone; }
    public StringProperty  emailProperty()      { return email; }

    public int    getCustomerId() { return customerId.get(); }
    public String getName()       { return name.get(); }
    public String getAddress()    { return address.get(); }
    public String getPhone()      { return phone.get(); }
    public String getEmail()      { return email.get(); }

    public void setCustomerId(int v) { customerId.set(v); id = v; }
    public void setName(String v)    { name.set(v); }
    public void setAddress(String v) { address.set(v); }
    public void setPhone(String v)   { phone.set(v); }
    public void setEmail(String v)   { email.set(v); }
}