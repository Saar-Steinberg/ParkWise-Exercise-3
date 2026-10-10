package Control;
import Entity.*;
import Boundary.*;

import java.util.List;

public class ConveyorsController {

    private ParkingLot parkingLot;
    private GateSensorInterface gateSensor;
    private ParkingRepository repository;

    // Constructor 
    public ConveyorsController(ParkingLot parkingLot, GateSensorInterface gateSensor, ParkingRepository repository) {
        this.parkingLot = parkingLot;
        this.gateSensor = gateSensor;
        this.repository = repository;
    }

    // Parks a vehicle once we get a new session (created when the gate opens)
    public void parkVehicle(ParkingSession session) {
        Vehicle vehicle = session.getVehicle();
        System.out.println(">> ConveyorsController: Starting parking process for " + vehicle.getId());
        
        // Assigning a conveyor
        Conveyor conveyor = findOptimalConveyor(vehicle);
        
        if (conveyor == null) {
            System.err.println("Error: No suitable conveyor found for vehicle " + vehicle.getId());
            return;
        }

        // Running an optim algorithm to find the nearest parking space
        ParkingSpace optimalSpace = findNearestParkingSpace(conveyor, vehicle);
        
        if (optimalSpace == null) {
            System.err.println("Error: Parking lot full or no suitable space size.");
            return;
        }

        // Parking the car
        System.out.println(">> Robot " + conveyor.getId() + " taking vehicle " + vehicle.getId() + 
                           " to Floor " + optimalSpace.getFloor() + " Space " + optimalSpace.getId());
        
        conveyor.assignVehicle(vehicle); // Assiging the car to the conveyor
        
        // Simulate the parking time
        try { Thread.sleep(200); } catch (Exception e) {}

        conveyor.clearVehicle();         // Clear the conveyor
        optimalSpace.setOccupied(true);  // Parking Space is now occupied
        session.setParkingSpace(optimalSpace); // Set parking space in the parking session
        
        // Sync changes to the database - update space status and save the new session
        repository.updateParkingSpaceStatus(optimalSpace.getId(), true);
        repository.insertSession(session);
        System.out.println(">> DB Sync: Session Started & Space Occupied.");
        
        System.out.println(">> Parked Successfully.");
        
        // Close the barrier at the end of the process
        gateSensor.closeBarrier();
    }

    // Gets a parking session the retrives the vehicle back to the gate
    public void retrieveVehicle(ParkingSession session) {
        Vehicle vehicle = session.getVehicle();
        ParkingSpace space = session.getParkingSpace();
        
        System.out.println(">> ConveyorsController: Retrieving vehicle " + vehicle.getId());

        // Finding a conveyor to get the car
        Conveyor conveyor = findOptimalConveyor(vehicle);
        
        if (conveyor != null) {
            System.out.println(">> Robot " + conveyor.getId() + " fetching vehicle from Floor " + space.getFloor());
            
            // Parking space is now available
            space.setOccupied(false);        
            
            // Sync changes to the database - mark space as free
            repository.updateParkingSpaceStatus(space.getId(), false);
            
            // Assign the requested vehicle to the conveyor
            conveyor.assignVehicle(vehicle); 

            // Simulate the time until the car is at the gate
            try { Thread.sleep(200); } catch (Exception e) {}
            
            conveyor.clearVehicle(); // Clear the conveyor
            
            System.out.println(">> Vehicle delivered to the exit gate. Waiting for payment...");
        } else {
            System.err.println("Error: No conveyor available for retrieval!");
        }
    }

    // Finds a conveyor to match the vehicle's needs, null if there is no matching conveyor
    private Conveyor findOptimalConveyor(Vehicle vehicle) {
        // Conveyor has to be available and suited for the vehicle's weight
        for (Conveyor c : parkingLot.getConveyors()) {
            if (c.isAvailable() && c.getMaxWeight() >= vehicle.getWeight()) {
                return c;
            }
        }
        return null;
    }

    // Finds the nearest parking space to the conveyor, checks if the parking space is suited for the current vehicle
    private ParkingSpace findNearestParkingSpace(Conveyor conveyor, Vehicle vehicle) {
        ParkingSpace bestSpace = null;
        double minDistance = Double.MAX_VALUE;

        for (ParkingSpace space : parkingLot.getParkingSpaces()) {
            // If conditions are answerd - parking space is available and size suits the car
            if (!space.isOccupied() && space.getSize().ordinal() >= vehicle.getSize().ordinal()) {
                
                // Calculating the euclidean distance
                double dx = space.getXCoordinate() - conveyor.getXCoordinate();
                double dy = space.getYCoordinate() - conveyor.getYCoordinate();
                double distance = Math.sqrt((dx * dx) + (dy * dy));

                // Considering the floors
                int floorDiff = Math.abs(space.getFloor() - conveyor.getFloor());
                distance += (floorDiff * 100);

                // If we find a better place, we save it
                if (distance < minDistance) {
                    minDistance = distance;
                    bestSpace = space;
                }
            }
        }
        return bestSpace;
    }
}