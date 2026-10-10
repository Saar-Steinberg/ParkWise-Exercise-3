package Entity;

import java.util.Date;

public class PriceList {
    
    private int id;                // Price list's id 
    private Date effectiveDate;    // Price list's effective date
    private double firstHourPrice; // First hour price
    private double extraHourPrice; // Extra hour price
    private double fullDayPrice;   // Full day price

    // Constructor
    public PriceList(int id, Date effectiveDate, double firstHourPrice, double extraHourPrice, double fullDayPrice) {
        this.id = id;
        this.effectiveDate = effectiveDate;
        this.firstHourPrice = firstHourPrice;
        this.extraHourPrice = extraHourPrice;
        this.fullDayPrice = fullDayPrice;
    }
    // Empty Constructor
    public PriceList(){
        this.id = -1;
        this.effectiveDate = null;
        this.firstHourPrice = -1;
        this.extraHourPrice = -1;
        this.fullDayPrice = -1;
    }

    // Getters 
    public int getId() { return id; }
    public Date getEffectiveDate() { return effectiveDate; }
    public double getFirstHourPrice() { return firstHourPrice; }
    public double getExtraHourPrice() { return extraHourPrice; }
    public double getFullDayPrice() { return fullDayPrice; }
}
