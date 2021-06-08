package utility;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class WriteExcel {

    public void run(Integer doc1,String runtype1, Integer doc2,String runtype2, double cosine, int inRow)
    {
        try {
            String filename = "CosineExcelFile.xls" ;


            //new FileInputStream(filename)
            File outputDataFile = new File(filename);
            if(outputDataFile.createNewFile())
            {
                 FileOutputStream output_file =new FileOutputStream(filename);//Open FileOutputStream to write updates
                  HSSFWorkbook wb =new HSSFWorkbook();
                  wb.write(output_file);
                  output_file.close();
            }
        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filename));
            HSSFSheet sheet ;
            if(workbook.getNumberOfSheets()<1) {
            sheet = workbook.createSheet("DocumentsSheet");
        }
        else
        {
            sheet = workbook.getSheet("DocumentsSheet");
        }
/*
        HSSFRow rowhead = sheet.createRow((short)0);
        rowhead.createCell(0).setCellValue("Document ID 1");
        rowhead.createCell(1).setCellValue("Document ID 2");
        rowhead.createCell(2).setCellValue("Cosine");
*/

        HSSFRow row = sheet.createRow((short)inRow);
        row.createCell(0).setCellValue(doc1);
            row.createCell(1).setCellValue(runtype1);
        row.createCell(2).setCellValue(doc2);
            row.createCell(3).setCellValue(runtype2);
        row.createCell(4).setCellValue(cosine);



        FileOutputStream fileOut = new FileOutputStream(filename,false);
        workbook.write(fileOut);
        fileOut.close();

    } catch ( Exception ex ) {
        System.out.println(ex);
    }
    }
}