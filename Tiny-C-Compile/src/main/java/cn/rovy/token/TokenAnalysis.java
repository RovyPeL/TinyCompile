package cn.rovy.token;

import cn.rovy.ds.GlobalVar;
import cn.rovy.ds.Token;
import cn.rovy.ds.Error;
import cn.rovy.ds.TokenType;

import java.util.*;

public class TokenAnalysis {


    private static final List<String> typeList = new ArrayList<>(Arrays.asList(
            "int", "char", "float"
    ));
    private static final List<String> keywordList = new ArrayList<>(Arrays.asList(
            "if", "while", "for", "do", "else", "return", "void", "static", "main"
    ));
    private static final List<String> symbolList = new ArrayList<>(Arrays.asList(
            "{", "}", "(", ")", ",", ";", "+", "-", "*", "/", "%", "\"", "'",
            "&", "|", "^", "<", ">", "=", "<=", ">=", "!=", "==", "&&", "||",
            "~", "!", "++", "--"
    ));
    private static List<String> identifierList = new ArrayList<>();
    private static List<String> constantList = new ArrayList<>();
    /*存放文件读取的字符串*/
    private String text;
    /*存放词法分析时的行号*/
    private int lineNum;

    // 词法分析
    public void analysis(String text) {
        // 去注释(加空格是防止去除注释后前后代码粘连)
        text = text.replaceAll("//[\\s\\S]*?\\n", "").replaceAll("/\\*(.|\\n)*\\*/", "") + " ";

        this.text = text;
        this.lineNum = 1;
        int num = text.length();
        int index = 0; // 记录当前下标

        while (index < num) {
            char c = text.charAt(index);
            if (c == '\n') {
                lineNum++;
            }
            if (Character.isWhitespace(c)) { // 跳过空白符
                index++;
            } else if (Character.isUpperCase(c) || Character.isLowerCase(c)) { // 开头为字母
                index = handleAlpha(index);
            } else if (Character.isDigit(c)) { // 开头为数字
                index = handleDigit(index);
            } else if (symbolList.contains(c + "")) { // 符号
                if (c == '\"') {
                    int i = index + 1;
                    /*存第一个“ */
                    GlobalVar.getInstance().getTokens().add(new Token(TokenType.SYMBOL, "\"",getTokenTypeNum(TokenType.SYMBOL,"\"")));
                    GlobalVar.getInstance().getTokenLineNum().add(lineNum);
                    /*寻找下个“，直到遇到”或END */
                    while (i < num && text.charAt(i) != '\"')
                        i++;
                    /*把字符串存入*/
                    if (index + 1 <= i) {
                        GlobalVar.getInstance().getTokens().add(new Token(TokenType.STRING, text.substring(index + 1, i),getTokenTypeNum(TokenType.STRING,text.substring(index + 1, i))));
                        GlobalVar.getInstance().getTokenLineNum().add(lineNum);
                    }
                    /*如果是遇到”存进去，遇到END存Error*/
                    if (i < num && text.charAt(i) == '\"') {
                        GlobalVar.getInstance().getTokens().add(new Token(TokenType.SYMBOL, "\"",symbolList.indexOf("\"")));
                        GlobalVar.getInstance().getTokenLineNum().add(lineNum);
                    } else {
                        GlobalVar.getInstance().getErrors().add(new Error("line " + lineNum + " : " + " 缺少 \" "));
                    }
                    index = i + 1;
                } else if (c == '\'') {
                    index++;
                    /*存第一个' */
                    GlobalVar.getInstance().getTokens().add(new Token(TokenType.SYMBOL, "'",symbolList.indexOf("'")));
                    GlobalVar.getInstance().getTokenLineNum().add(lineNum);
                    /*识别下个字符（转义字符和普通字符） */
                    if (index < num && text.charAt(index) == '\\') {
                        GlobalVar.getInstance().getTokens().add(new Token(TokenType.CHAR, text.substring(index, index + 2),getTokenTypeNum(TokenType.CHAR,text.substring(index, index + 2))));
                        GlobalVar.getInstance().getTokenLineNum().add(lineNum);
                        index += 2;
                    }else if (index < num && text.charAt(index) != '\'') {
                        GlobalVar.getInstance().getTokens().add(new Token(TokenType.CHAR, text.charAt(index) + "",getTokenTypeNum(TokenType.CHAR,text.charAt(index) + "")));
                        GlobalVar.getInstance().getTokenLineNum().add(lineNum);
                        index += 1;
                    }
                    /*如果是遇到'存进去，遇到END存Error*/
                    if (index < num && text.charAt(index) == '\'') {
                        GlobalVar.getInstance().getTokens().add(new Token(TokenType.CHAR,"\0",getTokenTypeNum(TokenType.CHAR,"\0")));
                        GlobalVar.getInstance().getTokenLineNum().add(lineNum);
                        GlobalVar.getInstance().getTokens().add(new Token(TokenType.SYMBOL, "'",getTokenTypeNum(TokenType.SYMBOL,"'")));
                        GlobalVar.getInstance().getTokenLineNum().add(lineNum);
                        index++;
                    } else {
                        GlobalVar.getInstance().getErrors().add(new Error("line " + lineNum + " : " + " 缺少 ' "));
                    }
                }else if (index + 1 < num && symbolList.contains(text.substring(index, index + 2))) {   //处理双字符运算符
                    GlobalVar.getInstance().getTokens().add(new Token(TokenType.SYMBOL, text.substring(index, index + 2),getTokenTypeNum(TokenType.SYMBOL,text.substring(index, index + 2))));
                    GlobalVar.getInstance().getTokenLineNum().add(lineNum);
                    index += 2;
                } else {
                    GlobalVar.getInstance().getTokens().add(new Token(TokenType.SYMBOL, c + "",getTokenTypeNum(TokenType.SYMBOL,c + "")));
                    GlobalVar.getInstance().getTokenLineNum().add(lineNum);
                    index++;
                }
            } else {
                GlobalVar.getInstance().getErrors().add(new Error("line " + lineNum + " : " + "无法识别 " + c));
                index++;
            }
        }

        // 添加终止符
        GlobalVar.getInstance().getTokens().add(new Token(TokenType.END, "END",getTokenTypeNum(TokenType.END,"END")));
        GlobalVar.getInstance().getTokenLineNum().add(lineNum);
        //保存每个token对应的行号
    }

    // 开头为字母
    private int handleAlpha(int index) {
        int i = index;
        char c;
        do {
            i++;
            c = text.charAt(i);
        } while (Character.isUpperCase(c) || Character.isLowerCase(c) || Character.isDigit(c));

        String s = text.substring(index, i);

        // 判断 类型名 关键字 标识符
        if (typeList.contains(s)) {
            GlobalVar.getInstance().getTokens().add(new Token(TokenType.TYPE, s,getTokenTypeNum(TokenType.TYPE,s)));
            GlobalVar.getInstance().getTokenLineNum().add(lineNum);
        } else if (keywordList.contains(s)) {
            GlobalVar.getInstance().getTokens().add(new Token(TokenType.KEYWORD, s,getTokenTypeNum(TokenType.KEYWORD,s)));
            GlobalVar.getInstance().getTokenLineNum().add(lineNum);
        } else {
            GlobalVar.getInstance().getTokens().add(new Token(TokenType.IDENTIFIER, s,getTokenTypeNum(TokenType.IDENTIFIER,s)));
            GlobalVar.getInstance().getTokenLineNum().add(lineNum);
        }

        return i;
    }

    // 开头为数字
    private int handleDigit(int index) {
        int i = index;
        char c;
        do {
            i++;
            c = text.charAt(i);
        } while (Character.isDigit(c));

        // 判断 小数 整数
        if (c == '.') {
            do {
                i++;
                c = text.charAt(i);
            } while (Character.isDigit(c));
            String s = text.substring(index, i);
            if (s.charAt(s.length()-1) == '.') {
                GlobalVar.getInstance().getErrors().add(new Error("line " + lineNum + " : " + "数字格式错误"));
            } else {
                GlobalVar.getInstance().getTokens().add(new Token(TokenType.FLOAT, s,getTokenTypeNum(TokenType.FLOAT,s)));
                GlobalVar.getInstance().getTokenLineNum().add(lineNum);
            }
        } else {
            String s = text.substring(index, i);
            GlobalVar.getInstance().getTokens().add(new Token(TokenType.INTEGER, s,getTokenTypeNum(TokenType.INTEGER,s)));
            GlobalVar.getInstance().getTokenLineNum().add(lineNum);
        }

        return i;
    }

    private int getTokenTypeNum(TokenType type,String s) {
        return switch (type) {
            case SYMBOL -> symbolList.indexOf(s);
            case IDENTIFIER -> {
                identifierList.add(s);
                identifierList = new ArrayList<>(new HashSet<>(identifierList));
                yield identifierList.indexOf(s);
            }
            case KEYWORD -> keywordList.indexOf(s);
            case TYPE -> typeList.indexOf(s);
            case STRING,CHAR,INTEGER,FLOAT -> {
                constantList.add(s);
                constantList = new ArrayList<>(new HashSet<>(constantList));
                yield constantList.indexOf(s);
            }
            case END -> 0;
        } + 1;
    }
}
