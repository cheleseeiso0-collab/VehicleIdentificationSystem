package com.example.vehicleidentificationsystem.model;

import javafx.beans.property.*;

public class SystemUser extends BaseModel {
    private final IntegerProperty userId   = new SimpleIntegerProperty();
    private final StringProperty  username = new SimpleStringProperty();
    private final StringProperty  password = new SimpleStringProperty();
    private final StringProperty  role     = new SimpleStringProperty(); // ADMIN, POLICE, etc.
    private final StringProperty  fullName = new SimpleStringProperty();
    private final BooleanProperty active   = new SimpleBooleanProperty(true);

    public SystemUser() { super(); }

    public SystemUser(int userId, String username, String password,
                      String role, String fullName, boolean active) {
        super(userId);
        this.userId.set(userId);
        this.username.set(username);
        this.password.set(password);
        this.role.set(role);
        this.fullName.set(fullName);
        this.active.set(active);
    }

    @Override
    public String getSummary() {
        return "User #" + getUserId() + " | " + getUsername() + " | Role: " + getRole();
    }

    @Override
    public String getModuleTag() { return "ADMIN"; }

    public IntegerProperty userIdProperty()   { return userId; }
    public StringProperty  usernameProperty() { return username; }
    public StringProperty  passwordProperty() { return password; }
    public StringProperty  roleProperty()     { return role; }
    public StringProperty  fullNameProperty() { return fullName; }
    public BooleanProperty activeProperty()   { return active; }

    public int     getUserId()   { return userId.get(); }
    public String  getUsername() { return username.get(); }
    public String  getPassword() { return password.get(); }
    public String  getRole()     { return role.get(); }
    public String  getFullName() { return fullName.get(); }
    public boolean isActive()    { return active.get(); }

    public void setUserId(int v)    { userId.set(v); id = v; }
    public void setUsername(String v){ username.set(v); }
    public void setPassword(String v){ password.set(v); }
    public void setRole(String v)   { role.set(v); }
    public void setFullName(String v){ fullName.set(v); }
    public void setActive(boolean v){ active.set(v); }
}