package utility;

import org.apache.commons.collections4.IterableUtils;

import java.io.File;
import java.util.ArrayList;

public class RunHtmlParser {

    public static void main(String[] args)
    {
        //String file = "Diabetic.html";
        File directory = new File("C:\\Users\\gjm36\\Desktop\\guidelines_html\\");

        if(!directory.exists())
        {
System.exit(1);
        }

        File[] files = directory.listFiles();
        for(File f: files){
            System.out.println(f.getName());
            String outfile = f+".txt";
            HTMLtoText ht = new HTMLtoText(outfile);
            ht.parseHTMLtoText(f.getPath(),outfile);
        }
    }

}
