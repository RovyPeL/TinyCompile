package cn.rovy.ds;

// 变量信息
public class Var {

	public String type;
	public String valName;
	
	public Var (String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "[type=" + type + ", valName=" + valName + "]";
	}
}
