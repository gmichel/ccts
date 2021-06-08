package utility;

import java.io.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

public class TextRetriever extends HTMLEditorKit.ParserCallback
{
    private int tabLevel = 1;
    private int line = 1;
    boolean styleElement = false;



    public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos)
    {
          if(tag.toString()=="style")
        {
            styleElement=true;
        }
        else
        {
            styleElement=false;
        }
    }

    public void handleText(char[] data, int pos)
    {
        if(styleElement==false)

        {
            displayData( new String(data) );
        }

    }

    private void displayData(String text)
    {
        System.out.println(text);
    }

    public static void main(String[] args)
            throws IOException
    {

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
