package ml;

import utility.DBConnection;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import utility.SVMModel;
import utility.SqlQueryResource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Silmaril on 3/21/17.
 * Evaluates a set of new documents by analysis_batch - post ctakes processing
 */

public class NationalEvaluateTestBatch {

    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();
    InstanceDataFormat instanceDataFormat = new InstanceDataFormat();
    static List<String> docs = new ArrayList();


    NationalEvaluateTestBatch(int testCount, int positiveCount, int negativeCount, String organ, String instance_key,String runtype)
    {
        try {

            String sqlQ = sqlQueryResource.getSqlQuery("select-testset-document-organ-station-runtype");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(sqlQ);
            ps.setInt(1, testCount);
            ps.setString(2, organ);
            ps.setString(3, instance_key);
            ps.setInt(4, positiveCount);
            ps.setString(5, organ);
            ps.setString(6, instance_key);
            ps.setInt(7, negativeCount);
            ps.setString(8, organ);
            ps.setString(9, instance_key);
            ps.setString(10, runtype);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                docs.add(rs.getString("document_id"));
            }

            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void run(String organ, int docID)
    {
        instanceDataFormat = new InstanceDataFormat();

        String modelName = "libsvm" + organ + ".model";

        svm_model svm_model = new svm_model();

        try {

            if (new File(modelName).exists()) {
                svm_model = svm.svm_load_model(modelName);
            } else {

                String sqlQuery = "SELECT ModelValue FROM svm.NationalModel WHERE  ModelOrgan=(?)";
                PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(sqlQuery);
                ps.setString(1, organ);
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

        instanceDataFormat.captureData(docID, organ);

        evaluate_document_vector(docID, svm_model, organ);
    }

    public void evaluate_document_vector(int docID, svm_model svm_model, String term_matrix_type)
    {
        String termVec = instanceDataFormat.getResTermVec();

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

        // System.out.println(" Prediction:" + v);
        try {
            String insertPrediction = "UPDATE svm.Prediction SET Prediction = '" + stripped_v + "'  where DocumentId = " + docID + " and Organ = '" + term_matrix_type + "'\n" +
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
            //      System.out.print("(" + labels[i] + ":" + prob_estimates[i] + ")");

            try {
                String insertProbability = "UPDATE svm.PredictionProbability SET ProbabilityEstimate = " + prob_estimates[i] + "  where ProbabilityLabel = " + labels[i] + " and DocumentId = " + docID + " and Organ = '" + term_matrix_type + "'\n" +
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

//two runtypes positive and negative
    //counts are from training set
    //this test automatically selects docs not in training set
    public static void main(String[] args)
    {
      /*  String organ = "lung";
        String station = "689";
        int testCount=300;
        int positiveCount=900;
        int negativeCount=900;
        String runtype = "negative";*/

        String organ = "liver";
        String station = "689";
        int testCount=90;
        int positiveCount=210;
        int negativeCount=210;
        String runtype = "negative";

        NationalEvaluateTestBatch nationalEvaluateTestBatch = new NationalEvaluateTestBatch(testCount,positiveCount,negativeCount,organ, station,runtype);
        for (String doc : docs) {
            System.out.print("Testing:" + doc);
            nationalEvaluateTestBatch.run(organ, Integer.parseInt(doc));
            System.out.println();
        }
    }


   /* public void evaluate_document_vector(int docID, svm_model svm_model) {
        String termVec = instanceDataFormat.getResTermVec();

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
        String stripped_v = ""+(long)v;

        try {
            String insertDTV = "UPDATE svm.DocumentTermVector SET term_vector = '"+stripped_v+" "+ instanceDataFormat.getResTermVec()+"'  where document_id = "+docID+"\n" +
                    "IF @@ROWCOUNT = 0\n" +
                    " BEGIN\n" +
                    "  INSERT INTO svm.DocumentTermVector VALUES(?,?)\n"+
                    " END\n"
                    ;

            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insertDTV);
            ps.setString(1,stripped_v+" "+ instanceDataFormat.getResTermVec() );
            ps.setInt(2, docID);
            ps.execute();
            ps.close();

        } catch (Exception e) {
            System.err.println("Got an exception in method FormatOutput");
            System.err.println("Error is:" + e.getMessage());
        }
//if prob estimate for non current label being tested
        for (int i = 0; i < totalClasses; i++) {
            System.out.print("(" + labels[i] + ":" + prob_estimates[i] + ")");
        }

        System.out.println(" Prediction:" + v);
    }
*/

}
