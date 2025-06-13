package cn.rovy.ds;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 语法树的节点
@Data
public class Node {

	private String name; // 名字
	private Node father; // 父节点
	private List<Node> sons; // 子节点
	private Map<String, String> attributeMap;// 属性表

	public Node(String name, Node father) {
		super();
		this.name = name;
		this.father = father;
		sons = new ArrayList<>();
		attributeMap = new HashMap<>();
	}
}
