package model;

public class Violation extends BaseEntity {
    private int    vehicleId;
    private String violationDate;
    private String violationType;
    private double fineAmount;
    private String status;

    public Violation() {}
    public Violation(int id, int vehicleId, String violationDate,
                     String violationType, double fineAmount, String status) {
        this.id = id; this.vehicleId = vehicleId;
        this.violationDate = violationDate; this.violationType = violationType;
        this.fineAmount = fineAmount; this.status = status;
    }

    public int    getVehicleId()     { return vehicleId; }
    public String getViolationDate() { return violationDate; }
    public String getViolationType() { return violationType; }
    public double getFineAmount()    { return fineAmount; }
    public String getStatus()        { return status; }

    public void setVehicleId(int v)       { vehicleId = v; }
    public void setViolationDate(String v){ violationDate = v; }
    public void setViolationType(String v){ violationType = v; }
    public void setFineAmount(double v)   { fineAmount = v; }
    public void setStatus(String v)       { status = v; }

    @Override public String getDisplayLabel() {
        return violationDate + " — " + violationType +
               " | M " + String.format("%,.2f", fineAmount) + " [" + status + "]";
    }
}
