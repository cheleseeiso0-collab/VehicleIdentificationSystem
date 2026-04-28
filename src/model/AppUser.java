package model;

public class AppUser extends BaseEntity {
    private String  username;
    private String  role;
    private boolean active;

    public AppUser() {}
    public AppUser(int id, String username, String role, boolean active) {
        this.id = id; this.username = username;
        this.role = role; this.active = active;
    }

    public String  getUsername() { return username; }
    public String  getRole()     { return role; }
    public boolean isActive()    { return active; }

    public void setUsername(String v) { username = v; }
    public void setRole(String v)     { role = v; }
    public void setActive(boolean v)  { active = v; }

    @Override public String getDisplayLabel() {
        return "@" + username + " [" + role + "]" + (active ? "" : " — disabled");
    }
}
