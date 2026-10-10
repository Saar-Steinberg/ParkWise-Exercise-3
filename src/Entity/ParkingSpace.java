package Entity;
import Enums.*;

public class ParkingSpace {
    
    private int id;             // Parking space id
    private int parkingLotID;   // Parking lot id
    private int floor;          // Floor number
    private int xCoordinate;    // X value in parking lot
    private int yCoordinate;    // Y value in parking lot
    private Size size;          // Parking space size
    private boolean isOccupied; // Parking space availability

    // Constructor
    public ParkingSpace(int id, int parkingLotID, int floor, int xCoordinate, int yCoordinate, Size size) {
        this.id = id;
        this.parkingLotID = parkingLotID;
        this.floor = floor;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.size = size;
        this.isOccupied = false; 
    }
    // Empty Constructor
    public ParkingSpace(){
        this.id = -1;
        this.parkingLotID = -1;
        this.floor = -0101; // -1 can used as a floor number so its defined as -0101
        this.xCoordinate = -0101; //-1 can used as a coordinate so its defined as -0101
        this.yCoordinate = -0101; // -1 can used as a coordinate so its defined as -0101
        this.size = null;
        this.isOccupied = false;
    }

    // Getters
    public int getId() { return id; }
    public int getParkingLotID() { return parkingLotID; }
    public int getFloor() { return floor; }
    public Size getSize() { return size; }
    public int getXCoordinate(){return xCoordinate;}
    public int getYCoordinate(){return yCoordinate;}
    
    // Checks if parking place is available
    public boolean isOccupied() { return isOccupied; }

    // Changes the value of the availability of the parking space (True / False)
    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}
