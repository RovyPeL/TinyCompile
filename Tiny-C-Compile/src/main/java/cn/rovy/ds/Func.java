package cn.rovy.ds;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 函数信息
@Data
public class Func {

	private int pos; // 函数起始位置
	private String returnType; // 返回类型
	private List<String> argList; // 涉及函数传参顺序的使用
	private Map<String, Var> argMap; //涉及快速查找参数变量
	private List<String> varNames; // 函数作用域变量名列表(涉及变量的创建顺序)
	private Map<String, Var> locals; // 本地变量表
	private Map<String, Var> tmps; // 临时变量表

	public Func(String returnType) {
		this.returnType = returnType;
		varNames = new ArrayList<>();
		argList = new ArrayList<>();
		argMap = new HashMap<>();
		locals = new HashMap<>();
		tmps = new HashMap<>();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String var : varNames)
			str.append(var).append(":").append(getVar(var)).append(" ");
		return "pos=" + pos + " vars=" + str;
	}

	public Var getVar(String varName) {
		Var var = null;
		if (argMap.containsKey(varName))
			var = argMap.get(varName);
		if (locals.containsKey(varName))
			var = locals.get(varName);
		if (tmps.containsKey(varName))
			var = tmps.get(varName);
		return var;
	}
}
