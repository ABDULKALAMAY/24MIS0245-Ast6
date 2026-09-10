package com.yourname.ambulance.service;

import com.yourname.ambulance.exception.AmbulanceAlreadyAssignedException;
import com.yourname.ambulance.exception.AmbulanceNotFoundException;
import com.yourname.ambulance.exception.InvalidEmergencyRequestException;
import com.yourname.ambulance.model.Ambulance;
import com.yourname.ambulance.model.AmbulanceStatus;
import com.yourname.ambulance.model.AmbulanceType;
import com.yourname.ambulance.model.Emergency;
import com.yourname.ambulance.model.EmergencyPriority;
import com.yourname.ambulance.model.EmergencyStatus;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

public class DispatchService {
    private static final double AVERAGE_SPEED_KMPH = 40.0;
    private final Map<String, Ambulance> ambulances = new HashMap<>();
    private final Map<String, Emergency> emergencyHistory = new LinkedHashMap<>();
    private final PriorityQueue<Emergency> waitingQueue = new PriorityQueue<>(
            Comparator.comparingInt((Emergency emergency) -> emergency.getPriority().ordinal())
                    .thenComparing(Emergency::getRequestTime));

    public void registerAmbulance(Ambulance ambulance) {
        if (ambulance == null || ambulance.getAmbulanceId() == null || ambulance.getAmbulanceId().isBlank()) {
            throw new IllegalArgumentException("Ambulance ID is required.");
        }
        ambulances.put(ambulance.getAmbulanceId(), ambulance);
    }

    public Emergency createEmergency(Emergency emergency) {
        validateEmergency(emergency);
        if (emergencyHistory.containsKey(emergency.getEmergencyId())) {
            throw new InvalidEmergencyRequestException("Emergency ID already exists.");
        }
        emergencyHistory.put(emergency.getEmergencyId(), emergency);
        tryAllocate(emergency);
        return emergency;
    }

    private void validateEmergency(Emergency emergency) {
        if (emergency == null || isBlank(emergency.getEmergencyId()) || isBlank(emergency.getPatientId())
                || isBlank(emergency.getEmergencyType()) || isBlank(emergency.getDestinationHospital())
                || emergency.getPriority() == null || emergency.getPickupLocationKm() < 0) {
            throw new InvalidEmergencyRequestException("Emergency details are invalid or incomplete.");
        }
    }

    private boolean isBlank(String value) { return value == null || value.isBlank(); }

    private void tryAllocate(Emergency emergency) {
        Optional<Ambulance> bestAmbulance = findBestAmbulance(emergency);
        if (bestAmbulance.isPresent()) {
            assign(bestAmbulance.get(), emergency);
        } else {
            waitingQueue.add(emergency);
        }
    }

    private Optional<Ambulance> findBestAmbulance(Emergency emergency) {
        AmbulanceType preferredType = preferredType(emergency.getPriority());
        return ambulances.values().stream()
                .filter(ambulance -> ambulance.getStatus() == AmbulanceStatus.AVAILABLE)
                .min(Comparator.comparingInt((Ambulance ambulance) -> ambulance.getType() == preferredType ? 0 : 1)
                        .thenComparingDouble(ambulance -> Math.abs(
                                ambulance.getCurrentLocationKm() - emergency.getPickupLocationKm())));
    }

    private AmbulanceType preferredType(EmergencyPriority priority) {
        return switch (priority) {
            case CRITICAL -> AmbulanceType.ICU;
            case HIGH -> AmbulanceType.ADVANCED_LIFE_SUPPORT;
            default -> AmbulanceType.BASIC;
        };
    }

    private void assign(Ambulance ambulance, Emergency emergency) {
        if (ambulance.getStatus() != AmbulanceStatus.AVAILABLE) {
            throw new AmbulanceAlreadyAssignedException(ambulance.getAmbulanceId() + " is already assigned.");
        }
        ambulance.setStatus(AmbulanceStatus.DISPATCHED);
        emergency.setStatus(EmergencyStatus.ASSIGNED);
        emergency.setAssignedAmbulanceId(ambulance.getAmbulanceId());
        double distance = Math.abs(ambulance.getCurrentLocationKm() - emergency.getPickupLocationKm());
        emergency.setEstimatedArrivalMinutes(distance / AVERAGE_SPEED_KMPH * 60.0);
    }

    public void advanceAmbulanceState(String ambulanceId) {
        Ambulance ambulance = ambulances.get(ambulanceId);
        if (ambulance == null) {
            throw new AmbulanceNotFoundException("No ambulance with ID " + ambulanceId);
        }
        switch (ambulance.getStatus()) {
            case DISPATCHED -> ambulance.setStatus(AmbulanceStatus.EN_ROUTE);
            case EN_ROUTE -> {
                ambulance.setStatus(AmbulanceStatus.PATIENT_PICKED_UP);
                findActiveEmergency(ambulanceId).ifPresent(emergency -> emergency.setStatus(EmergencyStatus.IN_PROGRESS));
            }
            case PATIENT_PICKED_UP -> ambulance.setStatus(AmbulanceStatus.HOSPITAL_ARRIVED);
            case HOSPITAL_ARRIVED -> {
                findActiveEmergency(ambulanceId).ifPresent(emergency -> emergency.setStatus(EmergencyStatus.COMPLETED));
                ambulance.setStatus(AmbulanceStatus.AVAILABLE);
                allocateNextWaitingEmergency(ambulance);
            }
            default -> throw new IllegalStateException("Cannot advance an available ambulance.");
        }
    }

    private Optional<Emergency> findActiveEmergency(String ambulanceId) {
        return emergencyHistory.values().stream()
                .filter(emergency -> ambulanceId.equals(emergency.getAssignedAmbulanceId()))
                .filter(emergency -> emergency.getStatus() == EmergencyStatus.ASSIGNED
                        || emergency.getStatus() == EmergencyStatus.IN_PROGRESS)
                .findFirst();
    }

    private void allocateNextWaitingEmergency(Ambulance ambulance) {
        Emergency next = waitingQueue.poll();
        if (next != null) {
            assign(ambulance, next);
        }
    }

    public Collection<Emergency> getHistory() { return emergencyHistory.values(); }
    public int getWaitingCount() { return waitingQueue.size(); }
    public Ambulance getAmbulance(String ambulanceId) {
        Ambulance ambulance = ambulances.get(ambulanceId);
        if (ambulance == null) {
            throw new AmbulanceNotFoundException("No ambulance with ID " + ambulanceId);
        }
        return ambulance;
    }
}