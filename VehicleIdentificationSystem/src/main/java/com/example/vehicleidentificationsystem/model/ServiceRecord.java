package com.example.vehicleidentificationsystem.model;

import javafx.beans.property.*;

public class ServiceRecord extends BaseModel {
    private final IntegerProperty serviceId   = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId   = new SimpleIntegerProperty();
    private final StringProperty  serviceDate = new SimpleStringProperty();
    private final StringProperty  serviceType = new SimpleStringProperty();
    private final StringProperty  description = new SimpleStringProperty();
    private final DoubleProperty  cost        = new SimpleDoubleProperty();

    public ServiceRecord() { super(); }

    public ServiceRecord(int serviceId, int vehicleId, String serviceDate,
                         String serviceType, String description, double cost) {
        super(serviceId);
        this.serviceId.set(serviceId);
        this.vehicleId.set(vehicleId);
        this.serviceDate.set(serviceDate);
        this.serviceType.set(serviceType);
        this.description.set(description);
        this.cost.set(cost);
    }

    @Override public String getSummary() {
        return "Service #" + getServiceId() + " | " + getServiceType() + " | " + getServiceDate();
    }
    @Override public String getModuleTag() { return "WORKSHOP"; }

    public IntegerProperty serviceIdProperty()   { return serviceId; }
    public IntegerProperty vehicleIdProperty()   { return vehicleId; }
    public StringProperty  serviceDateProperty() { return serviceDate; }
    public StringProperty  serviceTypeProperty() { return serviceType; }
    public StringProperty  descriptionProperty() { return description; }
    public DoubleProperty  costProperty()        { return cost; }

    public int    getServiceId()   { return serviceId.get(); }
    public int    getVehicleId()   { return vehicleId.get(); }
    public String getServiceDate() { return serviceDate.get(); }
    public String getServiceType() { return serviceType.get(); }
    public String getDescription() { return description.get(); }
    public double getCost()        { return cost.get(); }

    public void setServiceId(int v)    { serviceId.set(v); id = v; }
    public void setVehicleId(int v)    { vehicleId.set(v); }
    public void setServiceDate(String v){ serviceDate.set(v); }
    public void setServiceType(String v){ serviceType.set(v); }
    public void setDescription(String v){ description.set(v); }
    public void setCost(double v)      { cost.set(v); }
}