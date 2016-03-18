package com.csci572hw.rest;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.csci572hw.model.ChildrenDTO;
import com.csci572hw.model.StateClusterDTO;
import com.csci572hw.model.StateMainClusterDTO;
import com.csci572hw.solr.QuerySolr;

@Path("/api/query5")
public class Query5Service {
	
	@GET
	@Path("/getBubbleChart")
	@Produces(MediaType.APPLICATION_JSON)
	public StateMainClusterDTO getBubbleChart(){
		
		QuerySolr querySolr=new QuerySolr("localhost",8983,"memexcollection");
		SolrDocumentList response=querySolr.getQueryFromSolr("missile&wt=json&indent=true",10);
		Map<String,Map<String, Integer>> listOfMaps = new HashMap<String,Map<String, Integer>>();
		for(SolrDocument doc: response){
			String content=doc.getFieldValue("content").toString();
			String state=doc.getFieldValue("Geographical_Name_State").toString();
			if(state.equals("N/A"))
				continue;
			if(!listOfMaps.containsKey(state))
				listOfMaps.put(state, new HashMap<String,Integer>());
			
			String []contentArray=content.split(" ");
			for(String str:contentArray){
				if (str.equals(""))
					continue;
				if (str.length()<=3)
					continue;
				if(!StringUtils.isAlphanumeric(str))
					continue;
			//	if(!str.chars().allMatch(c -> c < 128))
				//	continue;
				str = Normalizer.normalize(str, Normalizer.Form.NFD);
				str = str.replaceAll("[^\\x00-\\x7F]", "");
				
				
				if(listOfMaps.get(state).containsKey(str))
					listOfMaps.get(state).put(str, listOfMaps.get(state).get(str)+1);
				else
					listOfMaps.get(state).put(str,1);
			}
			}
		
		StateMainClusterDTO mainCluster = new StateMainClusterDTO();
		List<StateClusterDTO> clustersList = new LinkedList<StateClusterDTO>();
		for(String state : listOfMaps.keySet()){
			StateClusterDTO cluster = new StateClusterDTO();
			cluster.setName(state);
			
			List<ChildrenDTO> children = new LinkedList<ChildrenDTO>();
			Iterator<String> iter = listOfMaps.get(state).keySet().iterator();
            while (iter.hasNext()) {
                String keyword = iter.next();
                int count = listOfMaps.get(state).get(keyword);
				ChildrenDTO child = new ChildrenDTO();
				child.setName(keyword);
				child.setSize(count);
				children.add(child);
			}
			cluster.setChildren(children);
			
			clustersList.add(cluster);
		}
		
		mainCluster.setName("Cluster");
		mainCluster.setChildren(clustersList);
		return mainCluster;
	}
	

}
