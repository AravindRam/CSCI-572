package com.csci572hw.model;
import java.util.List;

public class StateMainClusterDTO {
		private String name;
		private List<StateClusterDTO> children;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<StateClusterDTO> getChildren() {
			return children;
		}
		public void setChildren(List<StateClusterDTO> children) {
			this.children = children;
		}
}
