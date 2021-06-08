/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author gjm36
 */
public class YtexManagerIncSta3n {

//    private final static Logger LOGGER = Logger.getLogger(YtexManager.class.getName());

    public static void main(String[] args) {

        YtexManagerIncSta3n ytex = new YtexManagerIncSta3n();
        List stationsAsList = ytex.getActiveStationsPS();

        for (Iterator iterator = stationsAsList.iterator(); iterator.hasNext();) {
            Object nextStation = iterator.next();
            ytex.runSPUpdateProceduresSta3nStoredProcedure(new HashMap<String, Integer>() {
                {
                    put("sta3n", (Integer) nextStation);
                }
            });

        }
        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
            {
                put("func", "runSPUpdateProceduresSta3nStoredProcedure()");
                put("message", "Update Procedures Sta3n Done");
            }
        });

              ytex.runDeleteDocumentPS();
              
        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
            {
                put("func", "runDeleteDocumentPS()");
                put("message", "runDeleteDocumentPS Done");
            }
        });
//        for (Iterator iterator = stationsAsList.iterator(); iterator.hasNext();) {
//            Object nextStation = iterator.next();
//            ytex.runSPCopyRadnucMedReports74StoredProcedure(new HashMap<String, Integer>() {
//                {
//                    put("sta3n", (Integer) nextStation);
//                }
//            });
//        }
//        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
//            {
//                put("func", "runSPCopyRadnucMedReports74StoredProcedure()");
//                put("message", "Copy Radnuc Med Reports 74 Done");
//            }
//        });

//        for (Iterator iterator = stationsAsList.iterator(); iterator.hasNext();) {
//            Object nextStation = iterator.next();
//
//            ytex.runSPUpdateReportsToClassifyStoredProcedure(new HashMap<String, Integer>() {
//                {
//                    put("sta3n", (Integer) nextStation);
//                }
//            });
//        }
//        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
//            {
//                put("func", "runSPUpdateReportsToClassifyStoredProcedure()");
//                put("message", "Update Reports To Classify Done");
//            }
//        });

        ytex.runSPRetrieveReportForSearchStoredProcedure();
        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
            {
                put("func", "runSPRetrieveReportForSearchStoredProcedure()");
                put("message", "Retrieve Report For Search Done");
            }
        });

        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
            {
                put("func", "runCPE.bat");
                put("message", "Starting Ytex Pipeline");
            }
        });

        ytex.runYtexPipeline("c:\\clinicalnlp\\ytex\\ccts\\runCPE.bat");
        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
            {
                put("func", "runCPE.bat");
                put("message", "Compeleted Ytex Pipeline");
            }
        });

        ytex.runSPOncologyLungSearchStoredProcedure();
        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
            {
                put("func", "runSPOncologyLungSearchStoredProcedure()");
                put("message", "Oncology Lung Search Done");
            }
        });

        ytex.runSPLocationStoredProcedure();
        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
            {
                put("func", "runSPLocationStoredProcedure()");
                put("message", "Location Done");
            }
        });

        ytex.runSPOncologyLiverSearchStoredProcedure();
        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
            {
                put("func", "runSPOncologyLiverSearchStoredProcedure()");
                put("message", "Oncology Liver Search Done");
            }
        });

        ytex.runSPOncologyOtherSearchStoredProcedure();
        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
            {
                put("func", "runSPOncologyOtherSearchStoredProcedure()");
                put("message", "Oncology Other Search Done");
            }
        });

        ytex.runSPUpdateSearchResultsStoredProcedure();
        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
            {
                put("func", "runSPUpdateSearchResultsStoredProcedure()");
                put("message", "Update Search Results Done");
            }
        });

//        for (Iterator iterator = stationsAsList.iterator(); iterator.hasNext();) {
//            Object nextStation = iterator.next();
//            ytex.runSPUpdateSearchResultsAlertStoredProcedure(new HashMap<String, Integer>() {
//                {
//                    put("sta3n", (Integer) nextStation);
//                }
//            });
//        }

//        ytex.runSPUpdateRadnucMedReports74StoredProcedure();
//        ytex.runSPLogStoredProcedure(new HashMap<String, String>() {
//            {
//                put("func", "runSPUpdateRadnucMedReports74StoredProcedure()");
//                put("message", "Update Radnuc Med Reports 74 Done");
//            }
//        });

    }

    private void runSPUpdateSearchResultsStoredProcedure() {
        CallableStatement cstmt = null;
        SqlConnection connection = new SqlConnection();

        try {
            cstmt = connection.getConnection().prepareCall("{call stex.SP_update_search_results}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

//    private void runSPUpdateRadnucMedReports74StoredProcedure() {
//        CallableStatement cstmt = null;
//        SqlConnection connection = new SqlConnection();
//
//        try {
//            cstmt = connection.getConnection().prepareCall("{call stex.SP_update_radnuc_med_reports_74}");
//            cstmt.execute();
//        } catch (Exception ex) {
//            Logger.getLogger(YtexManager.class.getName()).log(
//                    Level.SEVERE, null, ex);
//        } finally {
//            if (cstmt != null) {
//                try {
//                    cstmt.close();
//                } catch (SQLException ex) {
//                    Logger.getLogger(YtexManager.class.getName()).log(
//                            Level.WARNING, null, ex);
//                }
//            }
//        }
//
//    }

    
//    private void runSPUpdateSearchResultsAlertStoredProcedure(HashMap params) {
//        CallableStatement cstmt = null;
//        SqlConnection connection = new SqlConnection();
//
//        try {
//            cstmt = connection.getConnection().prepareCall("{call stex.SP_update_search_results_alert(?)}");
//            cstmt.setInt(1, (Integer) params.get("sta3n"));
//            cstmt.execute();
//        } catch (Exception ex) {
//            Logger.getLogger(YtexManager.class.getName()).log(
//                    Level.SEVERE, null, ex);
//        } finally {
//            if (cstmt != null) {
//                try {
//                    cstmt.close();
//                } catch (SQLException ex) {
//                    Logger.getLogger(YtexManager.class.getName()).log(
//                            Level.WARNING, null, ex);
//                }
//            }
//        }
//
//    }

    private void runSPLogStoredProcedure(HashMap params) {
        CallableStatement cstmt = null;
        SqlConnection connection = new SqlConnection();

        try {
            cstmt = connection.getConnection().prepareCall("{call stex.SP_log(?,?)}");
            cstmt.setString(1, (String) params.get("func"));
            cstmt.setString(2, (String) params.get("message"));
            //   cstmt.registerOutParameter("averageWeight", java.sql.Types.DECIMAL);
            cstmt.execute();
            //  averageWeight = cstmt.getDouble("averageWeight");
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

//    private void runSPUpdateReportsToClassifyStoredProcedure(HashMap params) {
//        CallableStatement cstmt = null;
//        SqlConnection connection = new SqlConnection();
//        try {
//            cstmt = connection.getConnection().prepareCall("{call stex.SP_update_reports_to_classify(?)}");
//            cstmt.setInt(1, (Integer) params.get("sta3n"));
//            cstmt.execute();
//        } catch (Exception ex) {
//            Logger.getLogger(YtexManager.class.getName()).log(
//                    Level.SEVERE, null, ex);
//        } finally {
//            if (cstmt != null) {
//                try {
//                    cstmt.close();
//                } catch (SQLException ex) {
//                    Logger.getLogger(YtexManager.class.getName()).log(
//                            Level.WARNING, null, ex);
//                }
//            }
//        }
//
//    }

    private void runSPUpdateProceduresSta3nStoredProcedure(HashMap params) {
        CallableStatement cstmt = null;
        SqlConnection connection = new SqlConnection();
        try {
            cstmt = connection.getConnection().prepareCall("{call stex.SP_update_procedures_sta3n(?)}");
            cstmt.setInt(1, (Integer) params.get("sta3n"));
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

    private void runSPRetrieveReportForSearchStoredProcedure() {
        CallableStatement cstmt = null;
        SqlConnection connection = new SqlConnection();

        try {
            cstmt = connection.getConnection().prepareCall("{call stex.SP_retrieve_reports_for_search}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

    private void runSPOncologyOtherSearchStoredProcedure() {
        CallableStatement cstmt = null;
        SqlConnection connection = new SqlConnection();

        try {
            cstmt = connection.getConnection().prepareCall("{call stex.SP_oncology_other_search}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

    private void runSPOncologyLiverSearchStoredProcedure() {
        CallableStatement cstmt = null;
        SqlConnection connection = new SqlConnection();

        try {
            cstmt = connection.getConnection().prepareCall("{call stex.SP_oncology_liver_search}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

    private void runSPOncologyLungSearchStoredProcedure() {
        CallableStatement cstmt = null;
        SqlConnection connection = new SqlConnection();

        try {
            cstmt = connection.getConnection().prepareCall("{call stex.SP_oncology_lung_search}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

    private void runSPLocationStoredProcedure() {
        CallableStatement cstmt = null;
        SqlConnection connection = new SqlConnection();

        try {
            cstmt = connection.getConnection().prepareCall("{call stex.SP_Location}");
            cstmt.execute();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }

    }

//    private void runSPCopyRadnucMedReports74StoredProcedure(HashMap params) {
//        CallableStatement cstmt = null;
//        SqlConnection connection = new SqlConnection();
//        try {
//            cstmt = connection.getConnection().prepareCall("{call stex.SP_copy_radnuc_med_reports_74(?)}");
//            cstmt.setInt(1, (Integer) params.get("sta3n"));
//            cstmt.execute();
//        } catch (Exception ex) {
//            Logger.getLogger(YtexManager.class.getName()).log(
//                    Level.SEVERE, null, ex);
//        } finally {
//            if (cstmt != null) {
//                try {
//                    cstmt.close();
//                } catch (SQLException ex) {
//                    Logger.getLogger(YtexManager.class.getName()).log(
//                            Level.WARNING, null, ex);
//                }
//            }
//        }
//
//    }

    private List getActiveStationsPS() {
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
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }
        return stationsAsList;
    }

    private void runYtexPipeline(String pathFilename) {
        final File ytexBatchFile = new File(pathFilename);

        // The output file. All activity is written to this file
        final File outputFile = new File(String.format("output_%tY%<tm%<td_%<tH%<tM%<tS.txt",
                System.currentTimeMillis()));

        // The argument to the batch file. 
        // final String argument = "Albert Attard";
        // Create the process
        //  final ProcessBuilder processBuilder = new ProcessBuilder(ytexBatchFile.getAbsolutePath());
        final ProcessBuilder processBuilder = new ProcessBuilder(ytexBatchFile.getPath());
        // Redirect any output (including error) to a file. This avoids deadlocks
        // when the buffers get full. 
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(outputFile);

        // Add a new environment variable
        // processBuilder.environment().put("message", "Example of process builder");
        // Set the working directory. The batch file will run as if you are in this
        // directory.
        //  processBuilder.directory(new File("work"));
        // Start the process and wait for it to finish. 
        Process process = null;
        int exitStatus;
        try {
            process = processBuilder.start();
            exitStatus = process.waitFor();
            System.out.println("Processed finished with status: " + exitStatus);
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void runDeleteDocumentPS() {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        SqlConnection connection = new SqlConnection();
        try {
            String sql = "DELETE FROM stex.document";
            pstmt = connection.getConnection().prepareStatement(sql);
            rs = pstmt.executeQuery();
        } catch (Exception ex) {
            Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(YtexManagerIncSta3n.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }
    }
}
