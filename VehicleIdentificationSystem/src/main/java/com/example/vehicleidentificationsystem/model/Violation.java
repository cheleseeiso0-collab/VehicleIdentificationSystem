package com.example.vehicleidentificationsystem.model;

import javafx.beans.property.*;

public class Violation extends BaseModel {
    private final IntegerProperty violationId   = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId     = new SimpleIntegerProperty();
    private final StringProperty  violationDate = new SimpleStringProperty();
    private final StringProperty  violationType = new SimpleStringProperty();
    private final DoubleProperty  fineAmount    = new SimpleDoubleProperty();
    private final StringProperty  status        = new SimpleStringProperty(); // Paid / Unpaid

    public Violation() { super(); }

    public Violation(int violationId, int vehicleId, String violationDate,
                     String violationType, double fineAmount, String status) {
        super(violationId);
        this.violationId.set(violationId);
        this.vehicleId.set(vehicleId);
        this.violationDate.set(violationDate);
        this.violationType.set(violationType);
        this.fineAmount.set(fineAmount);
        this.status.set(status);
    }

    @Override
    public String getSummary() {
        return "Violation #" + getViolationId() + " | " + getViolationType()
                + " | M" + String.format("%.2f", getFineAmount()) + " | " + getStatus();
    }

    @Override
    public String getModuleTag() { return "POLICE"; }

    public IntegerProperty violationIdProperty()   { return violationId; }
    public IntegerProperty vehicleIdProperty()     { return vehicleId; }
    public StringProperty  violationDateProperty() { return violationDate; }
    public StringProperty  violationTypeProperty() { return violationType; }
    public DoubleProperty  fineAmountProperty()    { return fineAmount; }
    public StringProperty  statusProperty()        { return status; }

    public int    getViolationId()   { return violationId.get(); }
    public int    getVehicleId()     { return vehicleId.get(); }
    public String getViolationDate() { return violationDate.get(); }
    public String getViolationType() { return violationType.get(); }
    public double getFineAmount()    { return fineAmount.get(); }
    public String getStatus()        { return status.get(); }

    public void setViolationId(int v)    { violationId.set(v); id = v; }
    public void setVehicleId(int v)      { vehicleId.set(v); }
    public void setViolationDate(String v){ violationDate.set(v); }
    public void setViolationType(String v){ violationType.set(v); }
    public void setFineAmount(double v)  { fineAmount.set(v); }
    public void setStatus(String v)      { status.set(v); }
}