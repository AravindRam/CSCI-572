package com.csci572hw.model;
import java.util.List;

public class AddressStateMainClusterDTO {
		private String name;
		private List<AddressStateClusterDTO> children;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<AddressStateClusterDTO> getChildren() {
			return children;
		}
		public void setChildren(List<AddressStateClusterDTO> clustersList) {
			this.children = clustersList;
		}
}
