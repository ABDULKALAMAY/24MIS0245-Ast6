package com.yourname.ambulance.service;

import com.yourname.ambulance.exception.InvalidEmergencyRequestException;
import com.yourname.ambulance.model.Ambulance;
import com.yourname.ambulance.model.AmbulanceStatus;
import com.yourname.ambulance.model.AmbulanceType;
import com.yourname.ambulance.model.Emergency;
import com.yourname.ambulance.model.EmergencyPriority;
import com.yourname.ambulance.model.EmergencyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DispatchServiceTest {
    private DispatchService service;

    @BeforeEach
    void setUp() {
        service = new DispatchService();
        service.registerAmbulance(new Ambulance("A1", AmbulanceType.ICU, "Ravi", "9000000001", 2));
        service.registerAmbulance(new Ambulance("A2", AmbulanceType.BASIC, "Anu", "9000000002", 10));
    }

    @Test
    void criticalEmergencyPrefersIcuAmbulance() {
        Emergency emergency = service.createEmergency(new Emergency(
                "E1", "P1", "Cardiac arrest", 3, "City Hospital", EmergencyPriority.CRITICAL));
        assertEquals("A1", emergency.getAssignedAmbulanceId());
        assertEquals(1.5, emergency.getEstimatedArrivalMinutes());
    }

    @Test
    void nearestMatchingTypeIsSelected() {
        service.registerAmbulance(new Ambulance("A3", AmbulanceType.BASIC, "Maya", "9000000003", 4));
        Emergency emergency = service.createEmergency(new Emergency(
                "E2", "P2", "Fall", 5, "City Hospital", EmergencyPriority.NORMAL));
        assertEquals("A3", emergency.getAssignedAmbulanceId());
    }

    @Test
    void queuesWhenNoAmbulanceIsAvailableAndDrainsAutomatically() {
        service.createEmergency(new Emergency("E1", "P1", "Accident", 3, "City Hospital", EmergencyPriority.CRITICAL));
        service.createEmergency(new Emergency("E2", "P2", "Fall", 5, "City Hospital", EmergencyPriority.NORMAL));
        Emergency queued = service.createEmergency(new Emergency("E3", "P3", "Fall", 5, "City Hospital", EmergencyPriority.HIGH));
        assertNull(queued.getAssignedAmbulanceId());
        assertEquals(1, service.getWaitingCount());
        completeCurrentTrip("A1");
        assertEquals("A1", queued.getAssignedAmbulanceId());
        assertEquals(0, service.getWaitingCount());
    }

    @Test
    void criticalWaitingEmergencyIsAllocatedBeforeNormalEmergency() {
        service.createEmergency(new Emergency("E1", "P1", "Accident", 3, "Hospital", EmergencyPriority.CRITICAL));
        service.createEmergency(new Emergency("E2", "P2", "Fall", 5, "Hospital", EmergencyPriority.NORMAL));
        Emergency normal = service.createEmergency(new Emergency("E3", "P3", "Fever", 5, "Hospital", EmergencyPriority.NORMAL));
        Emergency critical = service.createEmergency(new Emergency("E4", "P4", "Trauma", 6, "Hospital", EmergencyPriority.CRITICAL));
        completeCurrentTrip("A1");
        assertEquals("A1", critical.getAssignedAmbulanceId());
        assertNull(normal.getAssignedAmbulanceId());
    }

    @Test
    void stateTransitionsCompleteEmergency() {
        Emergency emergency = service.createEmergency(new Emergency("E1", "P1", "Injury", 4, "Hospital", EmergencyPriority.NORMAL));
        service.advanceAmbulanceState("A2");
        assertEquals(AmbulanceStatus.EN_ROUTE, service.getAmbulance("A2").getStatus());
        service.advanceAmbulanceState("A2");
        assertEquals(EmergencyStatus.IN_PROGRESS, emergency.getStatus());
        service.advanceAmbulanceState("A2");
        service.advanceAmbulanceState("A2");
        assertEquals(EmergencyStatus.COMPLETED, emergency.getStatus());
        assertEquals(AmbulanceStatus.AVAILABLE, service.getAmbulance("A2").getStatus());
    }

    @Test
    void invalidRequestThrows() {
        assertThrows(InvalidEmergencyRequestException.class, () -> service.createEmergency(
                new Emergency("E9", "", "Fall", 4, "Hospital", EmergencyPriority.NORMAL)));
    }

    private void completeCurrentTrip(String ambulanceId) {
        service.advanceAmbulanceState(ambulanceId);
        service.advanceAmbulanceState(ambulanceId);
        service.advanceAmbulanceState(ambulanceId);
        service.advanceAmbulanceState(ambulanceId);
    }
}