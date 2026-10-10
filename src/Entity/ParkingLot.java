package Entity;

import java.util.ArrayList;
import java.util.List;

import Enums.Size;

public class ParkingLot {
    
    private int id;             // Parking lot's id
    private String name;        // Parking lot's name
    private String address;     // Parking lot's address
    private String city;        // Parking lot's city
    private int availableSpace; // Parking lot's number of available spaces

    private List<Client> registeredClients;         // All registred clients
    private List<Conveyor> conveyors;               // All parking lot's conveyors
    private List<PriceList> priceHistory;           // Parking lot's price history
    private List<ParkingSession> activeSessions;    // Current active sessions
    private List<ParkingSession> historySessions;   // Non active sessions (session history)
    private List<ParkingSpace> parkingSpaces;       // All parking spaces in lot

    // Constructor
    public ParkingLot(int id, String name, String address, String city, int maxCapacity) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.availableSpace = maxCapacity;
        this.conveyors = new ArrayList<>();
        this.priceHistory = new ArrayList<>();
        this.activeSessions = new ArrayList<>();
        this.historySessions = new ArrayList<>();
        this.parkingSpaces = new ArrayList<>();
        this.registeredClients = new ArrayList<>();
    }

    // Empty Constructor
    public ParkingLot(){
        this.id = -1;
        this.name = null;
        this.address = null;
        this.city = null;
        this.availableSpace = -1;
        this.conveyors = new ArrayList<>();
        this.priceHistory = new ArrayList<>();
        this.activeSessions = new ArrayList<>();
        this.historySessions = new ArrayList<>();
        this.parkingSpaces = new ArrayList<>();
        this.registeredClients = new ArrayList<>();
    }

    
    // Returns the Client who ownes the vehicle detected by the gate sensor, null otherwise
    public Client getClientByVehicle(Vehicle vehicle) {
        for (Client client : this.registeredClients) {
            if (client.ownsVehicle(vehicle.getId())) {
                return client; // Client Found
            }
        }
        
        return null; 
    }
    
    // Adds a session to active sessions list
public void addActiveSession(ParkingSession session) {
    this.activeSessions.add(session);
    this.availableSpace--; 
    
    // Checking if a parking space already assigned
    if (session.getParkingSpace() != null) {
        session.getParkingSpace().setOccupied(true);
    }
}

    // Ends a session - adds it to history sessions list, removes it from active sessions a list
    public void completeSession(ParkingSession session) {
        if (this.activeSessions.remove(session)) {
            this.historySessions.add(session);
            this.availableSpace++;
            
            // Parking space is now free
            session.getParkingSpace().setOccupied(false);
        }
    }
    // Adds a conveyor to conveyor's list
    public void addConveyor(Conveyor c) { conveyors.add(c); }
    // Adds a parking space to parking space list
    public void addParkingSpace(ParkingSpace ps) { parkingSpaces.add(ps); }
    // Adds a vehicle to registerd vehicles list

    // Adds a price list to price lists list
    public void addPriceList(PriceList pl) { priceHistory.add(pl); }

    // Getters
    public List<ParkingSession> getActiveSessions() { return activeSessions; }
    public List<PriceList> getPriceHistory() { return priceHistory; }
    public int getId(){return id;}
    public String getName(){return name;}
    public List<Conveyor> getConveyors() { return conveyors; }
    public List<ParkingSpace> getParkingSpaces() { return parkingSpaces; }
    public List<ParkingSession> getHistorySessions(){return historySessions;}
}
