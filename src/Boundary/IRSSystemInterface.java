package Boundary;

public interface IRSSystemInterface {
    // Acts automaticly when recives a price list in a json format from the IRS
    void onPriceListRecived(String json);
}