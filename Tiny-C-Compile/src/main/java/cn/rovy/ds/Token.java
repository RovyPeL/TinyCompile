package cn.rovy.ds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private TokenType type;	// 类型
    private String value;
    private int num;

    @Override
    public String toString() {
        return "Token[" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", num=" + num +
                ']' + '\n';
    }
}
