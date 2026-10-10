package Main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.File;

// A class made to check our DataBase connection
public class TestDB {
    public static void main(String[] args) {
        System.out.println("--- Starting DB Connection Test ---");

        try {
            // Checking the drivers
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            System.out.println("V Step 1: Driver Class Found!");

            // Checking the file dir
            String dbPath = "ParkWiseDB.accdb"; 
            File f = new File(dbPath);
            if(f.exists() && !f.isDirectory()) { 
                System.out.println("V Step 2: DB File found at: " + f.getAbsolutePath());
            } else {
                System.err.println("X Step 2: DB File NOT found at: " + f.getAbsolutePath());
                // Get out if there is no file found
                return;
            }

            // Connection
            String url = "jdbc:ucanaccess://" + dbPath;
            Connection conn = DriverManager.getConnection(url);
            System.out.println("V Step 3: Connection Established Successfully!");
            
            conn.close();

        } catch (ClassNotFoundException e) {
            System.err.println("X Error: Driver JARs are missing from Classpath!");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("X General Error:");
            e.printStackTrace();
        }
        
        System.out.println("--- Test Finished ---");
    }
}