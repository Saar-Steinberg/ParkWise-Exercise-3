package Entity;
import Enums.*;

public class Conveyor {
    private int id;                 // Conveyor's id
    private int floor;              // Conveyor's floor
    private int xCoordinate;        // Conveyor's xCoordinate
    private int yCoordinate;        // Conveyor's yCoordinate
    private double maxWeight;       // Conveyor's max weight
    private Status status;          // Conveyor's current status
    private Status previousStatus;  // Conveyor's last status - for deep history
    private int attempts;           // Conveyor's counter for integrity check attepmts
    private boolean weightChanged;  // Flag to follow weight changes
    private Vehicle currentVehicle; // Current vehicle on the conveyor, nulll if there is no vehicle

    // Constructor
    public Conveyor(int id, int floor, int xCoordinate, int yCoordinate, double maxWeight) {
        this.id = id;
        this.floor = floor;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.maxWeight = maxWeight;
        
        this.status = Status.OFF; // Conveyor exists but off at beggining
        this.previousStatus = Status.OFF;
        this.attempts = 0;
        this.weightChanged = false;
        this.currentVehicle = null; 
    }
    // Empty Constructor
    public Conveyor(){
        this.id = -1;
        this.floor = -0101;
        this.xCoordinate = -0101;
        this.yCoordinate = -0101;
        this.maxWeight = -1;
        
        this.status = Status.OFF; // Conveyor exists but off at beggining
        this.previousStatus = Status.OFF;
        this.attempts = 0;
        this.weightChanged = false;
        this.currentVehicle = null;
    }
    
    
    // Checks if conveyor is available - true if its empty and passed the integrity checks
    public boolean isAvailable() {
        return (this.status == Status.OPERATIONAL) && (this.currentVehicle == null);
    }
    
    // Loading a vehicle on the conveyor
    public void assignVehicle(Vehicle vehicle) {
        this.currentVehicle = vehicle;
        this.previousStatus = this.status; 
        this.status = Status.ACTIVE;       
    }
    
    // Unloading a vehicle from the conveyor
    public void clearVehicle() {
        this.currentVehicle = null;
        this.status = Status.OPERATIONAL; 
    }
    // Getters
    public int getId() { return id; }
    public double getMaxWeight() { return maxWeight; }
    public Status getStatus() { return status; }
    public Vehicle getCurrentVehicle() { return currentVehicle; }
    public int getXCoordinate(){return xCoordinate;}
    public int getYCoordinate(){return yCoordinate;}
    public int getFloor(){return floor;}
    
    
    // Sets the conveyor current and previous status
    public void setStatus(Status status) {
        this.previousStatus = this.status;
        this.status = status;
    
}
}
