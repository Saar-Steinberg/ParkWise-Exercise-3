package Main; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent;   
import Control.*;
import Boundary.*;
import Entity.*;
import Enums.Size;

public class MainApp extends JFrame {

    private JPanel cardPanel;
    private CardLayout cl;

    // Controllers & Data
    private ParkingRepository repository;
    private ParkingLot parkingLot;
    private ConveyorsController conveyorsController;
    private PaymentController paymentController;
    private PricingController pricingController;

    // Colors that are used in the interface
    public static final Color COLOR_PRIMARY = new Color(44, 62, 80);    // Midnight Blue
    public static final Color COLOR_ACCENT = new Color(52, 152, 219);   // Peter River Blue
    public static final Color COLOR_SUCCESS = new Color(39, 174, 96);   // Nephritis Green
    public static final Color COLOR_BG = new Color(236, 240, 241);      // Clouds (Light Gray)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 16);
    
    public MainApp() {
        setTitle("ParkWise | Smart Parking System");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeSystem(); 

        cl = new CardLayout();
        cardPanel = new JPanel(cl);

        // Screens
        cardPanel.add(createWelcomeScreen(), "Welcome");
        cardPanel.add(new FrmAdmin(this), "Admin");
        cardPanel.add(new FrmClient(this, conveyorsController, paymentController, parkingLot), "Client");

        add(cardPanel);
        setVisible(true);
    }

    private void initializeSystem() {
        System.out.println(">> System Initialization Started...");
        repository = new ParkingRepository();
        
        System.out.println(">> Fetching ParkingLot configuration from DB...");
        parkingLot = repository.loadMainParkingLot();
        repository.loadConveyors(parkingLot);      
        repository.loadParkingSpaces(parkingLot);  
        repository.loadSessions(parkingLot);       

        System.out.println(">> Data Loaded: " + parkingLot.getActiveSessions().size() + " active sessions.");
        // In this part of the HW, we simulate all the hardware parts
        GateSensorInterface gateSensor = new GateSensorInterface() {
            @Override
            // Will always return this license numebr
            public String detectVehicle() { return "123-45-678"; }
            @Override
            public void openBarrier() { System.out.println(">> GATE OPENING <<"); }
            @Override
            public void closeBarrier() { System.out.println(">> GATE CLOSING <<"); }
        };

        PaymentGatewayInterface paymentGateway = new CreditCardSystem();
        // Setting the controllers
        pricingController = new PricingController(parkingLot.getId());
        conveyorsController = new ConveyorsController(parkingLot, gateSensor, repository); 
        paymentController = new PaymentController(paymentGateway, pricingController, parkingLot, gateSensor, repository);
    }

    private JPanel createWelcomeScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARY); 

        JLabel title = new JLabel("ParkWise Digital Portal", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(100, 0, 10, 0));

        JLabel sub = new JLabel("Smart Parking Management System", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        sub.setForeground(new Color(200, 200, 200));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(sub);
        panel.add(titleBox, BorderLayout.NORTH);

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 50, 0));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(120, 150, 150, 150));

        JButton btnAdmin = createStyledButton("Administrator", "Administrator Screen", COLOR_ACCENT);
        btnAdmin.addActionListener(e -> cl.show(cardPanel, "Admin"));

        JButton btnClient = createStyledButton("Customer", "Parking & Club Services", COLOR_SUCCESS);
        btnClient.addActionListener(e -> cl.show(cardPanel, "Client"));

        buttons.add(btnAdmin);
        buttons.add(btnClient);
        panel.add(buttons, BorderLayout.CENTER);
        
        JLabel footer = new JLabel("© ParkWise coursework project", SwingConstants.CENTER);
        footer.setForeground(new Color(100, 100, 100));
        footer.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledButton(String title, String subtitle, Color baseColor) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    g.setColor(baseColor.brighter());
                } else {
                    g.setColor(baseColor);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btn.setLayout(new BorderLayout());
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(subtitle, SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(230, 230, 230));

        btn.add(lblTitle, BorderLayout.CENTER);
        btn.add(lblSub, BorderLayout.SOUTH);
        
        return btn;
    }

    public void showWelcome() {
        cl.show(cardPanel, "Welcome");
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(MainApp::new);
    }
}
