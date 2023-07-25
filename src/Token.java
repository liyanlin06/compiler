package com.demo;

public class Token {
    private String type;
    private String value;
    private int lineno;
    private int column;
    private int width;

    public Token() {
        lineno = 0;
        column = 0;
        width = 0;
    }

    public Token(String type, String value, int lineno, int column, int width) {
        this.type = type;
        this.value = value;
        this.lineno = lineno;
        this.column = column;
        this.width = width;
    }

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLineno() {
        return lineno;
    }

    public void setLineno(int lineno) {
        this.lineno = lineno;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
