package org.jyg.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * create by jiayaoguang on 2021/6/12
 */
public class StartUp {


    public static void main(String[] args) throws Exception {





//        File file = new File(rootDirName + "/test.xlsx");


//        if (file.isFile() && file.exists()) {
//
//        } else {
//            System.out.println(
//                    "Error to open rootDirName");
//        }

        Properties properties = new Properties();
        try (InputStream in = new BufferedInputStream(Files.newInputStream(new File("config.properties").toPath()));) {
            properties.load(in);
        }

        String excelDir = (String)properties.getOrDefault("excelDir","excel");
        String excelName = (String)properties.getProperty("excelName");

        String javaOutDir = (String)properties.getOrDefault("javaOutDir","java");
        String csharpOutDir = (String)properties.getOrDefault("csharpOutDir","csharp");

        String jsonOutDir = (String)properties.getOrDefault("jsonOutDir","json");

        if(StringUtils.isNotEmpty(excelName) && !"all".equals(excelName)){
            File excelFile = new File(excelName);
            try {
                ExcelProcessor excelProcessor = new ExcelProcessor(excelFile);
                excelProcessor.setCsharpOutPath(csharpOutDir);
                excelProcessor.setJavaOutPath(javaOutDir);
                excelProcessor.process();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }


        File rootDir = new File(excelDir);
        dealExcelDir(rootDir , csharpOutDir,javaOutDir , jsonOutDir);


    }


    public static void dealExcelDir(File excelRootDir,String csharpOutDir,String javaOutDir , String jsonOutDir) {
        String rootDirName = excelRootDir.getName();
        if (!excelRootDir.exists()) {
            throw new IllegalArgumentException("root dir not found : " + rootDirName);
        }

        if (!excelRootDir.isDirectory()) {
            throw new IllegalArgumentException("root file nor dir : " + rootDirName);
        }
        File[] listFiles = excelRootDir.listFiles();
        if (listFiles == null) {
            throw new IllegalArgumentException("root file listFiles is null : " + rootDirName);
        }

        for (File childFile : listFiles) {
            if (childFile.isDirectory()) {
                dealExcelDir(childFile , csharpOutDir , javaOutDir , jsonOutDir);
                return;
            }
            if(childFile.getName().startsWith("$")){
                return;
            }
            if ( !childFile.getName().endsWith(".xlsx") && !childFile.getName().endsWith(".xls") ){
                return;
            }

            try {
                ExcelProcessor excelProcessor = new ExcelProcessor(childFile);
                excelProcessor.setCsharpOutPath(csharpOutDir);
                excelProcessor.setJavaOutPath(javaOutDir);
                excelProcessor.setJsonOutPath(jsonOutDir);
                excelProcessor.process();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
