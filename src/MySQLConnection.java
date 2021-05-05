import java.sql.*;

public class MySQLConnection
{
    public static Connection getConnection() throws SQLException {
        //MySQL User login information
        String MySQLURL = "jdbc:mysql://167.114.145.95:3306/Inventory?useSSL=false";
        String databseUserName = "sedb";
        String databasePassword = "Grocery1?";

        Connection connection = DriverManager.getConnection(MySQLURL,databseUserName,databasePassword);
        return connection;
    }
}
