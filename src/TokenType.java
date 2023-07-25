package com.demo;

// 这里是token类型的定义，我们可以定义为接口常量
// 这里是直接沿用类型定义即可
public interface TokenType {
    // single-character token types
    public static final String TK_PLUS = "+";
    public static final String TK_MINUS = "-";
    public static final String TK_MUL = "*";
    public static final String TK_DIV = "/";
    public static final String TK_NEG = "unary-";
    public static final String TK_LT = "<";
    public static final String TK_GT = ">";
    public static final String TK_LPAREN = "(";
    public static final String TK_RPAREN = ")";
    public static final String TK_LBRACE = "{";
    public static final String TK_RBRACE = "}";
    public static final String TK_LBRACK = "[";
    public static final String TK_RBRACK = "]";
    public static final String TK_COMMA = ",";
    public static final String TK_SEMICOLON = ";";
    public static final String TK_ASSIGN = "=";

    //  double-character token types
    public static final String TK_EQ = "==";
    public static final String TK_NE = "!=";
    public static final String TK_GE = ">=";
    public static final String TK_LE = "<=";
    // block of reserved words
    public static final String TK_RETURN = "return";
    public static final String TK_INT = "int";
    public static final String TK_IF = "if";
    public static final String TK_THEN = "then";
    public static final String TK_ELSE = "else";
    // misc
    public static final String TK_IDENT = "IDENT";
    public static final String TK_BOOL = "bool";

    public static final String TK_INTEGER_CONST = "INTEGER_CONST";
    public static final String TK_BOOL_CONST_TRUE  = "true";
    public static final String TK_BOOL_CONST_FALSE = "false";
    public static final String TK_WHILE = "while";
    public static final String TK_EOF = "EOF";
}
