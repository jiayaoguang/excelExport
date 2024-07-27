package org.jyg.excel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * create by jiayaoguang on 2021/6/12
 */
public class ExcelSheetProcessor {

    private final Sheet sheet;


    private final String genClassPrefix = "Conf";

    private String javaOutPath;
    private String csharpOutPath;
    private String jsonOutPath;


    public ExcelSheetProcessor(Sheet excelSheet) {
        this.sheet = excelSheet;
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

    public void process() {

        if (sheet.getSheetName().contains(" ")) {
            throw new IllegalArgumentException("sheet.getSheetName().contains space char");
        }

        String[] nameAndNote = sheet.getSheetName().split("\\|");
        if (nameAndNote.length > 2) {
            throw new IllegalArgumentException("sheet name error");
        }

        String sheetName = nameAndNote[0];

        String mappingClassName = genClassPrefix + nameAndNote[0];

        if (sheetName.isEmpty()) {
            throw new RuntimeException("sheetName.isEmpty() ");
        } else if (sheetName.length() == 1) {
            mappingClassName = genClassPrefix + nameAndNote[0].toUpperCase();
        } else {
            mappingClassName = genClassPrefix + nameAndNote[0].substring(0, 1).toUpperCase() + nameAndNote[0].substring(1);
        }

        String comment = "sheet>" + mappingClassName;

        if (nameAndNote.length == 2) {
            comment = nameAndNote[1];
        }

        System.out.println("find sheet :"+mappingClassName + " ( " + comment + ")");

        Iterator<Row> rowIterator = sheet.rowIterator();

        ObjectMapper mapper = new ObjectMapper();


        List<Map<String, Object>> valuesList = new ArrayList<>();

        Map<Integer, String> columnIndex2nameMap = new HashMap<>();

        Map<String, String> fieldName2TypeMap = new LinkedHashMap<>();

        int fieldStartlineIndex = 0;

        for (Row row; rowIterator.hasNext(); ) {
            row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();

            int rowNum = row.getRowNum();

//            System.out.println("rowNum" + rowNum);

            Map<String, Object> name2cellValueMap = new HashMap<>();

            if(rowNum >= fieldStartlineIndex + 3){
                valuesList.add(name2cellValueMap);
            }

            for (Cell cell; cellIterator.hasNext(); ) {
                cell = cellIterator.next();

                CellType cellType = cell.getCellType();

                Object value = null;

                switch (cellType) {
                    case _NONE:
                        value = null;
                        break;
                    case NUMERIC:
                        value = cell.getNumericCellValue();
                        break;
                    case STRING:
                        value = cell.getStringCellValue();
                        break;
                    case BOOLEAN:
                        value = cell.getBooleanCellValue();
                        break;
                    default:
                        value = cell.getStringCellValue();
                        break;
                }

                if (rowNum == fieldStartlineIndex) {
                    columnIndex2nameMap.put(cell.getColumnIndex(), String.valueOf(value));
                }
                if (rowNum == fieldStartlineIndex + 1) {
                    String fieldName = columnIndex2nameMap.get(cell.getColumnIndex());
                    if(fieldName == null){
                        System.out.println(sheetName + " ,find unknown fieldName type : " + value);
                    }else {
                        fieldName2TypeMap.put(fieldName, String.valueOf(value));
                    }
                }

                if (rowNum >= fieldStartlineIndex + 3) {

                    String fieldName = columnIndex2nameMap.get(cell.getColumnIndex());
                    if(fieldName == null){
                        System.out.println(sheetName + " , unknown fieldName value : " + value);
                    }else {
                        String type = fieldName2TypeMap.get(fieldName);
                        if(StringUtils.isNotEmpty(type)){
                            type = type.toLowerCase();
                            switch (type){
                                case "byte":
                                case "short":
                                case "int":
                                case "long":
                                case "float":
                                case "double":{
                                    if(value == null){
                                        value = 0;
                                    }else {
                                        value = NumberUtils.toLong(value.toString());
                                    }
                                    break;
                                }
                                case "bool":
                                case "boolean":{
                                    if(value == null){
                                        value = false;
                                    }else if (!(value instanceof Boolean)) {
                                        value = ("true".equalsIgnoreCase(value.toString()));
                                    }
                                    break;
                                }
                                case "string[]":{
                                    if(value == null){
                                        value = new String[0];
                                    }else {
                                        value = value.toString().split(",");
                                    }
                                    break;
                                }


                                case "byte[]":
                                case "short[]":
                                case "int[]":
                                case "long[]":{
                                    if(value == null){
                                        value = new long[0];
                                    }else {
                                        String[] numStrs = value.toString().split(",");
                                        long[] nums = new long[numStrs.length];
                                        for(int i=0;i<numStrs.length;i++){
                                            nums[i] = NumberUtils.toLong(numStrs[i] , 0L);
                                        }
                                        value = nums;
                                    }
                                    break;
                                }

                                case "float[]":
                                case "double[]":{
                                    if(value == null){
                                        value = new double[0];
                                    }else {
                                        String[] numStrs = value.toString().split(",");
                                        double[] nums = new double[numStrs.length];
                                        for(int i=0;i<numStrs.length;i++){
                                            nums[i] = NumberUtils.toDouble(numStrs[i] , 0.0d);
                                        }
                                        value = nums;
                                    }
                                    break;
                                }


                                case "char[]":{
                                    if(value == null){
                                        value = new char[0];
                                    }else {
                                        String[] numStrs = value.toString().split(",");
                                        double[] chars = new double[numStrs.length];
                                        for(int i=0;i<numStrs.length;i++){
                                            chars[i] = numStrs[i].charAt(0);
                                        }
                                        value = chars;
                                    }
                                    break;
                                }

                                case "string": {
                                    if(value == null){
                                        value = "";
                                    }else if (!(value instanceof String)) {
                                        value = value.toString();
                                    }
                                    break;
                                }
                                default:
                                    throw new UnsupportedOperationException("unknown type field type : "+ type);


                            }
                        }

                        name2cellValueMap.put(fieldName, value);
                    }
                }

//                System.out.println("cell column : " + cell.getColumnIndex() + " row : " + cell.getRowIndex() + "  " + value);



            }


        }


        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(valuesList);

            FileUtils.writeStringToFile(new File(jsonOutPath + "/"+mappingClassName + ".json") , json);
            makeJavaFile(mappingClassName , fieldName2TypeMap);
            makeCSharpFile(mappingClassName , fieldName2TypeMap);
        } catch (IOException e ) {
            e.printStackTrace();
        }


    }



    private void makeJavaFile(String className,Map<String, String> fieldName2TypeMap){
        byte[] fileBytes = TemplateUtil.createJavaBytes(className , fieldName2TypeMap);
        try {
            FileUtils.writeByteArrayToFile(new File(javaOutPath + "/"+className + ".java") , fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeCSharpFile(String className,Map<String, String> fieldName2TypeMap){
        byte[] fileBytes = TemplateUtil.createCSharpBytes(className , fieldName2TypeMap);
        try {
            FileUtils.writeByteArrayToFile(new File(csharpOutPath + "/"+className + ".cs") , fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
