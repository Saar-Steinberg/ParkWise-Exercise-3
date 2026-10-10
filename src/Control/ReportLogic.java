package Control;

import java.sql.Connection;
import java.util.HashMap;
import javax.swing.JOptionPane;
import utils.DBConnection; 
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.view.JasperViewer;


 // Logic Controller that handles reports generation
public class ReportLogic {

    public void compileAnnualSummaryReport(int year) {
        try {
            Connection conn = DBConnection.getInstance().getConnection(); // Getting the DB connection
            
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Could not establish database connection.");
                return;
            }

            // Preaping the parameters to generate the report
            HashMap<String, Object> params = new HashMap<>();
            params.put("pYear", Integer.valueOf(year));
            
            System.out.println("Generating report for year: " + year);

            JasperPrint print = JasperFillManager.fillReport(
                getClass().getResourceAsStream("/Boundary/AnnualReport.jasper"), 
                params, conn);

            // If there is no data for the inputed year
            if (print.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No data found for the year: " + year);
            } else {
                // Display the report in the viewer
                JasperViewer.viewReport(print, false);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error generating report: " + e.getMessage());
        }
    }
}