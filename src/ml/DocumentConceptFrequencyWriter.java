package ml;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XmlCasDeserializer;
import org.xml.sax.SAXException;
import utility.DBConnection;
import utility.SqlQueryResource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

import org.apache.uima.cas.CAS;



/**
 * Created by VHACONMICHEG on 6/8/2017.
 */
public class DocumentConceptFrequencyWriter {

    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();
    int document_id = 763001;
    String trainingVec = "";
    String termVec = "";
    String organ = "lung";
    List<String> concepts = null;
    List<String> termFreq = null;



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

        try {
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
        }

        termFreq = Arrays.asList(termVec.substring(2).split("\\s+"));
//Map<String,String> map = new LinkedHashMap<>();
        for (String term : termFreq) {
            String[] indfreqPair = term.split(":");
            //       map.put(concepts.get(Integer.parseInt(indfreqPair[0])-1),indfreqPair[1]);
            InsertIntoTable(concepts.get(Integer.parseInt(indfreqPair[0]) - 1), indfreqPair[1]);

        }


        //     System.out.println(map);
        //  String vecs = res.replaceAll("\\s(\\d\\s+)",System.lineSeparator()+"$1");
        // String out = vecs.replaceAll("(\\d+:)(\\d+)\\s","$2,");
        //   System.out.println(vecs);

    }


    public double getCosineSimilarity(HashMap<String,Double> map1, HashMap<String,Double> map2)
    {
        double dot = 0, scala1 = 0, scala2 = 0, val;
        String key;

        for (String cur : map1.keySet())
        {

            val = map1.get(cur);

            if (map2.containsKey(cur))
                dot += (val * map2.get(cur));

            scala1 += (val * val);
        }

        for (String cur : map2.keySet())
        {
            val = map2.get(cur);
            scala2 += (val * val);
        }

        scala1 = Math.sqrt(scala1);
        scala2 = Math.sqrt(scala2);

        return dot / (scala1 * scala2);
    }



    private CAS GetCas(String documentID)
    {
        GZIPInputStream gzIS = null;
        File descriptorFile = new File("AggregateYtexProcessor.xml");
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        CAS casDescriptor = null;
        CAS cas = null;
        try {
            Object descriptor = UIMAFramework.getXMLParser().parse(
                    new XMLInputSource(descriptorFile));
            casDescriptor = CasCreationUtils.createCas((AnalysisEngineDescription) descriptor);
            cas = CasCreationUtils.createCas(Collections.EMPTY_LIST,
                    casDescriptor.getTypeSystem(),
                    UIMAFramework.getDefaultPerformanceTuningProperties());
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        } catch (InvalidXMLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String strSQL = "select cas from etex.document where document_id = (?)";
            preparedStatement = dbConnection.getDataBaseConnection().prepareStatement(strSQL);
            preparedStatement.setInt(1, document_id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                gzIS = new GZIPInputStream(new BufferedInputStream(
                        rs.getBinaryStream(1)));
                XmlCasDeserializer.deserialize(gzIS, cas, true);
                       }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
            }
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
            }
            try {
                if (gzIS != null)
                    gzIS.close();
            } catch (IOException e) {
            }
        }
        return cas;
    }




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



    public static void main(String[] args)
    {
        DocumentConceptFrequencyWriter worker = new DocumentConceptFrequencyWriter();
        worker.run();
    }
}
