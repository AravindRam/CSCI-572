package com.csci572hw.model;
import java.util.List;

public class AddressStateClusterDTO {

	private String name;
	private List<AddressDTO> children;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<AddressDTO> getChildren() {
		return children;
	}
	public void setChildren(List<AddressDTO> children) {
		this.children = children;
	}

	
	

}
