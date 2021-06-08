package ml;

import features.FeatureSelector;
import gnu.trove.map.TMap;
import libsvm.svm_problem;
import org.apache.commons.io.FileUtils;

import utility.DBConnection;
import utility.SqlQueryResource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;


/**
 * Created by VHACONMICHEG on 6/8/2017.
 */
public class ConceptFeatureReducer {

    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();
  //  int document_id = 671407;
    String trainingVec = "";
  //  String termVec = "";
    String organ = "lung";
    List<String> concepts = null;
  //  List<String> termFreq = null;






    private File getTermMatrixAsFile(String term_matrix_type)
    {
        NationalTrainingSetBuilder nationalTrainingSetBuilder = new NationalTrainingSetBuilder();
        String termMatrixType = term_matrix_type;
        String tSet = nationalTrainingSetBuilder.getTrainingSet(termMatrixType);
        String tSetMod = tSet.replaceAll("\\s{2,}", System.getProperty("line.separator"));
        svm_problem svmProblem = nationalTrainingSetBuilder.getProblem(tSet);
        String outputFileName =
                new SimpleDateFormat("yyyyMMddHHmm'.TermMatrix_"+termMatrixType+".txt'").format(new Date());
        File retFile = new File(outputFileName);
        try {
            FileUtils.writeStringToFile(retFile, tSetMod, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retFile;
    }



    private void run()
    {
        try {
            String trainingVector = sqlQueryResource.getSqlQuery("training-vector");//"SELECT term_matrix FROM svm.TermMatrix where term_matrix_type = (?)";
            Connection conn = dbConnection.getDataBaseConnection();
            PreparedStatement ps = conn.prepareStatement(trainingVector);
            ps.setString(1, organ);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                trainingVec = rs.getString("TermVector");
                //  System.out.println(trainingVec);
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println("Error is:" + e.getMessage());
        }
        String tvec = trainingVec.substring(1, trainingVec.length() - 1);
        concepts = Arrays.asList(tvec.split(",\\s+"));

        FeatureSelector app = new FeatureSelector();
        app.startThreads(10, getTermMatrixAsFile("lung").getPath());

        app.waitForAllThreads();


        TMap<String, Double> all_features_scores = app.all_features_scores;




    //    System.out.println(all_features_scores);
        System.out.println(concepts);
     //   app.printOutput(all_features_scores);


        Iterator<String> features_names = all_features_scores.keySet().iterator();
       // int i = 0;
        while (features_names.hasNext() ) {
            String featureindex = features_names.next();
          //if(all_features_scores.get(featureindex)>.00){
               // System.out.println(featureindex + "\t" + all_features_scores.get(featureindex));
                System.out.println(featureindex + "\t" + concepts.get(Integer.parseInt(featureindex)-1) + "\t" + all_features_scores.get(featureindex));
        //  }
           // i++;
        }





        /* try {
            String termVector = "SELECT term_vector FROM svm.DocumentTermVector WHERE document_id = (?)";
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(termVector);
            ps.setInt(1, document_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                termVec = rs.getString("term_vector");
                // System.out.println(termVec.substring(2));
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println("Error is:" + e.getMessage());
        }*/

     //   termFreq = Arrays.asList(termVec.substring(2).split("\\s+"));
//Map<String,String> map = new LinkedHashMap<>();
      /*  for (String term : termFreq) {
            String[] indfreqPair = term.split(":");
            //       map.put(concepts.get(Integer.parseInt(indfreqPair[0])-1),indfreqPair[1]);
            InsertIntoTable(concepts.get(Integer.parseInt(indfreqPair[0]) - 1), indfreqPair[1]);

        }*/


        //     System.out.println(map);
        //  String vecs = res.replaceAll("\\s(\\d\\s+)",System.lineSeparator()+"$1");
        // String out = vecs.replaceAll("(\\d+:)(\\d+)\\s","$2,");
        //   System.out.println(vecs);

    }



/*

    private void InsertIntoTable(String b1, String b2)
    {
        try {
            String insertDocumentConceptFrequency = "INSERT INTO svm.DocumentConceptFrequency (DocumentId,Concept,Frequency) VALUES (?,?,?)";
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insertDocumentConceptFrequency);
            ps.setInt(1, document_id);
            ps.setString(2, b1);
            ps.setString(3, b2);
            ps.execute();
            ps.close();
        } catch (Exception e) {
            System.err.println("Got an exception in insertDocumentConceptFrequency");
            System.err.println("Error is:" + e.getMessage());
        }


    }
*/

    public static void main(String[] args)
    {
        ConceptFeatureReducer worker = new ConceptFeatureReducer();
        worker.run();
    }
}
