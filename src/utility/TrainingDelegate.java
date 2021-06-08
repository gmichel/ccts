package utility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by VHACONMICHEG on 6/19/2017.
 *
 */
public class TrainingDelegate {

    SqlQueryResource sqlQueryResource = new SqlQueryResource();
    DBConnection dbConnection = new DBConnection();

    public String retrieveClassNumber(String classValue,String classType)
    {
        String classNumber="";
        String className=classValue;
        PreparedStatement ps = null;
        try {
            String ab = sqlQueryResource.getSqlQuery("ref-class-name-index");
            ps = dbConnection.getDataBaseConnection().prepareStatement(ab);
            ps.setString(1, className);
            ps.setString(2, classType);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                classNumber=""+rs.getInt("ClassNameIndex");
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println("Got a DB exception in method retrieveClassNumber");
            System.err.println("Error is:" + e.getMessage());
        }

        return classNumber;
    }


}
