package Enums;
public enum Status {
    ACTIVE,          // Conveyor is currently active
    PAUSED,          // Conveyor paused
    OFF,             // Conveyor is off
    INTEGRITY_CHECK, // Conveyor in integrity check
    OPERATIONAL      // Conveyor is ready to park a car
}