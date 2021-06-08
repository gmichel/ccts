package ml;

import utility.DBConnection;
import utility.SVMModel;
import libsvm.*;
import utility.SqlQueryResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * Created by Silmaril on 3/21/17.
 * Evaluates a single new document by documentID - post ctakes processing
 */
public class NationalEvaluateDocument {

    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();
    InstanceDataFormat instanceDataFormat = new InstanceDataFormat();

    public void run(String term_matrix_type, int docID)
    {
        instanceDataFormat = new InstanceDataFormat();

        String modelName = "libsvm" + term_matrix_type  + ".model";

        svm_model svm_model = new svm_model();

        try {

            if (new File(modelName).exists()) {
                svm_model = svm.svm_load_model(modelName);
            } else {

                String sqlQuery = "SELECT ModelValue FROM svm.NationalModel WHERE ModelOrgan=(?)";
                PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(sqlQuery);
                ps.setString(1, term_matrix_type);
                ResultSet rs = ps.executeQuery();

                BufferedReader brModel = null;
                while (rs.next()) {
                    brModel = new BufferedReader(new StringReader(rs.getString("ModelValue")));
                }
                ps.close();
                rs.close();
                if (brModel == null) {
                    return;
                }
                SVMModel mySVMModel = new SVMModel();
                svm_model = mySVMModel.svm_load_model(brModel);
            }
        } catch (IOException e) {
            System.err.println("Got an exception loading model!");
            System.err.println("Error is:" + e.getMessage());
        } catch (SQLException se) {
            System.err.println("Got SQL exception loading model!");
            System.err.println("Error is:" + se.getMessage());
        }

        instanceDataFormat.captureData(docID, term_matrix_type);

        evaluate_document_vector(docID, svm_model, term_matrix_type);
    }

    public void evaluate_document_vector(int docID, svm_model svm_model, String term_matrix_type)
    {
        String termVec = instanceDataFormat.getResTermVec();
        System.out.println(instanceDataFormat.instanceUniqueConcepts);
        int[] indexes = parseIntArray(termVec.replaceAll("(\\d+):\\d+", "$1").split("\\s"));
        double[] values = parseDoubleArray(termVec.replaceAll("\\d+:(\\d+)", "$1").split("\\s"));

        svm_node[] nodes = new svm_node[values.length];

        for (int i = 0; i < values.length; i++) {
            svm_node node = new svm_node();
            node.index = indexes[i];
            node.value = values[i];
            nodes[i] = node;
        }

        int totalClasses = svm.svm_get_nr_class(svm_model);
        int[] labels = new int[totalClasses];
        svm.svm_get_labels(svm_model, labels);

        double[] prob_estimates = new double[totalClasses];
        double v = svm.svm_predict_probability(svm_model, nodes, prob_estimates);
        double norm = svm.svm_predict(svm_model, nodes);
       // System.out.println("norm pred="+(long) norm);
        String stripped_v = "" + (long) v;

        try {
            String insertDTV = "UPDATE svm.DocumentTermVector SET term_vector = '" + stripped_v + " " + instanceDataFormat.getResTermVec() + "'  where document_id = " + docID + "\n" +
                    "IF @@ROWCOUNT = 0\n" +
                    " BEGIN\n" +
                    "  INSERT INTO svm.DocumentTermVector VALUES(?,?)\n" +
                    " END\n";

            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insertDTV);
            ps.setString(1, stripped_v + " " + instanceDataFormat.getResTermVec());
            ps.setInt(2, docID);
            ps.execute();
            ps.close();

        } catch (Exception e) {
            System.err.println("Got an exception in insertDTV");
            System.err.println("Error is:" + e.getMessage());
        }

         System.out.println(" Prediction:" + v);
        try {
             String insertPrediction = "UPDATE svm.Prediction SET Prediction = '" + stripped_v + "'  where DocumentId = " + docID + " and Organ = '"+term_matrix_type+"'\n" +
                    "IF @@ROWCOUNT = 0\n" +
                    " BEGIN\n" +
                    "INSERT INTO svm.Prediction (DocumentId,Prediction,Organ) VALUES (?,?,?)" +
                    " END\n";

            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insertPrediction);
            ps.setInt(1, docID);
            ps.setString(2, stripped_v);
            ps.setString(3, term_matrix_type);
            ps.execute();
            ps.close();
        } catch (Exception e) {
            System.err.println("Got an exception in insertPPrediction");
            System.err.println("Error is:" + e.getMessage());
        }

        for (int i = 0; i < totalClasses; i++) {
             System.out.print("(" + labels[i] + ":" + prob_estimates[i] + ")");

            try {
                 String insertProbability = "UPDATE svm.PredictionProbability SET ProbabilityEstimate = " + prob_estimates[i] + "  where ProbabilityLabel = " + labels[i] + " and DocumentId = " + docID +" and Organ = '"+term_matrix_type+ "'\n" +

                        "IF @@ROWCOUNT = 0\n" +
                        " BEGIN\n" +
                         "INSERT INTO svm.PredictionProbability (ProbabilityLabel,ProbabilityEstimate,DocumentId,Organ) VALUES (?,?,?,?)" +
                         " END\n";

                PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insertProbability);
                ps.setString(1, "" + labels[i]);
                ps.setDouble(2, prob_estimates[i]);
                ps.setInt(3, docID);
                ps.setString(4, term_matrix_type);
                ps.execute();
                ps.close();


            } catch (Exception e) {
                System.err.println("Got an exception in insertProbability");
                System.err.println("Error is:" + e.getMessage());
            }
        }
    }

    private int[] parseIntArray(String[] arr)
    {
        return Stream.of(arr).mapToInt(Integer::parseInt).toArray();
    }

    private double[] parseDoubleArray(String[] arr)
    {
        return Stream.of(arr).mapToDouble(Double::parseDouble).toArray();
    }

    public static void main(String[] args)
    {
        NationalEvaluateDocument nationalEvaluateDocument = new NationalEvaluateDocument();
        int docID = 888452;
        String term_matrix_type = "liver";
        System.out.println("Testing:" + docID);
        nationalEvaluateDocument.run(term_matrix_type, docID);
    }

}



