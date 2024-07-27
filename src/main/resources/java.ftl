package config;

public class ${className?cap_first} {

<#list fieldName2TypeMap?keys as fieldName>
    private ${fieldName2TypeMap[fieldName]} ${fieldName};
</#list>

    public ${className?cap_first}() {
    }

<#list fieldName2TypeMap?keys as fieldName>
    public ${fieldName2TypeMap[fieldName]} get${fieldName?cap_first}() {
        return ${fieldName};
    }
    public void set${fieldName?cap_first}(${fieldName2TypeMap[fieldName]} ${fieldName}) {
        this.${fieldName} = ${fieldName};
    }
</#list>


}