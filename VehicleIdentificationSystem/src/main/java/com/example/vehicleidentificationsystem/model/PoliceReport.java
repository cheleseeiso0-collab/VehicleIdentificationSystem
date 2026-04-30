package com.example.vehicleidentificationsystem.model;

import javafx.beans.property.*;

public class PoliceReport extends BaseModel {
    private final IntegerProperty reportId    = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId   = new SimpleIntegerProperty();
    private final StringProperty  reportDate  = new SimpleStringProperty();
    private final StringProperty  reportType  = new SimpleStringProperty();
    private final StringProperty  description = new SimpleStringProperty();
    private final StringProperty  officerName = new SimpleStringProperty();

    public PoliceReport() { super(); }

    public PoliceReport(int reportId, int vehicleId, String reportDate,
                        String reportType, String description, String officerName) {
        super(reportId);
        this.reportId.set(reportId);
        this.vehicleId.set(vehicleId);
        this.reportDate.set(reportDate);
        this.reportType.set(reportType);
        this.description.set(description);
        this.officerName.set(officerName);
    }

    @Override
    public String getSummary() {
        return "Report #" + getReportId() + " | " + getReportType()
                + " | " + getReportDate() + " | Ofc. " + getOfficerName();
    }

    @Override
    public String getModuleTag() { return "POLICE"; }

    public IntegerProperty reportIdProperty()    { return reportId; }
    public IntegerProperty vehicleIdProperty()   { return vehicleId; }
    public StringProperty  reportDateProperty()  { return reportDate; }
    public StringProperty  reportTypeProperty()  { return reportType; }
    public StringProperty  descriptionProperty() { return description; }
    public StringProperty  officerNameProperty() { return officerName; }

    public int    getReportId()    { return reportId.get(); }
    public int    getVehicleId()   { return vehicleId.get(); }
    public String getReportDate()  { return reportDate.get(); }
    public String getReportType()  { return reportType.get(); }
    public String getDescription() { return description.get(); }
    public String getOfficerName() { return officerName.get(); }

    public void setReportId(int v)     { reportId.set(v); id = v; }
    public void setVehicleId(int v)    { vehicleId.set(v); }
    public void setReportDate(String v){ reportDate.set(v); }
    public void setReportType(String v){ reportType.set(v); }
    public void setDescription(String v){ description.set(v); }
    public void setOfficerName(String v){ officerName.set(v); }
}