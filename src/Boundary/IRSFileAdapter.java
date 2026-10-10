package Boundary;
import Control.*;
import java.util.ArrayList;
import java.util.List;

public class IRSFileAdapter implements IRSSystemInterface {
    
    // A list of all controllers - in order to update price lists to all parking lots
    private List<PricingController> subscribedControllers;

    public IRSFileAdapter() {
        this.subscribedControllers = new ArrayList<>();
    }

    // Adds controller to the list of controllers
    public void addController(PricingController controller) {
        this.subscribedControllers.add(controller);
    }

    @Override
    public void onPriceListRecived(String json) {
        System.out.println(">> IRS Adapter: New price list received from server.");
        
        
        for (PricingController controller : subscribedControllers) {
            
            // Import the relevant price list for each parking lot
            controller.importPriceList(json);
        }
        
        System.out.println(">> IRS Adapter: Update broadcasted to " + subscribedControllers.size() + " parking lots.");
    }
}
