package Boundary;

public interface GateSensorInterface {
    // Detects the vehicle that arrived to the gate
    String detectVehicle();

    // Opens the barrier at the gate
    void openBarrier();

    // Closes the barrier at the gate
    void closeBarrier();
}
