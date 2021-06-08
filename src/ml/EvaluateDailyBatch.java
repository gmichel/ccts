package ml;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import utility.DBConnection;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by George Michel on 3/21/17.
 * Evaluates a set of documents by organ and station - post ctakes processing
 * One Ring mode - Each organ has a Single model for all stations
 * Name of output file is libsvm+organ+.model
 * or from database as well
 * */

public class EvaluateDailyBatch {

    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();
    InstanceDataFormat instanceDataFormat = new InstanceDataFormat();
    static List<String> docs = new ArrayList();
    Multimap<Integer,String> stationOrganMap = ArrayListMultimap.create();;

    private Multimap<Integer,String> GetStationOrganAsMap()
    {
        try {
            String sqlDrop = sqlQueryResource.getSqlQuery("select_station_organ");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(sqlDrop);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int column1 = rs.getInt("station");
                String column2 = rs.getString("organ");
                stationOrganMap.put(column1, column2);
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stationOrganMap;
    }

    EvaluateDailyBatch()
    {
    }

    EvaluateDailyBatch(String organ, String instance_key, String runtype)
    {
        try {

            String sqlQ = sqlQueryResource.getSqlQuery("select-documents-from-organ-station-runtype");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(sqlQ);

            ps.setString(1, organ);
            ps.setString(2, instance_key);
            ps.setString(3, runtype);
            ResultSet rs = ps.executeQuery();
            docs = new ArrayList();
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

                String sqlQuery = sqlQueryResource.getSqlQuery("select_modelvalue");
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

    //    move_data_from_view_to_table();
    }

    private void move_data_from_view_to_table()
    {
        //drop table
        try {
            String sqlDrop = sqlQueryResource.getSqlQuery("drop_table");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(sqlDrop);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       //rebuild table from view
        try {
            String sqlMove = sqlQueryResource.getSqlQuery("move_view");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(sqlMove);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void evaluate_document_vector(int docID, svm_model svm_model, String organ)
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
        String prediction = "" + (long) v;

        try {
            String insertDTV = sqlQueryResource.getSqlQuery("update-insert-documenttermvector");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insertDTV);
            ps.setString(1, prediction + " " + instanceDataFormat.getResTermVec());
            ps.setInt(2, docID);
            ps.setString(3, prediction + " " + instanceDataFormat.getResTermVec());
            ps.setInt(4, docID);
            ps.execute();
            ps.close();

        } catch (Exception e) {
            System.err.println("Got an exception in insertDTV");
            System.err.println("Error is:" + e.getMessage());
        }

        try {
            String upateorinsertPred = sqlQueryResource.getSqlQuery("update-insert-prediction");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(upateorinsertPred);
            ps.setString(1, prediction);
            ps.setInt(2, docID);
            ps.setString(3, organ);
            ps.setInt(4, docID);
            ps.setString(5, prediction);
            ps.setString(6, organ);
            ps.execute();
            ps.close();
        } catch (Exception e) {
            System.err.println("Got an exception in insertPPrediction");
            System.err.println("Error is:" + e.getMessage());
        }

        for (int i = 0; i < totalClasses; i++) {
            try {
                String updateorinsertPredProb = sqlQueryResource.getSqlQuery("update-insert-predictionprobability");
                PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(updateorinsertPredProb);
                ps.setDouble(1, prob_estimates[i]);
                ps.setString(2, "" + labels[i]);
                ps.setInt(3, docID);
                ps.setString(4, organ);
                ps.setString(5, "" + labels[i]);
                ps.setDouble(6, prob_estimates[i]);
                ps.setInt(7, docID);
                ps.setString(8, organ);

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

    //Production Daily Run
    public static void main(String[] args)
    {
//multiple stations but one model

        String runtype = "production";
        EvaluateDailyBatch nat = new EvaluateDailyBatch();
        Multimap<Integer, String> storgMap = nat.GetStationOrganAsMap();
        EvaluateDailyBatch nationalEvaluateDailyBatch;
        for (Map.Entry<Integer, String> entry : storgMap.entries()) {
           // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            String organ =  entry.getValue();
            Integer station= entry.getKey();
            nationalEvaluateDailyBatch = new EvaluateDailyBatch(organ, station.toString(),runtype);

                for (String doc : docs) {
                    System.out.print("Daily Rpt - Station:" + station + " Organ:" + organ + " DocID:" + doc);
                    nationalEvaluateDailyBatch.run(organ, Integer.parseInt(doc));
                    System.out.println();
                }


        }
/*
        for (int i = 0; i < station.length ; i++) {
        EvaluateDailyBatch nationalEvaluateDailyBatch = new EvaluateDailyBatch(organ, station[i],runtype);
        for (String doc : docs) {
            System.out.print("Daily Report:" + doc);
            nationalEvaluateDailyBatch.run(organ, Integer.parseInt(doc));
            System.out.println();
        }
        */
        }

    }




