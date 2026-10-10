package Entity;
import java.time.LocalDateTime; 
import java.time.Duration;

public class ParkingSession {
    private int id;                   // Parking session id
    private Vehicle vehicle;          // Parked vehicle
    private int parkingLotID;         // Parking lot's id
    private LocalDateTime entryTime;  // Vehicle's entry time
    private LocalDateTime exitTime;   // Vehicle's exit time
    private ParkingSpace parkingSpace;// Vehicle's parking space
    private double sessionPrice;      // Session's price

    // Consctructor
    public ParkingSession(int id, Vehicle vehicle, int parkingLotID) {
        this.id = id;
        this.vehicle = vehicle;
        this.parkingLotID = parkingLotID;
        this.entryTime = LocalDateTime.now(); // Start session when creating a session object 
        
        this.exitTime = null; // Session is still active
        this.sessionPrice = 0.0; // Payment did not happen yet
    }


    // Ends the current session - sets exit time to now
    public void endSession() {
        this.exitTime = LocalDateTime.now(); 
    }
    // Returns the duration as a double
    public double getDurationInHours() {
    // If there is exit time, use it, otherwise, use now
    LocalDateTime endTime = (this.exitTime != null) ? this.exitTime : LocalDateTime.now();
    
    long minutes = Duration.between(this.entryTime, endTime).toMinutes();
    
    return minutes / 60.0;
}

    // Getters
    public int getId() { return id; }
    public int getParkingLotID(){return parkingLotID;}
    public Vehicle getVehicle() { return vehicle; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public ParkingSpace getParkingSpace() { return parkingSpace; }
    public double getSessionPrice() { return sessionPrice; }

    // Once session ended, sets the session's price (calculated in PaymentController)
    public void setSessionPrice(double sessionPrice) {
        this.sessionPrice = sessionPrice;
    }
    // Sets a new parking space
    public void setParkingSpace(ParkingSpace parkingSpace){
        this.parkingSpace = parkingSpace;
    }
    // Set Entry Time
    public void setEntryTime(LocalDateTime t) { this.entryTime = t; }
    // Set Exit Time
    public void setExitTime(LocalDateTime t) { this.exitTime = t; }
}