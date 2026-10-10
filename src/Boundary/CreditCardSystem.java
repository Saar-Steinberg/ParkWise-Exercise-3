package Boundary;

public class CreditCardSystem implements PaymentGatewayInterface {
    // Charges credit card. In this project, the payment gets always approved
    @Override
    public boolean chargeCreditCard(double amount, String creditDetails) {
        System.out.println(">> Payment System: Processing payment...");
        System.out.println(">> Card: " + creditDetails);
        System.out.println(">> Amount: " + amount + " NIS");
        
        // Proccessing simulation
        try {
            Thread.sleep(1000); // Wait 1 second to process
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(">> Payment System: Transaction APPROVED.");
        return true; 
    }
}