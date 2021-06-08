package ml;

import utility.DBConnection;
import utility.SqlQueryResource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by VHACONMICHEG on 7/11/2017.
 */
public class NationalRunEvaluateDocument {
    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();
    InstanceDataFormat instanceDataFormat = new InstanceDataFormat();
    public static void main(String[] args)
    {
        new NationalRunEvaluateDocument();
    }

    NationalRunEvaluateDocument()
    {
        String sqlQuery= "select document_id from etex.document where runtype='testtruepositive'";
        PreparedStatement ps = null;
        try {
            ps = dbConnection.getDataBaseConnection().prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                NationalEvaluateDocument nationalEvaluateDocument = new NationalEvaluateDocument();
                int docID = rs.getInt("document_id");
                String term_matrix_type = "lung";

                System.out.println("Testing:" + docID);
                nationalEvaluateDocument.run(term_matrix_type, docID);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
