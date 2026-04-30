package com.example.vehicleidentificationsystem.model;

import javafx.beans.property.*;

public class CustomerQuery extends BaseModel {
    private final IntegerProperty queryId      = new SimpleIntegerProperty();
    private final IntegerProperty customerId   = new SimpleIntegerProperty();
    private final StringProperty  customerName = new SimpleStringProperty();   
    private final IntegerProperty vehicleId    = new SimpleIntegerProperty();
    private final StringProperty  queryDate    = new SimpleStringProperty();
    private final StringProperty  queryText    = new SimpleStringProperty();
    private final StringProperty  responseText = new SimpleStringProperty();

    public CustomerQuery() { super(); }

    public CustomerQuery(int queryId, int customerId, String customerName, int vehicleId,
                         String queryDate, String queryText, String responseText) {
        super(queryId);
        this.queryId.set(queryId);
        this.customerId.set(customerId);
        this.customerName.set(customerName);
        this.vehicleId.set(vehicleId);
        this.queryDate.set(queryDate);
        this.queryText.set(queryText);
        this.responseText.set(responseText);
    }


    public CustomerQuery(int queryId, int customerId, int vehicleId,
                         String queryDate, String queryText, String responseText) {
        this(queryId, customerId, "", vehicleId, queryDate, queryText, responseText);
    }

    @Override public String getSummary() {
        return "Query #" + getQueryId() + " | Customer #" + getCustomerId()
                + " | " + getQueryDate();
    }
    @Override public String getModuleTag() { return "CUSTOMER"; }

    public IntegerProperty queryIdProperty()      { return queryId; }
    public IntegerProperty customerIdProperty()    { return customerId; }
    public StringProperty  customerNameProperty()  { return customerName; }
    public IntegerProperty vehicleIdProperty()     { return vehicleId; }
    public StringProperty  queryDateProperty()     { return queryDate; }
    public StringProperty  queryTextProperty()     { return queryText; }
    public StringProperty  responseTextProperty() { return responseText; }

    public int    getQueryId()      { return queryId.get(); }
    public int    getCustomerId()   { return customerId.get(); }
    public String getCustomerName() { return customerName.get(); }
    public int    getVehicleId()    { return vehicleId.get(); }
    public String getQueryDate()    { return queryDate.get(); }
    public String getQueryText()    { return queryText.get(); }
    public String getResponseText() { return responseText.get(); }

    public void setQueryId(int v)       { queryId.set(v); id = v; }
    public void setCustomerId(int v)    { customerId.set(v); }
    public void setCustomerName(String v) { customerName.set(v); }
    public void setVehicleId(int v)     { vehicleId.set(v); }
    public void setQueryDate(String v)  { queryDate.set(v); }
    public void setQueryText(String v)  { queryText.set(v); }
    public void setResponseText(String v){ responseText.set(v); }
}
