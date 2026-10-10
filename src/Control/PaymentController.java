package Control;
import Entity.*;
import Boundary.*;

import java.util.List;

public class PaymentController {
    
    private PaymentGatewayInterface gateway;
    private PricingController pricingController;
    private ParkingLot parkingLot;
    private GateSensorInterface gateSensor;
    private ParkingRepository repository; 

    // Constructor
    public PaymentController(PaymentGatewayInterface gateway, 
                             PricingController pricingController, 
                             ParkingLot parkingLot,
                             GateSensorInterface gateSensor,
                             ParkingRepository repository) {
        this.gateway = gateway;
        this.pricingController = pricingController;
        this.parkingLot = parkingLot;
        this.gateSensor = gateSensor;
        this.repository = repository; 
    }

    // Gets a session and credit card details, proccecing the payment. If succesfull - opens the gate
    public void processPayment(ParkingSession session, String creditDetails) {
        System.out.println("\n--- Starting Payment Process for Vehicle: " + session.getVehicle().getId() + " ---");

        Client client = parkingLot.getClientByVehicle(session.getVehicle());
        List<ParkingSession> history = parkingLot.getHistorySessions();

        // Calculate the price 
        double amountToPay = pricingController.calculatePayment(session, client, history);
        System.out.println(">> Final Calculated Price: " + amountToPay + " NIS");

        // Executing payment (outsourcing it)
        boolean paymentSuccess = gateway.chargeCreditCard(amountToPay, creditDetails);

        if (paymentSuccess) {
            System.out.println(">> Payment System: Transaction APPROVED.");

            // Update the session details
            session.setSessionPrice(amountToPay);
            session.endSession(); 
            
            // Update the session in DB with exit time, price and payment status
            repository.updateSessionExit(session);
            System.out.println(">> DB Sync: Session Ended & Paid.");

            // Generating recipt
            generateReceipt(session, client);

            // Updating parking lot
            parkingLot.completeSession(session);
            
            // Open sesemie :)
            System.out.println(">> System: Instructing Gate to OPEN.");
            gateSensor.openBarrier();
            
        } else {
            System.err.println(">> Payment Declined! Barrier will remain closed.");
        }
        System.out.println("--------------------------------------------------\n");
    }

    // Generating a recipt
    private void generateReceipt(ParkingSession session, Client client) {
        System.out.println("\n[ PARKWISE RECEIPT ]");
        System.out.println("| Date: " + session.getExitTime());
        System.out.println("| Vehicle: " + session.getVehicle().getId());
        if (client != null) {
            System.out.println("| Client: " + client.getFirstName() + " " + client.getLastName());
        } else {
            System.out.println("| Client: Guest");
        }
        System.out.println("| Duration: " + session.getDurationInHours() + " Hours"); 
        System.out.println("| Total Paid: " + session.getSessionPrice() + " NIS");
        System.out.println("[ END OF RECEIPT ]\n");
    }
}