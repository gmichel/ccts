package ml;

import utility.DBConnection;
import utility.SqlQueryResource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by VHACONMICHEG on 6/8/2017.
 */
public class Worker {

    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource=new SqlQueryResource();
    String res = "";

    private void run()
    {
        try {
            String select_term_matrix = sqlQueryResource.getSqlQuery("select-document");//"SELECT term_matrix FROM svm.TermMatrix where term_matrix_type = (?)";
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(select_term_matrix);
            ps.setString(1, "uncertain");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
               res =   rs.getString("document_id");
                System.out.println(res);
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println("Error is:" + e.getMessage());
        }

      //  String vecs = res.replaceAll("\\s(\\d\\s+)",System.lineSeparator()+"$1");
       // String out = vecs.replaceAll("(\\d+:)(\\d+)\\s","$2,");
     //   System.out.println(vecs);

    }

    public static void main(String[] args)
    {
        Worker worker = new Worker();
        worker.run();
    }
}
