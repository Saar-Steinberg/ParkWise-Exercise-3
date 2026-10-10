package Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Client {
    private int id;                // Client's id
    private String firstName;      // Client's first name 
    private String lastName;       // Client's last name 
    private String phoneNumber;    // Client's phone number 
    private boolean isMember;      // Is the client a member
    private Date registrationDate; // Client's registration date 
    private List<ParkingLot> preferredLotHistory; // Client's preffered lots
    private List<String> ownedVehicleIds; // Client's owned vehicles (ID's)

    // Constructor
    public Client(int id, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.isMember = false; // Client is not a member in the beggining
        this.registrationDate = new Date(); // Registration date = now
        this.preferredLotHistory = new ArrayList<>();
        this.ownedVehicleIds = new ArrayList<>();
    }

    // Adds a parking lot to client's parking lot history
    public void addParkingLotToHistory(ParkingLot lot) {
        this.preferredLotHistory.add(lot);
    }
    // Turns the client to a member
    public void becomeMember(){
        this.isMember = true;
    }
    // Add a vehicle to a client
    public void addVehicle(String vehicleLicensePlate) {
        if (!this.ownedVehicleIds.contains(vehicleLicensePlate)) {
            this.ownedVehicleIds.add(vehicleLicensePlate);
        }
    }
    // Checks if the vehicle is owned by the client
    public boolean ownsVehicle(String vehicleLicensePlate) {
        return this.ownedVehicleIds.contains(vehicleLicensePlate);
    }

    // Getters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public boolean isMember() { return isMember; }
    public Date getRegistrationDate() { return registrationDate; }
    public List<ParkingLot> getPreferredParkingLots() { return preferredLotHistory; }
    
}
