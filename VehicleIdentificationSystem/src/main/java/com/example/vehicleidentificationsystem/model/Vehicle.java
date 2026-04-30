package com.example.vehicleidentificationsystem.model;

import javafx.beans.property.*;

public class Vehicle extends BaseModel {
    private final IntegerProperty vehicleId          = new SimpleIntegerProperty();
    private final StringProperty  registrationNumber = new SimpleStringProperty();
    private final StringProperty  make               = new SimpleStringProperty();
    private final StringProperty  model              = new SimpleStringProperty();
    private final IntegerProperty year               = new SimpleIntegerProperty();
    private final IntegerProperty ownerId            = new SimpleIntegerProperty();
    private final StringProperty  ownerName          = new SimpleStringProperty();

    public Vehicle() { super(); }

    public Vehicle(int vehicleId, String registrationNumber, String make, String model,
                   int year, int ownerId, String ownerName) {
        super(vehicleId);
        this.vehicleId.set(vehicleId);
        this.registrationNumber.set(registrationNumber);
        this.make.set(make);
        this.model.set(model);
        this.year.set(year);
        this.ownerId.set(ownerId);
        this.ownerName.set(ownerName);
    }

    @Override
    public String getSummary() {
        return getRegistrationNumber() + " | " + getMake() + " " + getModel() + " (" + getYear() + ")";
    }

    @Override
    public String getModuleTag() { return "WORKSHOP"; }

    public IntegerProperty vehicleIdProperty()          { return vehicleId; }
    public StringProperty  registrationNumberProperty() { return registrationNumber; }
    public StringProperty  makeProperty()               { return make; }
    public StringProperty  modelProperty()              { return model; }
    public IntegerProperty yearProperty()               { return year; }
    public IntegerProperty ownerIdProperty()            { return ownerId; }
    public StringProperty  ownerNameProperty()          { return ownerName; }

    public int    getVehicleId()          { return vehicleId.get(); }
    public String getRegistrationNumber() { return registrationNumber.get(); }
    public String getMake()               { return make.get(); }
    public String getModel()              { return model.get(); }
    public int    getYear()               { return year.get(); }
    public int    getOwnerId()            { return ownerId.get(); }
    public String getOwnerName()          { return ownerName.get(); }

    public void setVehicleId(int v)           { vehicleId.set(v); id = v; }
    public void setRegistrationNumber(String v){ registrationNumber.set(v); }
    public void setMake(String v)             { make.set(v); }
    public void setModel(String v)            { model.set(v); }
    public void setYear(int v)                { year.set(v); }
    public void setOwnerId(int v)             { ownerId.set(v); }
    public void setOwnerName(String v)        { ownerName.set(v); }
}