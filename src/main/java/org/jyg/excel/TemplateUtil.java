package org.jyg.excel;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateUtil {

    static Configuration configuration = new Configuration( Configuration.VERSION_2_3_23 );

    static {

        configuration.setClassForTemplateLoading(TemplateUtil.class, "/");

    }

    public static byte[] getTemplate(String ftlName , Map<String, Object> params  )  {
        try {
            Template template = configuration.getTemplate(ftlName);

            try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Writer out = new OutputStreamWriter(bos);){
                template.process(params, out);
                return bos.toByteArray();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static byte[] createJavaBytes(String className,Map<String, String> fieldName2TypeMap){

        fieldName2TypeMap = new HashMap<>(fieldName2TypeMap);


        for(String field : new ArrayList<>(fieldName2TypeMap.keySet())){
            String type = fieldName2TypeMap.get(field);
            if(type.equals("string")){
                fieldName2TypeMap.put(field , "String");
            }
        }


        Map<String , Object> params = new HashMap<>();
        params.put("className",className);
        params.put("fieldName2TypeMap",fieldName2TypeMap);

        return getTemplate("java.ftl",params);

    }

    public static byte[] createCSharpBytes(String className,Map<String, String> fieldName2TypeMap){


        fieldName2TypeMap = new HashMap<>(fieldName2TypeMap);


        for(String field : new ArrayList<>(fieldName2TypeMap.keySet())){
            String type = fieldName2TypeMap.get(field);
            if(type.equals("String")){
                fieldName2TypeMap.put(field , "string");
            }
        }


        Map<String , Object> params = new HashMap<>();
        params.put("className",className);
        params.put("fieldName2TypeMap",fieldName2TypeMap);

        return getTemplate("csharp.ftl",params);

    }




}
