package ml;

import utility.DBConnection;
import utility.SqlQueryResource;
import utility.WriteExcel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by VHACONMICHEG on 6/8/2017. Calcu;ate the cosine similarity of any two doc vecs...
 */

public class SimilarityMeasure {

    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();
    WriteExcel writeToExcel = new WriteExcel();
    //int document_id = 763001;
  //  int document_idd = 763018;
    String tVec = "";
    String termVec = "";
   // String organ = "lung";
    List<String> concepts = null;
    List<String> termFreq = null;
    int inRow = 1;

    private void run()
    {
        ArrayList<Integer>  doclist = getDocsAsArrayList();
        HashMap<Integer,String> docMap = getDocsAsHashMap();

        for (int i = 0; i < doclist.size(); i++) {
            for(int j=i+1; j < doclist.size();j++)
            {
            //    System.out.println(doclist.get(i)+","+doclist.get(j));

            int document_id = doclist.get(i);
            int document_idd = doclist.get(j);

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

termFreq = Arrays.asList(termVec.substring(2).trim().split("\\s+"));
        HashMap<String, Double> map = new LinkedHashMap<>();

        for (String term : termFreq) {
            String[] indfreqPair = term.split(":");
            map.put(indfreqPair[0],Double.parseDouble(indfreqPair[1]));
        }

        try {
            String termVector = "SELECT term_vector FROM svm.DocumentTermVector WHERE document_id = (?)";
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(termVector);
            ps.setInt(1, document_idd);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tVec = rs.getString("term_vector");
              //   System.out.println(termVec.substring(2));
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println("Error is:" + e.getMessage());
        }

        concepts = Arrays.asList(tVec.substring(2).trim().split("\\s+"));
        HashMap<String, Double> mapp = new LinkedHashMap<>();

        for (String term : concepts) {
            String[] indfreqPair = term.split(":");
            mapp.put(indfreqPair[0],Double.parseDouble(indfreqPair[1]));

        }
       // System.out.println(mapp);
        double cos = getCosineSimilarity(map,mapp);
        String runtype1= getRunType(document_id);
        String runtype2= getRunType(document_idd);

    writeToExcel.run(document_id,runtype1,document_idd,runtype2,cos, inRow);


        inRow++;
       // System.out.println(document_id+" "+document_idd+" "+cos);
        //  String vecs = res.replaceAll("\\s(\\d\\s+)",System.lineSeparator()+"$1");
        // String out = vecs.replaceAll("(\\d+:)(\\d+)\\s","$2,");
        //   System.out.println(vecs);
            }
        }
    }

    private String getRunType(int docID)
    {
        String runtype = "";
        try {
            String docs = "SELECT runtype  FROM etex.document where document_id = (?)";
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(docs);
            ps.setInt(1,docID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                runtype = rs.getString("runtype");

            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println("Error is:" + e.getMessage());

        }
return runtype;

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

private ArrayList<Integer> getDocsAsArrayList()
{
    ArrayList<Integer> doclist = new ArrayList<>();;
    try {
        String docs = "SELECT  document_id FROM etex.document TABLESAMPLE(2 PERCENT)\n" +
                "WHERE document_id IN (SELECT document_id FROM svm.DocumentTermVector) and runtype != 'production'";
        PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(docs);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          int doc_id = rs.getInt("document_id");

            doclist.add(doc_id);
        }
        ps.close();
        rs.close();
    } catch (Exception e) {
        System.err.println("Got an exception!");
        System.err.println("Error is:" + e.getMessage());
    }
    return doclist;
}

private HashMap<Integer,String> getDocsAsHashMap()
{
    HashMap<Integer,String> doctypeMap = new HashMap<>();

    try {
        String docs = "SELECT  document_id, runtype FROM etex.document where runtype='positive'";
        PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(docs);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int doc_id = rs.getInt("document_id");
            String runtype = rs.getString("runtype");
            doctypeMap.put(doc_id,runtype);
        }
        ps.close();
        rs.close();
    } catch (Exception e) {
        System.err.println("Got an exception!");
        System.err.println("Error is:" + e.getMessage());
    }
    return doctypeMap;
}




    public static void main(String[] args)
    {
        SimilarityMeasure worker = new SimilarityMeasure();
        worker.run();
    }
}
