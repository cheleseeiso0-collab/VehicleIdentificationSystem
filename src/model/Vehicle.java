package model;

public class Vehicle extends BaseEntity {
    private String registrationNumber;
    private String make;
    private String model;
    private int    year;
    private String color;
    private String chassisNumber;
    private int    ownerId;
    private String ownerName;

    public Vehicle() {}
    public Vehicle(int id, String reg, String make, String model, int year,
                   String color, String chassisNumber, int ownerId, String ownerName) {
        this.id = id; this.registrationNumber = reg;
        this.make = make; this.model = model; this.year = year;
        this.color = color; this.chassisNumber = chassisNumber;
        this.ownerId = ownerId; this.ownerName = ownerName;
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public String getMake()               { return make; }
    public String getModel()              { return model; }
    public int    getYear()               { return year; }
    public String getColor()              { return color; }
    public String getChassisNumber()      { return chassisNumber; }
    public int    getOwnerId()            { return ownerId; }
    public String getOwnerName()          { return ownerName; }

    public void setRegistrationNumber(String v) { registrationNumber = v; }
    public void setMake(String v)               { make = v; }
    public void setModel(String v)              { model = v; }
    public void setYear(int v)                  { year = v; }
    public void setColor(String v)              { color = v; }
    public void setChassisNumber(String v)      { chassisNumber = v; }
    public void setOwnerId(int v)               { ownerId = v; }
    public void setOwnerName(String v)          { ownerName = v; }

    @Override public String getDisplayLabel() {
        return registrationNumber + " — " + year + " " + make + " " + model +
               (color != null && !color.isEmpty() ? " (" + color + ")" : "");
    }
}
