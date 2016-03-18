package com.csci572hw.model;
import java.util.List;

public class StateClusterDTO {

	private String name;
	private List<ChildrenDTO> children;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ChildrenDTO> getChildren() {
		return children;
	}
	public void setChildren(List<ChildrenDTO> children) {
		this.children = children;
	}

}
