package Control;
import Entity.*;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PricingController {

    private PriceList currentPriceList;
    private int parkingLotId; // Current parking lot id
    
    // Members discount for every session
    private final double MEMBER_DISCOUNT_RATE = 0.10; 

    public PricingController(int parkingLotId) {
        this.parkingLotId = parkingLotId;
        
        // Defualt price list - in case something goes wrong
        this.currentPriceList = new PriceList(0, new Date(), 10.0, 5.0, 50.0);
    }

    
    // Calculates the price for the session, according to the client (member or not) and according the updated price list
    public double calculatePayment(ParkingSession session, Client client, List<ParkingSession> historySessions) {
        
        double price = calculateBasePrice(session);

        // If the client is not a member, he pays the full price
        if (client == null || !client.isMember()) {
            return price;
        }
        System.out.println(">> Club Member Identified! Calculating discounts...");

        // Setting the discount for members
        double discountAmount = price * MEMBER_DISCOUNT_RATE;
        price = price - discountAmount;
        System.out.println(">> Fixed Member Discount Applied (" + (MEMBER_DISCOUNT_RATE * 100) + "%). New Price: " + price);

        // Check if the client is eligible for free parking 
        if (isEligibleForFreeParking(session, client, historySessions)) {
            System.out.println(">> BENEFIT APPLIED: First parking in registration month in a preferred lot is FREE!");
            return 0.0; 
        }

        return price; 
    }

    // Calculates the base session price according to time spent in the lot
    private double calculateBasePrice(ParkingSession session) {
        if (currentPriceList == null) return 0.0;

        LocalDateTime entry = session.getEntryTime();
        LocalDateTime exit = LocalDateTime.now(); 

        
        long hours = Duration.between(entry, exit).toHours();
        if (Duration.between(entry, exit).toMinutes() % 60 > 0) hours++;
        if (hours < 1) hours = 1;

        double total = currentPriceList.getFirstHourPrice();

        if (hours > 1) {
            total += (hours - 1) * currentPriceList.getExtraHourPrice();
        }

        if (total > currentPriceList.getFullDayPrice()) {
            total = currentPriceList.getFullDayPrice();
        }
        return total;
    }

    // Checks is the client is eligible for free parking according to the parking lot rules
    private boolean isEligibleForFreeParking(ParkingSession currentSession, Client client, List<ParkingSession> history) {
        LocalDateTime now = LocalDateTime.now();
        
        // Date of registration
        LocalDateTime regDate = client.getRegistrationDate().toInstant()
                                      .atZone(ZoneId.systemDefault())
                                      .toLocalDateTime();

        // Is the current month the registration month
        boolean isRegistrationMonth = (now.getMonthValue() == regDate.getMonthValue()) && 
                                      (now.getYear() == regDate.getYear());

        if (!isRegistrationMonth) return false;

        // Is it a preffered parking lot
        boolean isPreferred = false;
        if (client.getPreferredParkingLots() != null) {
            for (ParkingLot lot : client.getPreferredParkingLots()) {
                if (lot.getId() == this.parkingLotId) {
                    isPreferred = true;  // Found
                    break;
                }
            }
        }
        if (!isPreferred) return false;

        // Is this the client's first parking session this month in the parking lot
        for (ParkingSession pastSession : history) {
            // Check if this is the same car
            if (pastSession.getVehicle().getId().equals(currentSession.getVehicle().getId())) {
                LocalDateTime exitTime = pastSession.getExitTime();
                // If client's had an exit in this month, he is not eligible
                if (exitTime.getMonthValue() == now.getMonthValue() && 
                    exitTime.getYear() == now.getYear()) {
                    return false; 
                }
            }
        }

        return true; // If we have gone thus far, the client is eligible for free parking
    }

    // Imports the price list to our system
    public void importPriceList(String json) {
        System.out.println(">> PricingController (Lot " + parkingLotId + "): Receiving JSON update...");
        
        try {
            // Finding the currect price list for this parking lot
            PriceList foundPriceList = parseJsonArrayFindById(json, this.parkingLotId);
            
            if (foundPriceList != null) {
                this.currentPriceList = foundPriceList; // Setting the new price list
                System.out.println(">> Price list updated successfully.");
                System.out.println(">> Rates -> 1st Hour: " + currentPriceList.getFirstHourPrice() + 
                                   ", Extra: " + currentPriceList.getExtraHourPrice() + 
                                   ", Daily Cap: " + currentPriceList.getFullDayPrice());
            } else {
                System.err.println(">> Warning: No configuration found for Lot ID " + this.parkingLotId + " in the JSON file.");
            }

        } catch (Exception e) {
            System.err.println(">> Error parsing price list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Parses the json to a price list, adds it to the currect parking lot
    private PriceList parseJsonArrayFindById(String json, int targetLotId) {
        String content = json.trim();
        
        if (content.startsWith("[")) {
            content = content.substring(1, content.length() - 1);
        }

        // Splits the data in the json
        String[] objects = content.split("(?<=\\}),\\s*(?=\\{)");

        for (String objJson : objects) {
            Map<String, String> map = parseSingleObjectToMap(objJson);
            
            // Check if the object suits our parking lot
            if (map.containsKey("parkingLotId")) {
                int jsonId = Integer.parseInt(map.get("parkingLotId"));
                if (jsonId == targetLotId) {
                    return createPriceListFromMap(map);
                }
            }
        }
        return null;
    }

    // Returns a map that contains key and value of the price list from the json
    private Map<String, String> parseSingleObjectToMap(String json) {
        Map<String, String> map = new HashMap<>();
        
        extractValue(json, "parkingLotId", map);
        extractValue(json, "effectiveDate", map);
        extractValue(json, "firstHour", map); 
        extractValue(json, "extraHour", map);
        extractValue(json, "fullDay", map);
        
        return map;
    }

    // Helper function - finds a value in the text and puts it in the map
    private void extractValue(String json, String key, Map<String, String> map) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"?([^,\"\n\r\\}]+)\"?");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            map.put(key, matcher.group(1).trim());
        }
    }

    // Creates a PriceList object from the map extracted from json
    private PriceList createPriceListFromMap(Map<String, String> map) {
        try {
            int id = Integer.parseInt(map.get("parkingLotId"));
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date effectiveDate = formatter.parse(map.get("effectiveDate"));
            
            double firstHour = Double.parseDouble(map.get("firstHour"));
            double extraHour = Double.parseDouble(map.get("extraHour"));
            double fullDay = Double.parseDouble(map.get("fullDay"));

            return new PriceList(id, effectiveDate, firstHour, extraHour, fullDay);
        } catch (Exception e) {
            throw new RuntimeException("Mapping error: " + e.getMessage());
        }
    }
}