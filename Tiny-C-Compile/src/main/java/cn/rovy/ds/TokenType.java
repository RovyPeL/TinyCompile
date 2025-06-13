package cn.rovy.ds;

public enum TokenType {

    END,
    KEYWORD,
    TYPE,
    SYMBOL,
    INTEGER,
    FLOAT,
    CHAR,
    STRING,
    IDENTIFIER;

    public String print() {
        return switch (this) {
            case END -> "终止符";
            case KEYWORD -> "关键字";
            case TYPE -> "类型名";
            case SYMBOL -> "符号";
            case INTEGER -> "整数";
            case FLOAT -> "浮点数";
            case CHAR -> "字符";
            case STRING -> "字符串";
            case IDENTIFIER -> "标识符";
        };
    }
}
