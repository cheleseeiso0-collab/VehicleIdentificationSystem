package com.example.vehicleidentificationsystem.model;

public abstract class BaseModel {
    protected int id;

    public BaseModel() {}
    public BaseModel(int id) { this.id = id; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public abstract String getSummary();
    public abstract String getModuleTag();

    @Override
    public String toString() {
        return "[" + getModuleTag() + "] " + getSummary();
    }
}