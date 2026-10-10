package Entity;
import Enums.*;

public class Vehicle {
    private String id;          // License number
    private String type;        // Car type
    private Color color;        // Car color 
    private Size size;          // Car size
    private double weight;      // Car weight
    
    // Photos are saved by their paths 
    private String frontPhoto;
    private String rearPhoto;
    private String sidePhoto;

    // Constructor
    public Vehicle(String id, String type, Color color, Size size, double weight) {
        this.id = id;
        this.type = type;
        this.color = color;
        this.size = size;
        this.weight = weight;
        this.frontPhoto = "";
        this.rearPhoto = "";
        this.sidePhoto = "";
    }
    // Empty Constructor
    public Vehicle(){
        this.id = null;
        this.type = null;
        this.color = null;
        this.size = null;
        this.weight = -1;
        this.frontPhoto = "";
        this.rearPhoto = "";
        this.sidePhoto = "";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public Size getSize() {
        return size;
    }

    public double getWeight() {
        return weight;
    }

    // Pictures getters and setters
    public void setFrontPhoto(String frontPhoto) {
        this.frontPhoto = frontPhoto;
    }

    public void setRearPhoto(String rearPhoto) {
        this.rearPhoto = rearPhoto;
    }

    public void setSidePhoto(String sidePhoto) {
        this.sidePhoto = sidePhoto;
    }
    
    public String getFrontPhoto() { return frontPhoto; }
    public String getRearPhoto() { return rearPhoto; }
    public String getSidePhoto() { return sidePhoto; }
}
