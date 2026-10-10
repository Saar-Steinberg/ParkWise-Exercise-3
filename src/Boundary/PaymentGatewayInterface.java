package Boundary;

public interface PaymentGatewayInterface {
    
    // Charges the given credit card with the given amount, returns true if charged succesfully, false otherwise
    boolean chargeCreditCard(double amount, String creditDetails);
}