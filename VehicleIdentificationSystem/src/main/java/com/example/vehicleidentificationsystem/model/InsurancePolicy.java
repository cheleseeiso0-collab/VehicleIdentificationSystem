package com.example.vehicleidentificationsystem.model;

import javafx.beans.property.*;

public class InsurancePolicy extends BaseModel {
    private final IntegerProperty policyId     = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId    = new SimpleIntegerProperty();
    private final StringProperty  provider     = new SimpleStringProperty();
    private final StringProperty  policyNumber = new SimpleStringProperty();
    private final StringProperty  startDate    = new SimpleStringProperty();
    private final StringProperty  expiryDate   = new SimpleStringProperty();
    private final StringProperty  coverageType = new SimpleStringProperty();
    private final DoubleProperty  premium      = new SimpleDoubleProperty();

    public InsurancePolicy() { super(); }

    public InsurancePolicy(int policyId, int vehicleId, String provider,
                           String policyNumber, String startDate,
                           String expiryDate, String coverageType, double premium) {
        super(policyId);
        this.policyId.set(policyId);
        this.vehicleId.set(vehicleId);
        this.provider.set(provider);
        this.policyNumber.set(policyNumber);
        this.startDate.set(startDate);
        this.expiryDate.set(expiryDate);
        this.coverageType.set(coverageType);
        this.premium.set(premium);
    }

    @Override public String getSummary() {
        return "Policy number" + getPolicyId() + " | " + getProvider() + " | " + getPolicyNumber();
    }
    @Override public String getModuleTag() { return "INSURANCE"; }

    public IntegerProperty policyIdProperty()     { return policyId; }
    public IntegerProperty vehicleIdProperty()    { return vehicleId; }
    public StringProperty  providerProperty()     { return provider; }
    public StringProperty  policyNumberProperty() { return policyNumber; }
    public StringProperty  startDateProperty()    { return startDate; }
    public StringProperty  expiryDateProperty()   { return expiryDate; }
    public StringProperty  coverageTypeProperty() { return coverageType; }
    public DoubleProperty  premiumProperty()      { return premium; }

    public int    getPolicyId()     { return policyId.get(); }
    public int    getVehicleId()    { return vehicleId.get(); }
    public String getProvider()     { return provider.get(); }
    public String getPolicyNumber() { return policyNumber.get(); }
    public String getStartDate()    { return startDate.get(); }
    public String getExpiryDate()   { return expiryDate.get(); }
    public String getCoverageType() { return coverageType.get(); }
    public double getPremium()      { return premium.get(); }

    public void setPolicyId(int v)     { policyId.set(v); id = v; }
    public void setVehicleId(int v)    { vehicleId.set(v); }
    public void setProvider(String v)  { provider.set(v); }
    public void setPolicyNumber(String v) { policyNumber.set(v); }
    public void setStartDate(String v)    { startDate.set(v); }
    public void setExpiryDate(String v)   { expiryDate.set(v); }
    public void setCoverageType(String v) { coverageType.set(v); }
    public void setPremium(double v)      { premium.set(v); }
}