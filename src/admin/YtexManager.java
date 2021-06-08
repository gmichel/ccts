package admin;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.DefaultFileSystem;
import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This program initiates the YTEX NLP pipeline
 *
 * @author George Michel - 12/19/17
 */
public class YtexManager {

    ProjectProperties projectProperties = new ProjectProperties();

    public ProjectProperties getProjectProperties()
    {
        return projectProperties;
    }




    public static void main(String[] args)
    {
        YtexManager ytex = new YtexManager();
        ytex.run(ytex);
    }


    public void run(YtexManager ytex)
    {
        Parameters params = new Parameters();
// Read data from this file
        Configurations configs = new Configurations();
        DefaultFileSystem dfs = new DefaultFileSystem();
        String bbb = dfs.getBasePath("config.properties");
        File propertiesFile = new File("config.properties");
      //  Configuration config = builder.getConfiguration();
    //    String ctakesHome = getProjectProperties().init("config.properties").getProperty("ctakes.home");
        String[] ytexCommand = null;
        try {
            PropertiesConfiguration config = configs.properties(propertiesFile);
            ytexCommand= new String[]{"java", "-cp",

                    config.getString("ctakes.classpath"),
                    config.getString("ctakes.log4j"),
                    config.getString("ctakes.minmem"),
                    config.getString("ctakes.maxmem"),
                    config.getString("ctakes.command")
                    ,config.getString("ctakes.cpe")
                    ,config.getString("ctakes.target")
            };

        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

    //    ytex.runYtexPipeline("D:\\apache-ctakes-4.0.0\\bin\\trainingValidationRunPositive.bat");
        System.out.println(Arrays.toString(ytexCommand));
    //    ytex.runYtexPipeline(ytexCommand);
    }

    private List getActiveStationsPS()
    {
        List stationsAsList = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        SqlConnection connection = new SqlConnection();
        try {
            String sql = "SELECT station_number AS sta3n FROM";
            sql += " ccts.institution_4";
            sql += " WHERE Active = 'Y'";

            pstmt = connection.getConnection().prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                stationsAsList.add(rs.getInt("sta3n"));
            }
        } catch (Exception ex) {
            Logger.getLogger(YtexManager.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManager.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManager.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }
        return stationsAsList;
    }

    private void runYtexPipeline(String[] ytexCommand)
    {
      //  final File ytexBatchFile = new File(pathFilename);

        // The output file. All activity is written to this file
        final File outputFile = new File(String.format("output_%tY%<tm%<td_%<tH%<tM%<tS.txt",
                System.currentTimeMillis()));

        // Create the process
     //   final ProcessBuilder processBuilder = new ProcessBuilder(ytexBatchFile.getPath());
        final ProcessBuilder processBuilder = new ProcessBuilder(ytexCommand);
        // Redirect any output (including error) to a file. This avoids deadlocks
        // when the buffers get full. 
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(outputFile);
        // Start the process and wait for it to finish. 
        Process process = null;
        int exitStatus;
        try {
            process = processBuilder.start();
            exitStatus = process.waitFor();
            System.out.println("Processed finished with status: " + exitStatus);
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
            Logger.getLogger(YtexManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void runDeleteDocumentPS()
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        SqlConnection connection = new SqlConnection();
        try {
            String sql = "DELETE FROM etex.document";
            pstmt = connection.getConnection().prepareStatement(sql);
            rs = pstmt.executeQuery();
        } catch (Exception ex) {
            Logger.getLogger(YtexManager.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManager.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManager.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }
    }
}
