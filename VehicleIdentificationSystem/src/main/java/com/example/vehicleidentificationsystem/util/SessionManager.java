package com.example.vehicleidentificationsystem.util;

import com.example.vehicleidentificationsystem.model.SystemUser;

public class SessionManager {
    private static SessionManager instance;
    private SystemUser currentUser;

    private SessionManager() {}
    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void setCurrentUser(SystemUser user) { this.currentUser = user; }
    public SystemUser getCurrentUser() { return currentUser; }
    public boolean isLoggedIn() { return currentUser != null; }
    public String getRole() { return currentUser != null ? currentUser.getRole() : "NONE"; }
    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(getRole()); }
    public void logout() { currentUser = null; }
}