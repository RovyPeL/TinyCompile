package cn.rovy.ds;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class GlobalVar {
    private static GlobalVar instance;

    /*编译时遇到的问题*/
    private List<Error> errors;

    /*词法分析*/
    private List<Token> tokens;             //存放识别到的token
    private List<Integer> tokenLineNum;    //存放与tokens下标对应token的行号

    /*语法分析与语义分析*/
    private Node rootNode;
    private Map<String, Func> funcsMap;//函数表

    /*中间代码生成与优化*/
    private List<FourItem> fourItemList;

    /*生成汇编代码*/



    private GlobalVar() {
        errors = new ArrayList<>();
        tokens = new ArrayList<>();
        tokenLineNum = new ArrayList<>();
        rootNode = new Node("root", null);
        fourItemList = new ArrayList<>();
    }

    public static GlobalVar getInstance(){
        if(instance == null){
            instance = new GlobalVar();
        }
        return instance;
    }

    public String showTokenAndNum(){
        StringBuilder str = null;
        for(int i=0;i<this.tokens.size();i++){
            if(str == null){
                str = new StringBuilder(this.tokenLineNum.get(i).toString()).append(":").append(this.tokens.get(i).toString());
            }else {
                str.append(this.tokenLineNum.get(i).toString()).append(":").append(this.tokens.get(i).toString());
            }
        }
        return str == null ? null : str.toString();
    }
}
