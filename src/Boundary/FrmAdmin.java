package Boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent;   
import Control.ReportLogic;
import Main.MainApp; 

public class FrmAdmin extends JPanel {

    private MainApp mainApp; 
    private ReportLogic reportLogic;

    public FrmAdmin(MainApp mainApp) {
        this.mainApp = mainApp;
        this.reportLogic = new ReportLogic();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(MainApp.COLOR_BG); 

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MainApp.COLOR_PRIMARY); 
        header.setPreferredSize(new Dimension(1000, 70));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnBack = new JButton("← Back");
        styleSmallButton(btnBack); 
        btnBack.addActionListener(e -> mainApp.showWelcome()); 

        JLabel lblTitle = new JLabel("Administrator Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(MainApp.FONT_TITLE.deriveFont(24f)); 
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(btnBack, BorderLayout.WEST);
        header.add(lblTitle, BorderLayout.CENTER);
        
        JPanel dummy = new JPanel(); dummy.setOpaque(false); dummy.setPreferredSize(btnBack.getPreferredSize());
        header.add(dummy, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Admin options
        JPanel grid = new JPanel(new GridLayout(2, 2, 30, 30));
        grid.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        grid.setBackground(MainApp.COLOR_BG); 

        grid.add(createCard("Annual Summary Report",
                "View yearly income and vehicle statistics",
                MainApp.COLOR_ACCENT, 
                this::generateReport));

        grid.add(createCard("Activate Conveyors",
                "Run daily integrity checks",
                MainApp.COLOR_SUCCESS, 
                () -> JOptionPane.showMessageDialog(this, "Conveyors activated")));

        grid.add(createCard("Club Growth Report",
                "View monthly membership statistics",
                new Color(155, 89, 182),
                () -> JOptionPane.showMessageDialog(this, "Club growth report")));

        grid.add(createCard("Parking Lots Management",
                "Add, Update, Read, Delete",
                new Color(241, 196, 15),
                () -> JOptionPane.showMessageDialog(this, "Parking lots managenent")));

        add(grid, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, String desc, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblDesc = new JLabel("<html><body style='width:220px'>" + desc + "</body></html>");
        lblDesc.setForeground(Color.DARK_GRAY);

        JButton btn = createFlatButton("Open", accent);
        btn.addActionListener(e -> action.run());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(btn);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    // Helper method to create a truly flat colored button
    private JButton createFlatButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g.setColor(color.brighter());
                } else {
                    g.setColor(color);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(MainApp.FONT_BUTTON.deriveFont(14f));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); 
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void generateReport() {
        String year = JOptionPane.showInputDialog(this, "Enter Year:");
        if (year != null && !year.isEmpty()) {
            reportLogic.compileAnnualSummaryReport(Integer.parseInt(year));
        }
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
}