package ml;

import dao.ConceptDao;
import utility.DBConnection;
import utility.SqlQueryResource;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/*This is for the instance doc to put it into libsvm format
Output is DocumentConceptList
Input documentID, radiology type (lung, liver...)
Calls Stored Procedure SP_single_concept_doc
select t1.document_id , t1.concept, t2.analysis_batch from
(
SELECT ab.document_id ,ab.anno_base_id , annoT.canonicalForm concept
  FROM etex.anno_token annoT, etex.anno_base ab
  where ab.anno_base_id = annoT.anno_base_id
  and annoT.canonicalForm is not null
 union
 SELECT ab.document_id ,ab.anno_base_id ,  aoc.code
  FROM etex.anno_ontology_concept aoc, etex.anno_base ab
  where ab.anno_base_id = aoc.anno_base_id
 ) t1, (select  ed.analysis_batch ,ed.document_id,ed.instance_id RowID
 from etex.document ed) t2
 where t1.document_id=@DOC_ID
 and  t2.document_id = t1.document_id
 order by t1.document_id
 */
class InstanceDataFormat {

    List<String> allUniqueConcepts = new ArrayList<String>();
    List<String> instanceUniqueConcepts = new ArrayList<String>();
    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();
    NationalTrainingSetBuilder nationalTrainingSetBuilder = new NationalTrainingSetBuilder();
    private String resTermVec;
    private String termVec;

    InstanceDataFormat()
    {
        termVec = "";
        resTermVec = "";
    }

    public void captureData(int docID, String term_matrix_type) {
        String docConceptMapAsString="";

        try {
            String singleconceptquery = sqlQueryResource.getSqlQuery("single-concept-doc");
            PreparedStatement pstmt = dbConnection.getDataBaseConnection().prepareStatement(singleconceptquery);
            pstmt.setInt(1, docID);
            ResultSet rs = pstmt.executeQuery();

            Map<Integer, ConceptDao> docConceptMap = new HashMap<Integer, ConceptDao>();
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
                //umls codes only!
                //  if (concept.matches("C\\d{4,}")) {
                //       docConceptMap.put(ind++, cd);
                //    }
                instanceUniqueConcepts.add(concept);
                docConceptMap.put(ind++, cd);
            }
            pstmt.close();
            rs.close();


            FormatOutput((HashMap) docConceptMap, docID, term_matrix_type);

            docConceptMapAsString = String.join(",", instanceUniqueConcepts);
            //performs update if already inserted
           /* String insertDCL = "UPDATE svm.DocumentConceptList SET DocumentConceptList = '"+docConceptMapAsString
                    +"'  where documentid = "+docID+"\n" +
                    "IF @@ROWCOUNT = 0\n" +
                    " BEGIN\n" +
                    "  INSERT INTO svm.DocumentConceptList VALUES(?,?)\n"+
                    " END\n"
                    ;
*/
            String insertDCL = sqlQueryResource.getSqlQuery("update-insert-documentconceptlist");
                    //String insert = "INSERT INTO svm.DocumentConceptList VALUES(?,?)";
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insertDCL);
            ps.setString(1, docConceptMapAsString);
            ps.setInt(2, docID);
            ps.execute();
            ps.close();

        } catch (SQLException sqle) {
            System.err.println("Got a SQL exception:");
            System.err.println("Error is:" + sqle.getMessage());
        } catch (IOException ioe) {
            System.err.println("Got an IO exception:");
            System.err.println("Error is:" + ioe.getMessage());
        }
    }

    private void FormatOutput(HashMap<Integer, ConceptDao> docconceptmap, int docID, String term_matrix_type) throws IOException {
        Map<String, Integer> conceptValMap = new HashMap<String, Integer>();
        //unique only
        for (Map.Entry<Integer, ConceptDao> entry : docconceptmap.entrySet()) {
            conceptValMap.put(entry.getValue().getConcept(), entry.getKey());
        }

        try {
            String select_term_vector = sqlQueryResource.getSqlQuery("training-vector");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(select_term_vector);
            ps.setString(1, term_matrix_type);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                setTermVec(rs.getString("TermVector"));
            }
            ps.close();
            rs.close();
        } catch (SQLException sqle) {
            System.err.println("Got a database exception!");
            System.err.println("Error is:" + sqle.getMessage());
        }
        //remove first and last
        setTermVec(getTermVec().substring(1, getTermVec().length()-1));
        String[] terms = getTermVec().split(",");
        for(int i=0;i<terms.length;i++)
        {
            allUniqueConcepts.add(terms[i].trim());
        }
        //unique concepts as list
       // for(Map.Entry<String, Integer> entry : conceptValMap.entrySet())
      //  {
       //     allUniqueConcepts.add(entry.getKey());
      //  }
        //debug
       // String TrainingSet = nationalTrainingSetBuilder.getTrainingSet(term_matrix_type);
       // String[] splitTrainingSet = TrainingSet.split("\\s{2,}");
        //allUniqueConcepts = Arrays.asList(splitTrainingSet);

        for (String concept : allUniqueConcepts) {
            int indy = allUniqueConcepts.indexOf(concept) + 1;
            //drop zeroes
            int freq = Collections.frequency(instanceUniqueConcepts, concept);
            if (freq != 0) {
                resTermVec = resTermVec + indy + ":" + freq + " ";
            }
        }
    }

    //for testing
    public static void main(String[] args) {
        //testing
        InstanceDataFormat dc = new InstanceDataFormat();
        int docID = 671363;
        String term_matrix_type = "lung";
        dc.captureData(docID, term_matrix_type);
    }

    public String getResTermVec()
    {
        return resTermVec;
    }

    public void setResTermVec(String resTermVec)
    {
        this.resTermVec = resTermVec;
    }

    public String getTermVec()
    {
        return termVec;
    }

    public void setTermVec(String termVec)
    {
        this.termVec = termVec;
    }
}
