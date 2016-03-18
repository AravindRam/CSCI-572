package com.csci572hw.model;

import java.util.List;

public class ClusterDTO {
	private Double name;
	private List<ChildrenDTO> children;
	
	public Double getName() {
		return name;
	}
	public void setName(Double name) {
		this.name = name;
	}
	public List<ChildrenDTO> getChildren() {
		return children;
	}
	public void setChildren(List<ChildrenDTO> children) {
		this.children = children;
	}
}
