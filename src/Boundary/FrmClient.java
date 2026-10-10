package Boundary;

import Control.*;
import Entity.*;
import Enums.Size;
import Main.MainApp; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent;   
import java.time.format.DateTimeFormatter;

public class FrmClient extends JPanel {

    private MainApp mainApp; 
    private ConveyorsController conveyorsController;
    private PaymentController paymentController;
    private ParkingLot parkingLot;

    public FrmClient(MainApp mainApp, 
                     ConveyorsController conveyorsController, 
                     PaymentController paymentController,
                     ParkingLot parkingLot) {
        this.mainApp = mainApp;
        this.conveyorsController = conveyorsController;
        this.paymentController = paymentController;
        this.parkingLot = parkingLot;
        
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(MainApp.COLOR_BG); 

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MainApp.COLOR_SUCCESS); 
        header.setPreferredSize(new Dimension(1000, 70));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnBack = new JButton("← Back");
        styleSmallButton(btnBack); 
        btnBack.addActionListener(e -> mainApp.showWelcome());
        
        JLabel lblTitle = new JLabel("Customer Service Portal");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(MainApp.FONT_TITLE.deriveFont(24f)); 

        header.add(btnBack, BorderLayout.WEST);
        header.add(lblTitle, BorderLayout.CENTER);
        
        JPanel dummy = new JPanel(); dummy.setOpaque(false); dummy.setPreferredSize(btnBack.getPreferredSize());
        header.add(dummy, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Client options on screen
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20)); 
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100)); 

        grid.add(createActionPanel(" Enter Parking", "Park your vehicle now", e -> handleEnterParking()));
        grid.add(createActionPanel(" My Status", "Check vehicle location", e -> handleViewStatus()));
        grid.add(createActionPanel(" Exit & Pay", "Retrieve vehicle and pay", e -> handleExitParking()));
        grid.add(createActionPanel(" Become a Member", "Join our VIP club", e -> handleBecomeMember()));

        add(grid, BorderLayout.CENTER);
    }
    // Become a member button
    private void handleBecomeMember() {
        JOptionPane.showMessageDialog(this, 
            "Congratulations!\nYou have successfully joined the ParkWise VIP Club.\nEnjoy exclusive benefits!", 
            "Welcome to the Club", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    // Entering parking logic
    private void handleEnterParking() {
        String licensePlate = JOptionPane.showInputDialog(this, "Enter Vehicle License Plate:");
        if (licensePlate == null || licensePlate.trim().isEmpty()) return;
        // Created the same vehicle for testing purpuses (besides id)
        Vehicle vehicle = new Vehicle(licensePlate, "Sedan", Enums.Color.OTHER, Size.MEDIUM, 1500.0);
        
        if (parkingLot.getActiveSessions().stream().anyMatch(s -> s.getVehicle().getId().equals(licensePlate))) {
            JOptionPane.showMessageDialog(this, "Vehicle is already inside!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ParkingSession session = new ParkingSession(generateId(), vehicle, parkingLot.getId());
        parkingLot.addActiveSession(session); 

        new Thread(() -> {
            conveyorsController.parkVehicle(session);
            SwingUtilities.invokeLater(() -> {
                if (session.getParkingSpace() != null) {
                    JOptionPane.showMessageDialog(this, 
                        "Vehicle Parked Successfully!\nSpace: " + session.getParkingSpace().getId());
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Parking Failed!\nNo suitable space or conveyor found.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    parkingLot.getActiveSessions().remove(session);
                }
            });
        }).start();
    }
    // Handling view details button
    private void handleViewStatus() {
        String licensePlate = JOptionPane.showInputDialog(this, "Enter Vehicle License Plate to check status:");
        if (licensePlate == null || licensePlate.trim().isEmpty()) return;

        ParkingSession session = parkingLot.getActiveSessions().stream()
                .filter(s -> s.getVehicle().getId().equals(licensePlate))
                .findFirst()
                .orElse(null);

        if (session != null && session.getParkingSpace() != null) {
            ParkingSpace space = session.getParkingSpace();
            String msg = String.format("<html><body style='width: 250px; font-family: Segoe UI;'>" +
                         "<h2 style='color: #27ae60;'>Parking Status</h2>" +
                         "<b>Vehicle:</b> %s<br>" +
                         "<b>Lot:</b> %s<br>" +
                         "<br>" +
                         "<b style='font-size: 14px'>Location: Floor %d, Space %d</b><br>" +
                         "<br>" +
                         "<b>Entry Time:</b> %s<br>" +
                         "<b>Duration:</b> %.2f Hours" +
                         "</body></html>",
                         session.getVehicle().getId(),
                         parkingLot.getName(),
                         space.getFloor(),
                         space.getId(),
                         session.getEntryTime().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")),
                         session.getDurationInHours());

            JOptionPane.showMessageDialog(this, msg, "Vehicle Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Vehicle not found in this parking lot.", "Status Check", JOptionPane.WARNING_MESSAGE);
        }
    }
    // Handling exit parking - ends session and brings back the car to the gate
    private void handleExitParking() {
        String licensePlate = JOptionPane.showInputDialog(this, "Enter Vehicle License Plate to Retrieve:");
        if (licensePlate == null || licensePlate.trim().isEmpty()) return;

        ParkingSession session = parkingLot.getActiveSessions().stream()
                .filter(s -> s.getVehicle().getId().equals(licensePlate))
                .findFirst()
                .orElse(null);

        if (session == null) {
            JOptionPane.showMessageDialog(this, "Vehicle not found in the parking lot.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Get the car to the gate while customer pays
        new Thread(() -> {
            conveyorsController.retrieveVehicle(session);
            SwingUtilities.invokeLater(() -> {
                String cc = JOptionPane.showInputDialog(this, "Vehicle at gate.\nEnter Credit Card Number:");
                if (cc != null && !cc.isEmpty()) {
                    paymentController.processPayment(session, cc);
                    JOptionPane.showMessageDialog(this, "Payment Approved. Have a safe trip!");
                } else {
                    JOptionPane.showMessageDialog(this, "Payment Cancelled.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            });
        }).start();
    }

    private JPanel createActionPanel(String title, String subtitle, java.awt.event.ActionListener action) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        Color btnColor = new Color(52, 152, 219);

        JButton btn = new JButton(title) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(btnColor.darker());
                } else if (getModel().isRollover()) {
                    g.setColor(btnColor.brighter());
                } else {
                    g.setColor(btnColor);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };

        btn.setFont(MainApp.FONT_BUTTON); 
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);

        JLabel sub = new JLabel(subtitle, SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13)); 
        sub.setForeground(Color.GRAY);
        sub.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        p.add(btn, BorderLayout.CENTER);
        p.add(sub, BorderLayout.SOUTH);
        return p;
    }
    
    private void styleSmallButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(255, 255, 255, 50)); 
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); }
            public void mouseExited(MouseEvent e) { btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); }
        });
    }

    private void returnToMain() {
        try { mainApp.showWelcome(); } catch (Exception ex) { ex.printStackTrace(); }
    }

    private int generateId() { return (int)(Math.random() * 10000); }
}