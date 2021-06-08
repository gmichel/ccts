package ml;

import dao.ConceptDao;
import utility.DBConnection;
import utility.SqlQueryResource;
import utility.TrainingDelegate;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by VHACONMICHEG on 6/15/2017.
 * This program captures the training data from etex.document. The runtype is used to set the label mapping from the refDocumentClass table.
 * The main query used is the stored procedure SP_national_training_data_concepts - sets the number of reports for each report type (+,-), organ, and site
 *
 */
public class NationalTrainingSetDataCapture {

    private List<String> allUniqueDocIds = new ArrayList<>();
    List<String> allUniqueConcepts = new ArrayList<>();
    TrainingDelegate trainingDelegate = new TrainingDelegate();
    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();
    boolean umlsCodesOnly;
    boolean conceptsOnly;
    String classType = "";

    public LinkedHashMap<Integer, ConceptDao> captureData(int numberPositiveRows, int numberNegativeRows, String trainingType, String station, boolean umlsCodesOnly, boolean conceptsOnly)
    {
        this.umlsCodesOnly = umlsCodesOnly;
        this.conceptsOnly = conceptsOnly;
      //  this.classType = classType;
        LinkedHashMap<Integer, ConceptDao> docConceptMap = new LinkedHashMap<>();
        try {
            //all data
            String training = sqlQueryResource.getSqlQuery("national-training-data-concepts");
            PreparedStatement pstmt2 = dbConnection.getDataBaseConnection().
                    prepareStatement(training);
            pstmt2.setInt(1, numberPositiveRows);
            pstmt2.setInt(2, numberNegativeRows);
            pstmt2.setString(3, trainingType);
            pstmt2.setString(4, station);
            ResultSet rs = pstmt2.executeQuery();

            int ind = 0;
            while (rs.next()) {
                //Retrieve by column name
                ConceptDao cd = new ConceptDao();
                String document_id = rs.getString("document_id");
                String concept = rs.getString("concept");
                String runtype = rs.getString("runtype");
                //capture results
                cd.setDocumentID(document_id);
                cd.setConcept(concept);
                cd.setRuntype(runtype);
                if (umlsCodesOnly) {
                    if (concept.matches("C\\d{4,}")) {
                        docConceptMap.put(ind++, cd);
                    }
                    continue;
                }
                if (conceptsOnly) {
                    if (!concept.matches("C\\d{4,}")) {
                        docConceptMap.put(ind++, cd);
                    }
                    continue;
                }
                docConceptMap.put(ind++, cd);
            }
            pstmt2.close();
            rs.close();
           // FormatOutput((LinkedHashMap<Integer, ConceptDao>) docConceptMap, trainingType);
        } catch (SQLException  e) {
            System.err.println("Got an exception!");
            System.err.println("Error is:" + e.getMessage());
        }
        return docConceptMap;
    }

    private String filteredItem(String key)
    {
        String item = "";
        //pass thru
        item = key;

        //remove specific terms
        // if (!key.matches("than|but|when")) {
        //   item = key;
        // }
        if (umlsCodesOnly) {  //umls codes only
            if (key.matches("C\\d{4,}")) {
                item = key;

            }
            return item;
        }
        if (conceptsOnly) {  //umls codes only
            if (!key.matches("C\\d{4,}")) {
                item = key;

            }
            return item;
        }
//pass thru
        return item;
    }

    private void FormatOutput(LinkedHashMap<Integer, ConceptDao> docconceptmap, String term_matrix_type) throws IOException
    {
        Map<String, Integer> conceptValMap = new LinkedHashMap<>();
        Map<String, Integer> docidValMap = new LinkedHashMap<>();

        //unique only
        for (Map.Entry<Integer, ConceptDao> entry : docconceptmap.entrySet()) {
            conceptValMap.put(entry.getValue().getConcept(), entry.getKey());
        }
        //unique concepts as list
        for (Map.Entry<String, Integer> entry : conceptValMap.entrySet()) {
            String item = filteredItem(entry.getKey());
            //remove single letter results
            if (item.length() > 1) {
            //    allUniqueConcepts.add(entry.getKey());
                allUniqueConcepts.add(item);
            }
        }
        writeToTrainingVector((ArrayList) allUniqueConcepts, term_matrix_type);
        //unique only
        for (Map.Entry<Integer, ConceptDao> integerConceptDaoEntry : docconceptmap.entrySet()) {
            docidValMap.put(integerConceptDaoEntry.getValue().getDocumentID(), integerConceptDaoEntry.getKey());
        }
        //as list
        docidValMap.entrySet().forEach((entry) -> allUniqueDocIds.add(entry.getKey()));

        ArrayList<String> termMatrixList =
                getListOfConcepts((ArrayList<String>) allUniqueDocIds, docconceptmap, term_matrix_type);
        writeToTermMatrix(String.join(" ", termMatrixList), term_matrix_type);
    }

    public void writeToTrainingVector( ArrayList allUniqueConcepts, String trainingType)
    {
        try {
            String insert = sqlQueryResource.getSqlQuery("national-update-insert-training-vector");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insert);

            ps.setString(1, allUniqueConcepts.toString());
            ps.setString(2, trainingType);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void writeToTermMatrix( String term_matrix, String trainingType)
    {
        try {
            String insert = sqlQueryResource.getSqlQuery("national-update-insert-term-matrix");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insert);

            ps.setString(1, trainingType);
            ps.setString(2, term_matrix);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // output libsvm formatted list


    public ArrayList<String> getListOfConcepts(ArrayList<String> allUniqueDocIds, HashMap<Integer, ConceptDao> docconceptmap, String trainingType)
    {
        ArrayList<String> conceptList;
        ArrayList<String> returnList = new ArrayList<>();
        String runtypeValue = "";
        String classNum = "";
        String documentTerms = "";

        for (String docId : allUniqueDocIds) {

            conceptList = new ArrayList<>();
            for (Map.Entry<Integer, ConceptDao> entry : docconceptmap.entrySet()) {
                String en = entry.getValue().getDocumentID();
                if (en.equals(docId)) {
                    //two labels
                    //analysisBatchNum = (runtypeValue.equals("truepositive")) ? "1" : "-1";
                    //multiclass
                    runtypeValue = entry.getValue().getRuntype();
                    conceptList.add(entry.getValue().getConcept());
                }
            }

            classNum = trainingDelegate.retrieveClassNumber(runtypeValue, classType);
            //not a regular classnum
            if (classNum == "") {
                continue;
            }

            documentTerms = classNum + " ";
            for (String concept : allUniqueConcepts) {
                int indy = allUniqueConcepts.indexOf(concept) + 1;
                int freq = Collections.frequency(conceptList, concept);
                //drop zero frequency features
                if (freq != 0) {
                    documentTerms = documentTerms + indy + ":" + freq + " ";
                }
            }
            try {
                //
                String insertdocumenttermvector = sqlQueryResource.getSqlQuery("update-insert-documenttermvector");
                PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insertdocumenttermvector);
                ps.setString(1, documentTerms);
                ps.setInt(2, Integer.parseInt(docId));
                ps.execute();
                ps.close();

                String insertdocumentconceptlist = sqlQueryResource.getSqlQuery("update-insert-documentconceptlist");
                PreparedStatement ps2 = dbConnection.getDataBaseConnection().prepareStatement(insertdocumentconceptlist);
                ps2.setString(1, conceptList.toString());
                ps2.setInt(2, Integer.parseInt(docId));
                ps2.execute();
                ps2.close();

            } catch (SQLException e) {
                System.err.println("Got a DB exception in method FormatOutput");
                System.err.println("Error is:" + e.getMessage());
            }
            //   System.out.println(allUniqueConcepts.toString());
            //    System.out.println(conceptList);
            //    System.out.println(documentTerms);
            returnList.add(documentTerms);
        }

        return returnList;
    }

    public static void main(String[] args)
    {
        NationalTrainingSetDataCapture dc = new NationalTrainingSetDataCapture();
        boolean umlsCodesOnly = false;
        boolean conceptsOnly = false;
        dc.classType = "Radiology"; //this will get POSITIVE and NEGATIVE
     /*   String organ = "lung";
        String station="689";
        int numberPositiveRows=900;
        int numberNegativeRows=900;*/
        String organ = "liver";
        String station="689";
        int numberPositiveRows=210;
        int numberNegativeRows=210;
        LinkedHashMap<Integer, ConceptDao> captureDataMap;
        long startTime = System.currentTimeMillis();
        captureDataMap = dc.captureData(numberPositiveRows,numberNegativeRows,organ, station, umlsCodesOnly, conceptsOnly);
        try {
            dc.FormatOutput(captureDataMap, organ);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
        System.out.println("Seconds="+TimeUnit.MILLISECONDS.toSeconds(elapsedTime));
        System.out.println("Minutes="+TimeUnit.MILLISECONDS.toMinutes(elapsedTime));

    }
}
