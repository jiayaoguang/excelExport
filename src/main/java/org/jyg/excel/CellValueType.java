package org.jyg.excel;

/**
 * create by jiayaoguang on 2021/6/27
 */
public enum CellValueType {
    BYTE("byte"),
    SHORT("short"),
    INT("int"),
    LONG("long"),

    STRING("string"),
    ;
    private final String className;

    CellValueType(String className) {
        this.className = className;
    }
}
