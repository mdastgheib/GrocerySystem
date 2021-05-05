import java.sql.*;

public class MySQLConnection
{
    public static void main(String[] args) {
        String MySQLURL = "jdbc:mysql://167.114.145.95:3306/Inventory?useSSL=false";
        String databseUserName = "sedb";
        String databasePassword = "Grocery1?";
        Connection con = null;
        try {
            con = DriverManager.getConnection(MySQLURL, databseUserName, databasePassword);
            if (con != null) {
                System.out.println("Database connection is successful !!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
