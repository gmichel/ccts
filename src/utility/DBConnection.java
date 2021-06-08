package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by VHACONMICHEG on 6/4/2017.
 */
public class DBConnection {

    public Connection getDataBaseConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ytex","ytex","ytex");
        } catch (Exception ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }


}
