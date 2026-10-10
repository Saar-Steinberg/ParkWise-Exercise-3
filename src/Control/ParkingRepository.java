package Control;

import Entity.*;
import Enums.Size;
import Enums.Status;
import Enums.Color;
import utils.DBConnection;
import java.sql.*;
public class ParkingRepository {

    // Loading a parking lot from DB
    public ParkingLot loadMainParkingLot() {
        // Loading parking lot number 1
        String sql = "SELECT * FROM ParkingLots WHERE ID = 1"; 
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) { // Getting the values from DB
                String name = rs.getString("Name");
                String address = rs.getString("Address");
                String city = rs.getString("City");
                int capacity = 100;
                try { capacity = rs.getInt("AvailableSpaces"); } catch (Exception e) {}

                System.out.println(">> Loaded ParkingLot: " + name + " (ID=1)");
                return new ParkingLot(1, name, address, city, capacity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(">> Warning: ParkingLot ID=1 not found. Creating default.");
        // In case parking lot was not found in db - create a new parking lot so we could work with it
        return new ParkingLot(1, "Default Park", "Street 1", "City", 100);
    }

    // Loading all parking spaces from DB
    public void loadParkingSpaces(ParkingLot lot) {
        String sql = "SELECT * FROM ParkingSpace WHERE parkingLotID = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, lot.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) { // Getting the values from DB
                int id = rs.getInt("ID");
                int floor = rs.getInt("floor");
                int x = rs.getInt("xCoordinate");
                int y = rs.getInt("yCoordinate");
                
                String sizeStr = rs.getString("size");
                Size size = Size.MEDIUM;
                if (sizeStr != null) try { size = Size.valueOf(sizeStr.toUpperCase()); } catch (Exception e) {}

                boolean occupied = rs.getBoolean("isOccupied");

                ParkingSpace ps = new ParkingSpace(id, lot.getId(), floor, x, y, size);
                // Set Parking space to its status and adding it to lot
                ps.setOccupied(occupied); 
                lot.addParkingSpace(ps);
            }
            System.out.println(">> Loaded " + lot.getParkingSpaces().size() + " Parking Spaces.");
        } catch (SQLException e) {
            System.err.println("X Error Loading Spaces: " + e.getMessage());
        }
    }

    // Loading all conveyors from DB
    public void loadConveyors(ParkingLot lot) {
        String sql = "SELECT * FROM Conveyors WHERE ParkingLotID = ?"; 
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, lot.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) { // Getting all values
                int id = rs.getInt("ID");
                int floor = rs.getInt("FloorNum");
                int x = rs.getInt("X_Pos");
                int y = rs.getInt("Y_Pos");
                double weight = rs.getDouble("MaxWeight");
                
                String statusStr = rs.getString("Status");
                Status status = Status.OFF;
                if (statusStr != null) try { status = Status.valueOf(statusStr.toUpperCase()); } catch (Exception e) {}

                Conveyor c = new Conveyor(id, floor, x, y, weight);
                c.setStatus(status);
                lot.addConveyor(c);
            }
            System.out.println(">> Loaded " + lot.getConveyors().size() + " Conveyors.");
        } catch (SQLException e) {
            System.err.println("X Error Loading Conveyors: " + e.getMessage());
        }
    }

    // Loading all parking sessions from DB
    public void loadSessions(ParkingLot lot) {
        String sql = "SELECT * FROM ParkingSessions WHERE ParkingLotID = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, lot.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) { // Getting all values
                int id = 0;
                try { id = Integer.parseInt(rs.getString("ID")); } catch (Exception e) {}
                String vehicleId = rs.getString("VehicleID");
                int spaceId = rs.getInt("ParkingSpaceID"); 
                Timestamp entryTs = rs.getTimestamp("EntryTime");
                Timestamp exitTs = rs.getTimestamp("ExitTime");
                double price = rs.getDouble("PaymentAmount");

                Vehicle v = getVehicleById(vehicleId);
                
                if (v != null) {
                    ParkingSpace ps = null;
                    if (spaceId != 0) {
                         int finalSpaceId = spaceId;
                         // Getting the parking space
                         ps = lot.getParkingSpaces().stream()
                                .filter(s -> s.getId() == finalSpaceId).findFirst().orElse(null);
                    }

                    if (ps != null || exitTs != null) { // If session is new
                        ParkingSession session = new ParkingSession(id, v, lot.getId());
                        if (ps != null) session.setParkingSpace(ps);
                        if (entryTs != null) session.setEntryTime(entryTs.toLocalDateTime());
                        
                        if (exitTs == null) { 
                            if (ps != null) {
                                // Adds it to active parking sessions array and sets parking space status to occupied
                                lot.addActiveSession(session);
                                ps.setOccupied(true);
                            }
                        } else { 
                            session.setExitTime(exitTs.toLocalDateTime());
                            session.setSessionPrice(price);
                            // If session ended, adds it to history sessions
                            lot.getHistorySessions().add(session);
                        }
                    }
                }
            }
            System.out.println(">> Loaded " + lot.getActiveSessions().size() + " Active Sessions.");
            
        } catch (SQLException e) {
            System.err.println("X Error Loading Sessions: " + e.getMessage());
        }
    }
    
    // Adding a session to DB
    public void insertSession(ParkingSession session) {
        checkAndInsertVehicle(session.getVehicle());
        
        String sql = "INSERT INTO ParkingSessions (ID, VehicleID, ParkingLotID, EntryTime, ParkingSpaceID, IsPaid) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, String.valueOf(session.getId()));
            stmt.setString(2, session.getVehicle().getId());
            stmt.setInt(3, session.getParkingLotID());
            stmt.setTimestamp(4, Timestamp.valueOf(session.getEntryTime()));
            
            if (session.getParkingSpace() != null) {
                stmt.setInt(5, session.getParkingSpace().getId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            stmt.setBoolean(6, false);
            stmt.executeUpdate();
            System.out.println(">> Session saved to DB.");
            
        } catch (SQLException e) {
            System.err.println("X Failed to save session: " + e.getMessage());
        }
    }
    
    // Updating the exit time and payment amount at the end of a session
    public void updateSessionExit(ParkingSession session) {
        String sql = "UPDATE ParkingSessions SET ExitTime = ?, PaymentAmount = ?, IsPaid = ? WHERE ID = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(session.getExitTime()));
            stmt.setDouble(2, session.getSessionPrice());
            stmt.setBoolean(3, true);
            stmt.setString(4, String.valueOf(session.getId()));
            stmt.executeUpdate();
            System.out.println(">> Session updated.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Updating the status of a parking space
    public void updateParkingSpaceStatus(int spaceId, boolean isOccupied) {
        String sql = "UPDATE ParkingSpace SET isOccupied = ? WHERE ID = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isOccupied);
            stmt.setInt(2, spaceId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Getting a vehicle from DB - by its licence plate
    private Vehicle getVehicleById(String id) {
        String sql = "SELECT * FROM Vehicles WHERE ID = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("Type");
                double weight = rs.getDouble("Weight");
                String colorStr = rs.getString("Color");
                String sizeStr = rs.getString("VehicleSize");
                Color color = Color.OTHER; 
                try { if(colorStr!=null) color = Color.valueOf(colorStr.toUpperCase()); } catch(Exception e){}
                Size size = Size.MEDIUM;
                try { if(sizeStr!=null) size = Size.valueOf(sizeStr.toUpperCase()); } catch(Exception e){}
                return new Vehicle(id, type, color, size, weight);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    // Adding a vehicle to DB
    private void checkAndInsertVehicle(Vehicle v) {
        if (getVehicleById(v.getId()) != null) return;
        String sql = "INSERT INTO Vehicles (ID, Type, Color, VehicleSize, Weight) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, v.getId());
            stmt.setString(2, v.getType());
            stmt.setString(3, v.getColor() != null ? v.getColor().toString() : "OTHER");
            stmt.setString(4, v.getSize() != null ? v.getSize().toString() : "MEDIUM");
            stmt.setDouble(5, v.getWeight());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}