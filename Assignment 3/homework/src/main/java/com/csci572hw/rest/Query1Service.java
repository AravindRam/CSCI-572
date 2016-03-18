package com.csci572hw.rest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.csci572hw.model.AddressDTO;
import com.csci572hw.model.AddressStateClusterDTO;
import com.csci572hw.model.AddressStateMainClusterDTO;
import com.csci572hw.solr.QuerySolr;	

@Path("/api/query1")
public class Query1Service {
	
	
	@GET
	@Path("/getBubbleMenu")
	@Produces(MediaType.APPLICATION_JSON)
	public AddressStateMainClusterDTO getBubbleMenu() {
		QuerySolr querySolr=new QuerySolr("localhost",8983,"memexcollection");
		SolrDocumentList response=querySolr.getQueryFromSolr("content%3A+%2F.*shotgun.*%2F+AND+(ctakes_Date_Annotation%3A+%2F2015-01.*%2F+sellerStartDate%3A+%2F2015-01.*%2F+buyerStartDate%3A+%2F2015-01.*%2F)&wt=json&indent=true",10000);
		Map<String,Map<String, String>> listOfMaps = new HashMap<String,Map<String, String>>();
		int state_count=0;
		for(SolrDocument doc: response){
			String category=doc.getFieldValue("category").toString();
			String state=doc.getFieldValue("Geographical_Name_State").toString();
			String imageURL=doc.getFieldValue("images").toString();
			if(category.equals("N/A"))
				continue;
			if(imageURL.equals("N/A"))
				continue;
			if(state.equals("N/A"))
				continue;
			if(!listOfMaps.containsKey(state))
				listOfMaps.put(state, new HashMap<String,String>());			
				
			if(listOfMaps.get(state).containsKey(category))
					continue;
				else
					listOfMaps.get(state).put(category,imageURL.split(",")[0]);
			}
			
		AddressStateMainClusterDTO mainCluster = new AddressStateMainClusterDTO();
		List<AddressStateClusterDTO> clustersList = new LinkedList<AddressStateClusterDTO>();
		for(String state : listOfMaps.keySet()){
			if(state_count<4){
			AddressStateClusterDTO cluster = new AddressStateClusterDTO();
			cluster.setName(state);
			
			List<AddressDTO> children = new LinkedList<AddressDTO>();
			Iterator<String> iter = listOfMaps.get(state).keySet().iterator();
            while (iter.hasNext()) {
                String category = iter.next();
                String address = listOfMaps.get(state).get(category);
                AddressDTO child = new AddressDTO();
				child.setName(category);
				child.setAddress(address);
				children.add(child);
			}
            if (children.size()!=3)
            	continue;
			cluster.setChildren(children);
			
			clustersList.add(cluster);
			}
			state_count++;
		}
		
		mainCluster.setName("Cluster");
		mainCluster.setChildren(clustersList);
		return mainCluster;
	}

		
	@GET
	@Path("/getTimeSeries")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getTimeSeries() {
		QuerySolr querySolr=new QuerySolr("localhost",8983,"memexcollection");
		SolrDocumentList docs = querySolr.getQueryFromSolr("content%3A+%2F.*shotgun.*%2F+AND+(ctakes_Date_Annotation%3A+%2F2015-01.*%2F+sellerStartDate%3A+%2F2010-01.*%2F+buyerStartDate%3A+%2F2010-01.*%2F)&rows=100000&wt=json&indent=true",100);
		List<String> list = new LinkedList<String>();
		for(int i=0; i< docs.size(); i++)
		{
			SolrDocument doc = docs.get(i);
			if(!doc.getFieldValue("sellerStartDate").equals("N/A"))
			{
				list.add(doc.getFieldValue("sellerStartDate").toString());
			}
		}
		return list;
	}
	

}
