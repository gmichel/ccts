package utility;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TFIDFCalculator {
    /**
     * @param doc  list of strings
     * @param term String represents a term
     * @return term frequency of term in document
     */

    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();

    public double tf(List<String> doc, String term)
    {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }

    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public double idf(List<List<String>> docs, String term)
    {
        double n = 0.0;
        for (List<String> doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(docs.size() / n);
    }

    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public double tfIdf(List<String> doc, List<List<String>> docs, String term)
    {
        return tf(doc, term) * idf(docs, term);

    }

    //read db result set into list
    private List<String> readDBAsList(String query)
    {
        List<String> lines = new ArrayList<>(); //Collections.emptyList();
        String reportTextSelector = query;
        PreparedStatement ps = null;
        try {
            ps = dbConnection.getDataBaseConnection().prepareStatement(reportTextSelector);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String line = rs.getString("doc_text");
                lines.add(line);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private  HashMap<String, List<String>> readDBAsWordsList(String query)
    {
        HashMap<String, List<String>> mappedWords = new LinkedHashMap<>();
        String reportTextSelector = query;
        PreparedStatement ps = null;
        try {
            ps = dbConnection.getDataBaseConnection().prepareStatement(reportTextSelector);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String docID = rs.getString("document_id");
                String word = rs.getString("coveredText");
                this.put(mappedWords,docID,word);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mappedWords;
    }

    private void put(HashMap mappedWords,String key, String value) {
        List<String> current = (List<String>)mappedWords.get(key);
        if (current == null) {
            current = new ArrayList<String>();
            mappedWords.put(key, current);
        }
        current.add(value);
    }

    //read file into list
    private List<String> readFileInList(String fileName)
    {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    Map<String, Integer> wordCounts = new TreeMap<String, Integer>();
    private Map wordCounter(String input)
    {
        Scanner doc = new Scanner(input);
        while (doc.hasNext()) {
            String next = doc.next().toLowerCase();
            if (!wordCounts.containsKey(next)) {
                wordCounts.put(next, 1);
            } else {
                wordCounts.put(next, wordCounts.get(next) + 1);
            }
        }
        return wordCounts;
    }

    private void run(TFIDFCalculator calculator)
    {
        int numberofdocs = 1000;
     //   List<List<String>> documents = new ArrayList<>();  //Arrays.asList(doc1, doc2, doc3);
        //String docstoreturn = "select top " + numberofdocs + " doc_text from etex.document";
        String docstoreturn = "SELECT doc.document_id, coveredText FROM [PSCI_CCTS].[etex].[anno_token] token\n" +
                "  inner join etex.anno_base base on base.anno_base_id = token.anno_base_id\n" +
                "  inner join etex.document doc on doc.document_id=base.document_id\n" +
                "  where token.canonicalForm is not null\n" +
                "  and doc.document_id in (select top "+numberofdocs+" document_id from etex.document)";

        HashMap<String, List<String>> docConceptsMap = readDBAsWordsList(docstoreturn);
        List<List<String>> documents = new ArrayList<>();
        // using for-each loop for iteration over Map.entrySet()
        for (Map.Entry<String,List<String>> entry : docConceptsMap.entrySet()) {
             documents.add(entry.getValue());
        }
        String term = "chest";

      //  double tfidf = calculator.tfIdf(documents.get(0), documents, term);
        System.out.println("Number of docs="+documents.size());
        for(int i=0; i<documents.size();i++) {

            double termFreq = tf(documents.get(i), term);
       if(termFreq>0) {
           wordCounts = wordCounter(String.join(" ", documents.get(i)));
           for (String word : wordCounts.keySet()) {
               int count = wordCounts.get(word);
               if (count >= 2)
               {
                    System.out.println(word + "\t" + count);
               }

           }
           wordCounts = new TreeMap<String, Integer>();
           System.out.println(documents.get(i));
           System.out.println("TF(" + term + ") = " +termFreq);
           // System.out.println("TF-IDF(" + term + ") = " + tfidf);
           System.out.println("TF-IDF(" + term + ") = " + calculator.tfIdf(documents.get(i), documents, term));
       }

        }
       /*for (int i = 0; i < documents.size(); i++) {
           // documents.add(Arrays.asList(docs.get(i).split("\\s")));
            wordCounts = wordCounter(String.join(" ", documents.get(i)));
            for (String word : wordCounts.keySet()) {
                int count = wordCounts.get(word);
                if (count >= 1)
                {
                    // System.out.println(word + "\t" + count);
                }

            }
        }*/
 /*


        String term = "CT";
        double tfidf = calculator.tfIdf(documents.get(1), documents, term);
        System.out.println(documents.get(1));
        System.out.println("TF("+term+") = "+ tf(documents.get(1),  term));
        System.out.println("TF-IDF("+term+") = " + tfidf);
*/

    }

    public static void main(String[] args)
    {
        TFIDFCalculator calculator = new TFIDFCalculator();
        calculator.run(calculator);
    }

}
