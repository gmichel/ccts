package admin;

import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This program initiates the YTEX NLP pipeline and associated stored procedures.
 * 
 * @author George Michel - 6/2016
 */
public class YtexManagerValidation {

    public static void main(String[] args) {

        YtexManagerValidation ytex = new YtexManagerValidation();
       // List stationsAsList = ytex.getActiveStationsPS();

//        for (Iterator iterator = stationsAsList.iterator(); iterator.hasNext();) {
//            Object nextStation = iterator.next();
//            ytex.runSPUpdateProceduresSta3nStoredProcedure(new HashMap<String, Integer>() {
//                {
//                    put("sta3n", (Integer) nextStation);
//                }
//            });
//
//        }
//        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
//            {
//                put("func", "runSPUpdateProceduresSta3nStoredProcedure()");
//                put("message", "Update Procedures Sta3n Done");
//            }
//        });
//
//              ytex.runDeleteDocumentPS();
//              
//        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
//            {
//                put("func", "runDeleteDocumentPS()");
//                put("message", "runDeleteDocumentPS Done");
//            }
//        });


//        ytex.runSPRetrieveReportForSearchStoredProcedure();
//        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
//            {
//                put("func", "runSPRetrieveReportForSearchStoredProcedure()");
//                put("message", "Retrieve Report For Search Done");
//            }
//        });



        ytex.runYtexPipeline("c:\\clinicalnlp\\ytex\\ccts\\runCPE.bat");  
   
   
       ytex.runSPLocationStoredProcedure();
       
        ytex.runSPOncologyLungSearchStoredProcedure();
        

 
//        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
//            {
//                put("func", "runSPLocationStoredProcedure()");
//                put("message", "Location Done");
//            }
//        });

        ytex.runSPOncologyLiverSearchStoredProcedure();
        

        ytex.runSPOncologyOtherSearchStoredProcedure();
        

    }

    private void runSPUpdateSearchResultsStoredProcedure() {
        CallableStatement cstmt = null;
      //  SqlConnection connection = new SqlConnection();
DBConnection connection = new DBConnection();
        try {
            cstmt = connection.getDataBaseConnection().prepareCall("{call stex.SP_update_search_results}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerValidation.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerValidation.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }


 
    private void runSPUpdateProceduresSta3nStoredProcedure(HashMap params) {
        CallableStatement cstmt = null;
      //  SqlConnection connection = new SqlConnection();
      DBConnection connection = new DBConnection();
        try {
            cstmt = connection.getDataBaseConnection().prepareCall("{call stex.SP_update_procedures_sta3n(?)}");
            cstmt.setInt(1, (Integer) params.get("sta3n"));
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerValidation.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerValidation.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

    private void runSPRetrieveReportForSearchStoredProcedure() {
        CallableStatement cstmt = null;
      //  SqlConnection connection = new SqlConnection();
DBConnection connection = new DBConnection();
        try {
            cstmt = connection.getDataBaseConnection().prepareCall("{call stex.SP_retrieve_reports_for_search}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerValidation.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerValidation.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

    private void runSPOncologyOtherSearchStoredProcedure() {
        CallableStatement cstmt = null;
       // SqlConnection connection = new SqlConnection();
DBConnection connection = new DBConnection();
        try {
            cstmt = connection.getDataBaseConnection().prepareCall("{call stex.SP_oncology_other_search}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerValidation.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerValidation.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

    private void runSPOncologyLiverSearchStoredProcedure() {
        CallableStatement cstmt = null;
      //  SqlConnection connection = new SqlConnection();
DBConnection connection = new DBConnection();
        try {
            cstmt = connection.getDataBaseConnection().prepareCall("{call stex.SP_oncology_liver_search}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerValidation.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerValidation.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

    private void runSPOncologyLungSearchStoredProcedure() {
        CallableStatement cstmt = null;
      //  SqlConnection connection = new SqlConnection();
DBConnection connection = new DBConnection();
        try {
            cstmt = connection.getDataBaseConnection().prepareCall("{call stex.SP_oncology_lung_search}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerValidation.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerValidation.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

    private void runSPLocationStoredProcedure() {
        CallableStatement cstmt = null;
      //  SqlConnection connection = new SqlConnection();
DBConnection connection = new DBConnection();
        try {
            cstmt = connection.getDataBaseConnection().prepareCall("{call stex.SP_location}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerValidation.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerValidation.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

     private void runYtexPipeline(String pathFilename) {
        final File ytexBatchFile = new File(pathFilename);

        // The output file. All activity is written to this file
        final File outputFile = new File(String.format("output_%tY%<tm%<td_%<tH%<tM%<tS.txt",
                System.currentTimeMillis()));

        // Create the process
        final ProcessBuilder processBuilder = new ProcessBuilder(ytexBatchFile.getPath());
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
            Logger.getLogger(YtexManagerValidation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
  private void runSPLogStoredProcedure(HashMap params) {
        CallableStatement cstmt = null;
       // SqlConnection connection = new SqlConnection();
 DBConnection connection = new DBConnection();
        try {
            cstmt = connection.getDataBaseConnection().prepareCall("{call stex.SP_log(?,?)}");
            cstmt.setString(1, (String) params.get("func"));
            cstmt.setString(2, (String) params.get("message"));
            //   cstmt.registerOutParameter("averageWeight", java.sql.Types.DECIMAL);
            cstmt.execute();
            //  averageWeight = cstmt.getDouble("averageWeight");
        } catch (Exception ex) {
            Logger.getLogger(YtexManager.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManager.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

   
}
