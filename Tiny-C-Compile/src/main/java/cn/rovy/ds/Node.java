package cn.rovy.ds;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 语法树的节点
@Data
public class Node {

	private String type; // 结点类型(对应文法)
	private Node father; // 父结点
	private List<Node> sons; // 子结点
	private Map<String, String> attributeMap;// 属性表

	public Node(String name, Node father) {
		this.type = type;
		this.father = father;
		sons = new ArrayList<>();
		attributeMap = new HashMap<>();
	}
	public Node getSonsByIndex(int index) {
		return sons.get(index);
	}
	public String getAttributeByKey(String key) {
		return attributeMap.get(key);
	}
	public void setAttributeByEq(String key, String value) {
		attributeMap.put(key, value);
	}

}
