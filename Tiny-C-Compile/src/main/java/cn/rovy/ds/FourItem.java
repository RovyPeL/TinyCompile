package cn.rovy.ds;

import lombok.Data;

// 四元式
@Data
public class FourItem {

	private String op;
	private String arg1;
	private String arg2;
	private String result;

	public FourItem(String op, String arg1, String arg2, String result) {
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.result = result;
	}

	@Override
	public String toString() {
		return "<" + op + " , " + arg1 + " , " + arg2 + " , " + result + ">\n";
	}
}
