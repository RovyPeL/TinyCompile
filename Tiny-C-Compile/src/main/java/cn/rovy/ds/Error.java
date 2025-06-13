package cn.rovy.ds;

public class Error {

    /*"line " + lineNum + " : " + "数字格式错误"*/
    String message;

    public Error(String error) {
        this.message = error;
    }

    @Override
    public String toString() {
        return "message:"+message;
    }
}
