package com.csci572hw.model;

import java.util.List;

public class MainClusterDTO {
	private String name;
	private List<ClusterDTO> children;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ClusterDTO> getChildren() {
		return children;
	}
	public void setChildren(List<ClusterDTO> children) {
		this.children = children;
	}
	
	

}
