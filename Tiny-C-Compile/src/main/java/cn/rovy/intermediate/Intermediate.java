package cn.rovy.intermediate;

import cn.rovy.ds.*;

import java.util.ArrayList;
import java.util.List;

public class Intermediate {

	private Func func;

	public void generate() {
		buildFunc(GlobalVar.getInstance().getRootNode().getSonsByIndex(0));
		buildFuncs(GlobalVar.getInstance().getRootNode().getSonsByIndex(1));

	}

	private void buildFunc(Node nodeFunc) {
		/*获取该结点函数的函数名*/
		String funcName = nodeFunc.getSonsByIndex(1).getAttributeByKey("name");
		/*获取函数*/
		func = GlobalVar.getInstance().getFuncsMap().get(funcName);
		/*设置函数表中该函数的执行位置*/
		func.setPos(GlobalVar.getInstance().getFourItemList().size() + 1);

		/*生成函数内：变量声明列表，语句*/
		buildDefinelist(nodeFunc.getSonsByIndex(6));
		buildStatements(nodeFunc.getSonsByIndex(7));
	}

	private void buildFuncs(Node nodefuncs) {
		String type = nodefuncs.getSonsByIndex(0).getType();

		if(type.equals("func")) {
			buildFunc(nodefuncs.getSonsByIndex(0));
			buildFuncs(nodefuncs.getSonsByIndex(1));
		}
	}

	/*处理变量声明列表*/
	private void buildDefinelist(Node definelist) {
		if (definelist.getSons().size() != 1){
			buildDefinestatement(definelist.getSonsByIndex(0));
			buildDefinelist(definelist.getSonsByIndex(1));
		}
	}

	/*声明赋值*/
	private void defineInit(Node definestatement,Node init) {
		if (init.getSons().size() > 1) {
			Node expression = init.getSonsByIndex(1);
			buildExpression(expression);

			/*表达式的值或临时变量的下标*/
			String exprValue = expression.getAttributeByKey("value");
			/*生成赋值四元式*/
			/*无临时变量*/
			FourItem fourItem = new FourItem("=", exprValue, "", definestatement.getSonsByIndex(1).getAttributeByKey("name"));
			/*有临时变量*/
			if (expression.getAttributeByKey("flag").equals("true")) {
				fourItem.setArg1(GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(exprValue) - 1).getResult());
			}
			GlobalVar.getInstance().getFourItemList().add(fourItem);
		}

		buildConse(definestatement.getSonsByIndex(3));
	}

	/*声明体*/
	private void buildDefinestatement(Node definestatement) {
		Node init = definestatement.getSonsByIndex(2);
		defineInit(definestatement,init);
	}

	/*连续声明*/
	private void buildConse(Node conse) {
		if (conse.getSons().size() != 1){
			Node init = conse.getSonsByIndex(2);
			defineInit(conse,init);
		}
	}

	private void buildExpression(Node expression) {
		/*单元与表达式操作*/
		Node item = expression.getSonsByIndex(0);
		Node expr = expression.getSonsByIndex(1);
		buildItem(item);
		buildExpr(expr);

		/*expr为空，表达式的值就为item的值*/
		if (expr.getAttributeByKey("op2") == null) {
			expression.setAttributeByEq("value", item.getAttributeByKey("value"));
			expression.setAttributeByEq("flag", item.getAttributeByKey("flag"));
		} else {
			String op2 = expr.getAttributeByKey("op2");
			String val = "#" + (GlobalVar.getInstance().getFourItemList().size() + 1);	//行数
			FourItem fourItem = new FourItem(op2, "", "", val);
			String itemVal = item.getAttributeByKey("value");
			String exprVal = expr.getAttributeByKey("value");

			/*判断两个参数是否是临时变量*/
			String arg1 = item.getAttributeByKey("flag").equals("true")
					? GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(itemVal) - 1).getResult()
					: itemVal;
			String arg2 = expr.getAttributeByKey("flag").equals("true")
					? GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(exprVal) - 1).getResult()
					: exprVal;
			fourItem.setArg1(arg1);
			fourItem.setArg2(arg2);

			String type = expression.getAttributeByKey("type");
			func.getVarNames().add(val);
			func.getTmps().put(val, new Var(type));

			if (op2.equals("=")) {
				func.getVarNames().remove(val);
				func.getTmps().remove(val);
				fourItem.setResult(fourItem.getArg1());
				fourItem.setArg1(fourItem.getArg2());
				fourItem.setArg2("");
			}
			GlobalVar.getInstance().getFourItemList().add(fourItem);
			expression.setAttributeByEq("val", GlobalVar.getInstance().getFourItemList().size() + "");
			expression.setAttributeByEq("flag", "true");
		}
	}

	private void buildItem(Node item) {
		Node op = item.getSonsByIndex(0);
		item.setAttributeByEq("flag", "false");

		/*单元运算是标识符(调用函数)*/
        switch (op.getType()) {
            case "IDENTIFIER" -> {
                /*标识符的名字*/
                String identifierName = op.getAttributeByKey("name");
                /*单标识符*/
                if (item.getSonsByIndex(1).getSons().size() == 1) {
                    item.setAttributeByEq("name", identifierName);
                }
			/*自带函数printf
			else if (id.equals("printf")) {
				FourItem fourItem = new FourItem("print", "", "", "");
				Node expression = item.getSonsByIndex(1).getSonsByIndex(1).getSonsByIndex(0);
				buildExpression(expression);
				String val = expression.getAttributeByKey("val");
				if (expression.getAttributeByKey("flag").equals("true"))
					val = GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(val) - 1).getResult();
				fourItem.setResult(val);
				GlobalVar.getInstance().getFourItemList().add(fourItem);
			}*/
                /*函数调用*/
                else {
                    FourItem fourItem = new FourItem("call", identifierName, "", "");
                    List<String> list = new ArrayList<>();    //存放参数

                    Node parameters = item.getSonsByIndex(1).getSonsByIndex(1);
                    if (parameters.getSons().size() > 1) {
                        Node expression = parameters.getSonsByIndex(0);
                        buildExpression(expression);
                        String val = expression.getAttributeByKey("value");
                        if (expression.getAttributeByKey("flag").equals("true"))
                            val = GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(val) - 1).getResult();
                        list.add(val);	//参数1
                        buildParam(list, parameters.getSonsByIndex(1));
                        fourItem.setArg2(list.toString());
                    }
                    /*结果存放在临时变量里*/
                    fourItem.setResult("#" + (GlobalVar.getInstance().getFourItemList().size() + 1));

                    /*将变量信息存入函数信息中*/
                    String type = item.getAttributeByKey("type");
                    func.getVarNames().add(fourItem.getResult());
                    func.getTmps().put(fourItem.getResult(), new Var(type));

                    /*函数调用产生临时变量*/
                    GlobalVar.getInstance().getFourItemList().add(fourItem);
                    item.setAttributeByEq("value", GlobalVar.getInstance().getFourItemList().size() + "");
                    item.setAttributeByEq("flag", "true");
                }
            }
            case "INTEGER" -> item.setAttributeByEq("value", op.getAttributeByKey("value"));
            case "FLOAT" -> item.setAttributeByEq("value", op.getAttributeByKey("value"));
            case "'" -> item.setAttributeByEq("value", "'" + item.getSonsByIndex(1).getAttributeByKey("value") + "'");
            case "\"" -> item.setAttributeByEq("value", "\"" + item.getSonsByIndex(1).getAttributeByKey("value") + "\"");
            case "(" -> {
                Node expression = item.getSonsByIndex(1);
                buildExpression(expression);
                item.setAttributeByEq("value", expression.getAttributeByKey("value"));
                item.setAttributeByEq("flag", expression.getAttributeByKey("flag"));
            }
            case "op1" -> {
                Node item2 = item.getSonsByIndex(1);
                buildItem(item2);
                String op1 = op.getAttributeByKey("value");
                String val = "#" + (GlobalVar.getInstance().getFourItemList().size() + 1); //临时变量

                FourItem fourItem = new FourItem(op1, item2.getAttributeByKey("value"), "", val);
				/*如果变量1是临时变量，将上个四元式的结果作为该变量的值*/
                if (item2.getAttributeByKey("flag").equals("true"))
                    fourItem.setArg1(GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(fourItem.getArg1()) - 1).getResult());

                String type = item.getAttributeByKey("type");	//变量类型
                func.getVarNames().add(val);
                func.getTmps().put(val, new Var(type));

                if (op1.equals("++") || op1.equals("--")) {
					/*自增自减无需再创建临时变量*/
                    func.getVarNames().remove(val);
                    func.getTmps().remove(val);

                    fourItem.setOp(fourItem.getOp().substring(0, 1));
                    fourItem.setResult(fourItem.getArg1());
                    fourItem.setArg2("1");
                }
				/*正负操作*/
				else if (op1.equals("+") || op1.equals("-")) {
                    fourItem.setArg2(fourItem.getArg1());
                    fourItem.setArg1("0");
                }

                GlobalVar.getInstance().getFourItemList().add(fourItem);
                item.setAttributeByEq("value", GlobalVar.getInstance().getFourItemList().size() + "");
                item.setAttributeByEq("flag", "true");
            }
        }
	}

	private void buildExpr(Node expr) {
		if(expr.getSons().size() > 1) {
			Node op2 = expr.getSonsByIndex(0);

			expr.setAttributeByEq("op2", op2.getAttributeByKey("value"));
			Node expression = expr.getSonsByIndex(1);
			buildExpression(expression);
			/*子表达式的操作*/
			expr.setAttributeByEq("value", expression.getAttributeByKey("value"));
			expr.setAttributeByEq("flag", expression.getAttributeByKey("flag"));
		}
	}

	private void buildParam(List<String> list, Node param) {
		if (param.getSons().size() != 1){
			Node expression = param.getSonsByIndex(1);
			buildExpression(expression);
			String val = expression.getAttributeByKey("value");
			if (expression.getAttributeByKey("flag").equals("true"))
				val = GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(val) - 1).getResult();
			list.add(val);
			buildParam(list, param.getSonsByIndex(2));
		}
	}

	private void buildStatements(Node statements) {
		if(statements.getSons().size() > 1) {
			buildStatement(statements.getSonsByIndex(0));
			buildStatements(statements.getSonsByIndex(1));
		}
	}

	private void buildStatement(Node statement) {
		Node son = statement.getSonsByIndex(0);
		String sonType = son.getType();
        switch (sonType) {
            case "expression" -> buildExpression(son);
            case "ifstatement" -> buildIfstatement(son);
            case "whilestatement" -> buildWhilestatement(son);
            case "forstatement" -> buildForstatement(son);
            case "dowhilestatement" -> buildDowhilestatement(son);
            case "returnstatement" -> buildReturnstatement(son);
        }
	}

	/*
	jnz 不为0跳转
	j   无条件跳转
	jz  为0跳转
	*/
	private void buildIfstatement(Node ifstatement) {
		Node expression = ifstatement.getSonsByIndex(2);
		Node statements = ifstatement.getSonsByIndex(5);
		Node elsestate = ifstatement.getSonsByIndex(7);

		buildExpression(expression);
		String val = expression.getAttributeByKey("value");

		/*
		n-1	<jnz,val, ,n+2>
		n 	<false四元式集> "getsize四元式集最后一个的下标"n
		n+1	<j, , ,m+1> "else执行完无条件跳转"
		n+2 m <true四元式集> "getsize四元式集最后一个的下标"m
		m+1
		*/
		FourItem jnzItem = new FourItem("jnz", val, "", "");
		if (expression.getAttributeByKey("flag").equals("true")) {
			jnzItem.setArg1(GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(val) - 1).getResult());
		}
		/*插入if条件判断跳转四元式*/
		GlobalVar.getInstance().getFourItemList().add(jnzItem);
		/*false四元式集*/
		buildElsestate(elsestate);
		/*设置if条件判断跳转四元式跳转目标*/
		jnzItem.setResult((GlobalVar.getInstance().getFourItemList().size() + 2) + "");

		FourItem jItem = new FourItem("j", "", "", "");
		/*插入无条件跳转四元式*/
		GlobalVar.getInstance().getFourItemList().add(jItem);
		/*true四元式集*/
		buildStatements(statements);
		/*设置无条件跳转四元式跳转目标*/
		jItem.setResult((GlobalVar.getInstance().getFourItemList().size() + 1) + "");
	}

	private void buildElsestate(Node elsestate) {
		String type = elsestate.getSonsByIndex(0).getType();
		if (type.equals("else")) {
			buildStatements(elsestate.getSonsByIndex(2));
		}
	}

	private void buildWhilestatement(Node whilestatement) {
		Node expression = whilestatement.getSonsByIndex(2);
		Node statements = whilestatement.getSonsByIndex(5);

		/*
		label <jz,val, ,n+2>
			  <循环四元式集> "getsize四元式集最后一个的下标"n
		n+1	  <j, , ,label>
		n+2
		*/
		int label = GlobalVar.getInstance().getFourItemList().size() + 1;
		buildExpression(expression);
		String val = expression.getAttributeByKey("value");
		FourItem jzItem = new FourItem("jz", val, "", "");
		if (expression.getAttributeByKey("flag").equals("true")) {
			jzItem.setArg1(GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(val) - 1).getResult());
		}
		GlobalVar.getInstance().getFourItemList().add(jzItem);

		buildStatements(statements);

		jzItem.setResult((GlobalVar.getInstance().getFourItemList().size() + 2) + "");

		FourItem jItem = new FourItem("j", "", "", label + "");
		GlobalVar.getInstance().getFourItemList().add(jItem);
	}

	private void buildForstatement(Node forstatement) {
		Node expression1 = forstatement.getSonsByIndex(2);
		Node expression2 = forstatement.getSonsByIndex(4);
		Node expression3 = forstatement.getSonsByIndex(6);
		Node statements = forstatement.getSonsByIndex(9);

		/*基本框架同while*/
		/*for循环第一个表达式*/
		buildExpression(expression1);

		int label = GlobalVar.getInstance().getFourItemList().size() + 1;
		/*for循环第二个表达式(循环条件)*/
		buildExpression(expression2);
		String val = expression2.getAttributeByKey("value");
		FourItem jzItem = new FourItem("jz", val, "", "");
		if (expression2.getAttributeByKey("flag").equals("true")) {
			jzItem.setArg1(GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(val) - 1).getResult());
		}
		GlobalVar.getInstance().getFourItemList().add(jzItem);

		buildStatements(statements);
		buildExpression(expression3);

		jzItem.setResult((GlobalVar.getInstance().getFourItemList().size() + 2) + "");

		FourItem jItem = new FourItem("j", "", "", label + "");
		GlobalVar.getInstance().getFourItemList().add(jItem);
	}

	private void buildDowhilestatement(Node dowhilestatement) {
		Node statements = dowhilestatement.getSonsByIndex(2);
		Node expression = dowhilestatement.getSonsByIndex(6);


		/*
		label <循环四元式集>
			  <jnz,val, ,label>
		*/
		int label = GlobalVar.getInstance().getFourItemList().size() + 1;
		buildStatements(statements);

		buildExpression(expression);
		String val = expression.getAttributeByKey("value");
		FourItem jnzItem = new FourItem("jnz", val, "", label + "");
		if (expression.getAttributeByKey("flag").equals("true")) {
			jnzItem.setArg1(GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(val) - 1).getResult());
		}
		GlobalVar.getInstance().getFourItemList().add(jnzItem);
	}

	private void buildReturnstatement(Node returnstatement) {
		Node ret = returnstatement.getSonsByIndex(1).getSonsByIndex(0);
		String type = ret.getType();
		if (type.equals("EMPTY")) {
			FourItem fourItem = new FourItem("ret", "", "", "");
			GlobalVar.getInstance().getFourItemList().add(fourItem);
		} else if (type.equals("expression")) {
			buildExpression(ret);
			String val = ret.getAttributeByKey("value");
			FourItem fourItem = new FourItem("ret", "", "", val);
			if (ret.getAttributeByKey("flag").equals("true")) {
				fourItem.setResult(GlobalVar.getInstance().getFourItemList().get(Integer.parseInt(val) - 1).getResult());
			}
			GlobalVar.getInstance().getFourItemList().add(fourItem);
		}
	}
}
