package org.jyg.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

/**
 * create by jiayaoguang on 2021/6/12
 */
public class ExcelProcessor {

    private final File excelFile;

    private String javaOutPath;
    private String csharpOutPath;
    private String jsonOutPath;

    public ExcelProcessor(File excelFile) {
        this.excelFile = excelFile;
    }


    public String getJavaOutPath() {
        return javaOutPath;
    }

    public void setJavaOutPath(String javaOutPath) {
        this.javaOutPath = javaOutPath;
    }

    public String getCsharpOutPath() {
        return csharpOutPath;
    }

    public void setCsharpOutPath(String csharpOutPath) {
        this.csharpOutPath = csharpOutPath;
    }

    public String getJsonOutPath() {
        return jsonOutPath;
    }

    public void setJsonOutPath(String jsonOutPath) {
        this.jsonOutPath = jsonOutPath;
    }

    public void process() throws Exception{


        try(FileInputStream fIP = new FileInputStream(excelFile);){
            XSSFWorkbook workbook = new XSSFWorkbook(fIP);
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            for(Sheet sheet ; sheetIterator.hasNext();){
                sheet = sheetIterator.next();
                ExcelSheetProcessor excelSheetProcessor = new ExcelSheetProcessor(sheet);
                excelSheetProcessor.setCsharpOutPath(csharpOutPath);
                excelSheetProcessor.setJavaOutPath(javaOutPath);
                excelSheetProcessor.setJsonOutPath(jsonOutPath);
                excelSheetProcessor.process();

            }

        }


    }


}
