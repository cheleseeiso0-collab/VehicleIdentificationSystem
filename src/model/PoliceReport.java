package model;

public class PoliceReport extends BaseEntity {
    private int    vehicleId;
    private String reportDate;
    private String reportType;
    private String description;
    private String officerName;

    public PoliceReport() {}
    public PoliceReport(int id, int vehicleId, String reportDate,
                        String reportType, String description, String officerName) {
        this.id = id; this.vehicleId = vehicleId;
        this.reportDate = reportDate; this.reportType = reportType;
        this.description = description; this.officerName = officerName;
    }

    public int    getVehicleId()   { return vehicleId; }
    public String getReportDate()  { return reportDate; }
    public String getReportType()  { return reportType; }
    public String getDescription() { return description; }
    public String getOfficerName() { return officerName; }

    public void setVehicleId(int v)      { vehicleId = v; }
    public void setReportDate(String v)  { reportDate = v; }
    public void setReportType(String v)  { reportType = v; }
    public void setDescription(String v) { description = v; }
    public void setOfficerName(String v) { officerName = v; }

    @Override public String getDisplayLabel() {
        return reportDate + " — " + reportType + " | Officer: " + officerName;
    }
}
