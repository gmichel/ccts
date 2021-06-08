package utility;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HTMLtoText extends HTMLEditorKit.ParserCallback {
    boolean styleElement = false;
    boolean endPara = false;
    String resultFile = "";

    HTMLtoText(String reusltFile)
    {
        resultFile = reusltFile;
    }

    public void parseHTMLtoText(String file, String outputfile)
    {
        HTMLtoText parser = new HTMLtoText(outputfile);
        this.resultFile = outputfile;
        Reader reader = null;
        try {
            reader = new FileReader(file);
            new ParserDelegator().parse(reader, parser, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(e);
        }

    }


    public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos)
    {
        if (tag.toString() == "style") {
            styleElement = true;
        } else {
            styleElement = false;
        }
    }

    public void handleEndTag(HTML.Tag tag, int pos)
    {
        if (tag.toString() == "p") {
            endPara = true;
        } else {
            endPara = false;
        }
    }

    public void handleText(char[] data, int pos)
    {
     //   Date date = new Date();
//        String resultFile =  new SimpleDateFormat("yyyyMMddHHmm'.ParsedHtml" + ".txt'").format(new Date());
String resultFile = this.resultFile;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile, true))) {

            if (styleElement == false) {
                writer.append(new String(data));

            }
            if (endPara) {
                writer.append(System.lineSeparator());
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


/*
 public void handleComment(char[] data, int pos)
    {
        displayData(new String(data));
    }

    public void handleEndOfLineString(String eol)
    {
        System.out.println( line++ );
    }

    public void handleEndTag(HTML.Tag tag, int pos)
    {
        tabLevel--;
        displayData("/" + tag);
    }

    public void handleError(String errorMsg, int pos)
    {
        displayData(pos + ":" + errorMsg);
    }

    public void handleMutableTag(HTML.Tag tag, MutableAttributeSet a, int pos)
    {
        displayData("mutable:" + tag + ": " + pos + ": " + a);
    }

    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet a, int pos)
    {
        displayData( tag + "::" + a );
//      tabLevel++;
    }

    public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos)
    {
        displayData( tag + ":" + a );
        tabLevel++;
    }
*/
