package com.yourname.ambulance.model;

public class Ambulance {
    private final String ambulanceId;
    private final AmbulanceType type;
    private AmbulanceStatus status;
    private final String driverName;
    private final String driverPhone;
    private double currentLocationKm;

    public Ambulance(String ambulanceId, AmbulanceType type, String driverName, String driverPhone, double currentLocationKm) {
        this.ambulanceId = ambulanceId;
        this.type = type;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.currentLocationKm = currentLocationKm;
        this.status = AmbulanceStatus.AVAILABLE;
    }

    public String getAmbulanceId() { return ambulanceId; }
    public AmbulanceType getType() { return type; }
    public AmbulanceStatus getStatus() { return status; }
    public void setStatus(AmbulanceStatus status) { this.status = status; }
    public String getDriverName() { return driverName; }
    public String getDriverPhone() { return driverPhone; }
    public double getCurrentLocationKm() { return currentLocationKm; }
    public void setCurrentLocationKm(double currentLocationKm) { this.currentLocationKm = currentLocationKm; }
}