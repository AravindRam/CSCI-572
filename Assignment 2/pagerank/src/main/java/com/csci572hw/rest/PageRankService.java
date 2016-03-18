package com.csci572hw.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.csci572hw.model.ChildrenDTO;
import com.csci572hw.model.ClusterDTO;
import com.csci572hw.model.GraphDTO;
import com.csci572hw.model.MainClusterDTO;
import com.csci572hw.pagerank.Node;
import com.csci572hw.pagerank.PageRankAlgorithm;
import com.csci572hw.pagerank.PageRankAlgorithm.MetadataTypes;

@Path("/pagerank")
public class PageRankService {

	private static final String JSON_FILE_PATH = "D:/Sandeep/CSCI572/Homework/Homework2/json/memex100.json";

	/**
	 * Restful service for PageRankDistribution. Here we show which documents have same
	 * page rank and cluster them using circle packing 
	 * 
	 * @return
	 */
	@GET
	@Path("/getDistribution")
	@Produces(MediaType.APPLICATION_JSON)
	public MainClusterDTO getPageRankDistribution() {
		
		MainClusterDTO mainCluster = new MainClusterDTO();
		PageRankAlgorithm pageRankAlgo = new PageRankAlgorithm(10, 0.1, 0.85);
		List<Node> nodesForGraph = pageRankAlgo.loadDocumentsFromSolr(JSON_FILE_PATH);
		
		Map<String, Node> nodesMap = new HashMap<String, Node>();
		//Populate nodesMap
		for(Node n : nodesForGraph){
			nodesMap.put(n.getId(), n);
		}
		
		Map<String, Double> finalPageRank = pageRankAlgo.getFinalPageRankMap(JSON_FILE_PATH);		
		Map<Double, List<String>> pageRankCheck = pageRankAlgo.getFinalPageRankDistribution(finalPageRank);

		System.out.println("finalPageRank : "+ finalPageRank);
		System.out.println("pageRankCheck : " + pageRankCheck);

		//Populate DTO's as required by D3
		List<ClusterDTO> clustersList = new LinkedList<ClusterDTO>();
		for(Map.Entry<Double, List<String>> entry : pageRankCheck.entrySet()){
			ClusterDTO cluster = new ClusterDTO();
			cluster.setName(entry.getKey());
			
			List<ChildrenDTO> children = new LinkedList<ChildrenDTO>();
			for(String docId : entry.getValue()){
				ChildrenDTO child = new ChildrenDTO();
				child.setSize(1);
				
				Node n = nodesMap.get(docId);
				String temp = "Document ID: "+docId+"\nTitle: "+n.getTitle()+
						"\nState: "+n.getGeographical_Name_State()+"\nCategory: "+n.getCategory();
				child.setName(temp);
				children.add(child);
			}
			cluster.setChildren(children);
			
			clustersList.add(cluster);
		}
		
		mainCluster.setName("Cluster");
		mainCluster.setChildren(clustersList);
		return mainCluster;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	@GET
	@Path("/getGraph")
	@Produces(MediaType.APPLICATION_JSON)
	public List<GraphDTO> getPageRankGraph() {

		List<GraphDTO> graphs = new LinkedList<GraphDTO>();
		PageRankAlgorithm pageRankAlgo = new PageRankAlgorithm(10, 0.1, 0.85);	

		List<Node> nodesForGraph = pageRankAlgo.loadDocumentsFromSolr(JSON_FILE_PATH);

		//Node List is populated now. So create directed graph based on meta-data features extracted by NER
		String metaCategories = "Handguns,Pistols,Semi-automatic,Machine,Revolvers,Revolver,Derringers,"
				+ "Rifles,Rifle,Shotguns,Shotgun,Long,Shoulder,Guns,Gun,Cannons,Tactical,Luger,Double,Single,"
				+ "Action,ACP,Bolt,Striker,Fire,6.8mm,9mm,45mm,Gauge,Magnum,Colt,Pump";

		//1. Gun Categories
		//Create node Hierarchy Map used to create directed graph
		Map<Integer, List<String>> nodeHierarchyMap = pageRankAlgo.createHirarchyMapForDirectedGraph(metaCategories, nodesForGraph, MetadataTypes.GUN_CATEGORIES);

		//Create Directed Edges between the Nodes from nodeHierarchyMap
		List<Integer> mapKeys = new LinkedList<Integer>();
		mapKeys.addAll(nodeHierarchyMap.keySet());
		Collections.sort(mapKeys);	

		System.out.println(nodeHierarchyMap.size());
		for(int i=0; i < mapKeys.size(); i++) {			
			Integer startKey = mapKeys.get(i);
			List<String> startNodes = nodeHierarchyMap.get(startKey);

			for(int j=i+1; j < mapKeys.size(); j++) {
				Integer endKey = mapKeys.get(j);
				List<String> endNodes = nodeHierarchyMap.get(endKey);

				for(String startNode : startNodes) {
					for(String endNode : endNodes){
						//Create a directed edge between StartNode and EndNode 
						GraphDTO graph = new GraphDTO(startNode, endNode, "suit");
						graphs.add(graph);
					}
				}				
			}
		}
		return graphs;
	}



}
