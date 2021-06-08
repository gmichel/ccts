package utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Created by VHACONMICHEG on 6/16/2017.
 */

public class SqlQueryResource {

    public String getSqlQuery(String sqlquery)
    {
        Properties props = new Properties();
        InputStream fis = null;
        String sqlqueriesfile = "sql-queries.xml";
        try {
            fis = SqlQueryResource.class.getClassLoader().getResourceAsStream(sqlqueriesfile);
            props.loadFromXML(fis);
        } catch (FileNotFoundException fe) {
            System.err.println("sql-queries.xml not found in classpath");
            System.err.println("Error is:" + fe.getMessage());
        } catch (InvalidPropertiesFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props.getProperty(sqlquery).trim();
    }
//example
    public static void main(String[] args)
    {
        SqlQueryResource sqlQueryResource = new SqlQueryResource();
        String output = sqlQueryResource.getSqlQuery("select-term-matrix");
        System.out.println("Query: "+ output);
    }

}
