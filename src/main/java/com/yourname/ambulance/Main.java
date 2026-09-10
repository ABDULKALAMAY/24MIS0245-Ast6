package com.yourname.ambulance;

import com.yourname.ambulance.model.Ambulance;
import com.yourname.ambulance.model.AmbulanceType;
import com.yourname.ambulance.model.Emergency;
import com.yourname.ambulance.model.EmergencyPriority;
import com.yourname.ambulance.service.DispatchService;

public class Main {
    public static void main(String[] args) {
        DispatchService service = new DispatchService();
        service.registerAmbulance(new Ambulance("A1", AmbulanceType.ICU, "Ravi", "9000000001", 2));
        service.registerAmbulance(new Ambulance("A2", AmbulanceType.BASIC, "Anu", "9000000002", 10));
        Emergency emergency = service.createEmergency(new Emergency(
                "E1", "P1", "Cardiac arrest", 3, "City Hospital", EmergencyPriority.CRITICAL));
        System.out.println("Emergency " + emergency.getEmergencyId() + " assigned to "
                + emergency.getAssignedAmbulanceId() + ", ETA: " + emergency.getEstimatedArrivalMinutes() + " minutes");
    }
}