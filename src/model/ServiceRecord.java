package model;

public class ServiceRecord extends BaseEntity {
    private int    vehicleId;
    private String serviceDate;
    private String serviceType;
    private String description;
    private double cost;

    public ServiceRecord() {}
    public ServiceRecord(int id, int vehicleId, String serviceDate,
                         String serviceType, String description, double cost) {
        this.id = id; this.vehicleId = vehicleId;
        this.serviceDate = serviceDate; this.serviceType = serviceType;
        this.description = description; this.cost = cost;
    }

    public int    getVehicleId()   { return vehicleId; }
    public String getServiceDate() { return serviceDate; }
    public String getServiceType() { return serviceType; }
    public String getDescription() { return description; }
    public double getCost()        { return cost; }

    public void setVehicleId(int v)      { vehicleId = v; }
    public void setServiceDate(String v) { serviceDate = v; }
    public void setServiceType(String v) { serviceType = v; }
    public void setDescription(String v) { description = v; }
    public void setCost(double v)        { cost = v; }

    @Override public String getDisplayLabel() {
        return serviceDate + " — " + serviceType + " | M " + String.format("%,.2f", cost);
    }
}
