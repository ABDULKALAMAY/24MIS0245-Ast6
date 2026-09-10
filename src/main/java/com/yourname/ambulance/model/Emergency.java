package com.yourname.ambulance.model;

import java.time.LocalDateTime;

public class Emergency {
    private final String emergencyId;
    private final String patientId;
    private final String emergencyType;
    private final double pickupLocationKm;
    private final String destinationHospital;
    private final EmergencyPriority priority;
    private EmergencyStatus status;
    private final LocalDateTime requestTime;
    private String assignedAmbulanceId;
    private double estimatedArrivalMinutes;

    public Emergency(String emergencyId, String patientId, String emergencyType, double pickupLocationKm,
                     String destinationHospital, EmergencyPriority priority) {
        this.emergencyId = emergencyId;
        this.patientId = patientId;
        this.emergencyType = emergencyType;
        this.pickupLocationKm = pickupLocationKm;
        this.destinationHospital = destinationHospital;
        this.priority = priority;
        this.status = EmergencyStatus.WAITING;
        this.requestTime = LocalDateTime.now();
    }

    public String getEmergencyId() { return emergencyId; }
    public String getPatientId() { return patientId; }
    public String getEmergencyType() { return emergencyType; }
    public double getPickupLocationKm() { return pickupLocationKm; }
    public String getDestinationHospital() { return destinationHospital; }
    public EmergencyPriority getPriority() { return priority; }
    public EmergencyStatus getStatus() { return status; }
    public void setStatus(EmergencyStatus status) { this.status = status; }
    public LocalDateTime getRequestTime() { return requestTime; }
    public void setAssignedAmbulanceId(String assignedAmbulanceId) { this.assignedAmbulanceId = assignedAmbulanceId; }
    public String getAssignedAmbulanceId() { return assignedAmbulanceId; }
    public void setEstimatedArrivalMinutes(double estimatedArrivalMinutes) { this.estimatedArrivalMinutes = estimatedArrivalMinutes; }
    public double getEstimatedArrivalMinutes() { return estimatedArrivalMinutes; }
}