package Boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Control.ReportLogic;

public class FrmReport extends JFrame {
    private JTextField txtYear;
    private JButton btnGenerate;

    public FrmReport() {
        setTitle("ParkWise Management Dashboard");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30));

        // Components
        add(new JLabel("Enter Report Year:"));
        txtYear = new JTextField(10);
        add(txtYear);

        btnGenerate = new JButton("Generate Summary Report");
        add(btnGenerate);

        // Action when button is clicked
       btnGenerate.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        try {
            int year = Integer.parseInt(txtYear.getText());
            ReportLogic logic = new ReportLogic();
            
            logic.compileAnnualSummaryReport(year); // Creating the report using ReportLogic
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid year.");
        }
    }
});

}


}