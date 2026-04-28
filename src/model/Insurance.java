package model;

public class Insurance extends BaseEntity {
    private int    vehicleId;
    private String provider;
    private String policyNumber;
    private String startDate;
    private String expiryDate;
    private String coverageType;
    private double premiumAmount;
    private String status;

    public Insurance() {}
    public Insurance(int id, int vehicleId, String provider, String policyNumber,
                     String startDate, String expiryDate, String coverageType,
                     double premiumAmount, String status) {
        this.id = id; this.vehicleId = vehicleId; this.provider = provider;
        this.policyNumber = policyNumber; this.startDate = startDate;
        this.expiryDate = expiryDate; this.coverageType = coverageType;
        this.premiumAmount = premiumAmount; this.status = status;
    }

    public int    getVehicleId()     { return vehicleId; }
    public String getProvider()      { return provider; }
    public String getPolicyNumber()  { return policyNumber; }
    public String getStartDate()     { return startDate; }
    public String getExpiryDate()    { return expiryDate; }
    public String getCoverageType()  { return coverageType; }
    public double getPremiumAmount() { return premiumAmount; }
    public String getStatus()        { return status; }

    public void setVehicleId(int v)      { vehicleId = v; }
    public void setProvider(String v)    { provider = v; }
    public void setPolicyNumber(String v){ policyNumber = v; }
    public void setStartDate(String v)   { startDate = v; }
    public void setExpiryDate(String v)  { expiryDate = v; }
    public void setCoverageType(String v){ coverageType = v; }
    public void setPremiumAmount(double v){ premiumAmount = v; }
    public void setStatus(String v)      { status = v; }

    @Override public String getDisplayLabel() {
        return provider + " | " + policyNumber + " | " + coverageType +
               " | Expires: " + expiryDate + " [" + status + "]";
    }
}
