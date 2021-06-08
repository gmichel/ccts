/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gjm36
 */
public class SqlConnection {


    private static Connection connection = null;
    private static SQLServerDataSource dataSource = new SQLServerDataSource();
  //  private final static Logger LOGGER = Logger.getLogger(YtexManager.class.getName());

    /**
     * @return the dataSource
     */
    public static SQLServerDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Constructor
     */
    public SqlConnection() {
        try {          
            createConnection();
        } catch (Exception ex) {         
             Logger.getLogger(SqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @return @throws Exception
     */
    private  Connection createConnection() {

         Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://vhacdwa01.vha.med.va.gov:1433;databaseName=psci_ccts;"
                    + "integratedSecurity=true;");
        } catch (Exception ex) {
            Logger.getLogger(SqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
}

