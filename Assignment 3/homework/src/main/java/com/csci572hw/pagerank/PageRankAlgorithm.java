package com.csci572hw.pagerank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;



public class PageRankAlgorithm {

	public enum MetadataTypes  {GUN_CATEGORIES, GEOGRAPHIC, TEMPORAL};

	//	private String solrUrl = "http://localhost:8983/solr/memexcollection/select?q=*%3A*&rows=151680&wt=json&indent=true";
	private static final String JSON_FILE_PATH = "D:/Sandeep/CSCI572/Homework/Homework2/json/";

	//values which can override default values for pagerank algorithm 
	private int maxIterations;
	//private double tolerance;
	private double alpha;

	public PageRankAlgorithm(int maxIterations, double tolerance, double alpha) {
		this.maxIterations = maxIterations;
		//this.tolerance = tolerance;
		this.alpha = alpha;
	}

	/**
	 * ================================================================================
	 * ****************Create Directed Graph for  MetaData*****************************
	 * ================================================================================
	 * Based upon below logic : Add a directed edge between Nodes
	 * 1. Get the metadata list and check all metadatas in each document. 
	 * 2. Maintain a hashmap <Integer,List<Node>>. Integer is level and List will contain all nodes with same count.
	 * 3. Sort hashmap in desc order of count. Create a directed edge between nodes between each level to all levels up
	 * 
	 */
	private DirectedSparseGraph<String, Integer> createDirectedGraph (List<Node> nodesForGraph, 
			Map<Integer, List<String>> nodeHierarchyMap) {

		long start = System.currentTimeMillis();
		int edgeCnt = 0;
		DirectedSparseGraph<String, Integer> graph = new DirectedSparseGraph<String, Integer>();

		//Add all Nodes to Graph
		for(Node n : nodesForGraph){
			graph.addVertex(n.getId());
		}

		//Create Directed Edges between the Nodes from nodeHierarchyMap
		List<Integer> mapKeys = new LinkedList<Integer>();
		mapKeys.addAll(nodeHierarchyMap.keySet());
		Collections.sort(mapKeys);
		//		System.out.println(mapKeys);		

		System.out.println(nodeHierarchyMap.size());
		for(int i=0; i < mapKeys.size(); i++) {			
			Integer startKey = mapKeys.get(i);
			List<String> startNodes = nodeHierarchyMap.get(startKey);

			for(int j=i+1; j < mapKeys.size(); j++){
				Integer endKey = mapKeys.get(j);
				List<String> endNodes = nodeHierarchyMap.get(endKey);

				for(String startNode : startNodes) {
					for(String endNode : endNodes) {
						//Create a directed edge between StartNode and EndNode 
						graph.addEdge(edgeCnt++, startNode, endNode, EdgeType.DIRECTED);
					}
				}				
				//break;	//only 1 level
			}
		}

		System.out.println(String.format("Loaded %d nodes and %d links in %d ms", graph.getVertexCount(), graph.getEdgeCount(), (System.currentTimeMillis()-start)));

		return graph;
	}

	/**
	 * Create a map used for creating directed edges between document Nodes
	 * 
	 * @param metaData
	 * @param nodesForGraph
	 * @param metadataType
	 * @return
	 */
	public Map<Integer, List<String>> createHirarchyMapForDirectedGraph (String metaData, 
			List<Node> nodesForGraph, MetadataTypes metadataType) {		
		Map<Integer, List<String>> nodeHierarchyMap = new HashMap<Integer, List<String>>();
		String[] metaDatas = metaData.split(",");

		//For each node, check how many metadata matches
		for(Node node : nodesForGraph){
			int matchCount = 0;			
			for(String meta : metaDatas) {				
				//Depending upon MetaDataType Check in specific attribute of Node
				if(metadataType.equals(MetadataTypes.GUN_CATEGORIES)) {					
					//GUN_CATEGORIES => check (title, keywords, content)
					matchCount += StringUtils.countMatches(node.getTitle().toLowerCase(), meta.trim().toLowerCase());
					matchCount += StringUtils.countMatches(node.getKeywords().toLowerCase(), meta.trim().toLowerCase());
					matchCount += StringUtils.countMatches(node.getContent().toLowerCase(), meta.trim().toLowerCase());					
				} else if(metadataType.equals(MetadataTypes.GEOGRAPHIC)) {
					//Geographic => check (title, availableFrom, content, Geographical_Name_State)
					matchCount += StringUtils.countMatches(node.getTitle().toLowerCase(), meta.trim().toLowerCase());
					matchCount += StringUtils.countMatches(node.getAvailableFrom().toLowerCase(), meta.trim().toLowerCase());
					matchCount += StringUtils.countMatches(node.getContent().toLowerCase(), meta.trim().toLowerCase());
					matchCount += StringUtils.countMatches(node.getGeographical_Name_State().toLowerCase(), meta.trim().toLowerCase());
				} else if(metadataType.equals(MetadataTypes.TEMPORAL)) {
					//Temporal => check in (buyerStartDate, sellerStartDate)
					matchCount += StringUtils.countMatches(node.getBuyerStartDate().toLowerCase(), meta.trim().toLowerCase());
					matchCount += StringUtils.countMatches(node.getSellerStartDate().toLowerCase(), meta.trim().toLowerCase());
				}								
			}

			//Populate nodeHierarchyMap used to create the directed graph structure
			if(nodeHierarchyMap.containsKey(matchCount)) {
				List<String> list = nodeHierarchyMap.get(matchCount);
				list.add(node.getId());
				nodeHierarchyMap.put(matchCount, list);
			}else {
				List<String> list = new ArrayList<String>();
				list.add(node.getId());
				nodeHierarchyMap.put(matchCount, list);
			}						
		}//end loop	

		//System.out.println(nodeHierarchyMap);
		return nodeHierarchyMap;	
	}

	/**
	 * Using Jung Page rank library to compute page rank. We create a directed graph 
	 * which is input to the page rank algorithm. The tolerance is 0.001 by default
	 * and page rank values tend to converge after 10 iterations, so using 10 iterations
	 * 
	 * @param graph
	 * @return
	 */
	public Map<String, Double> compute(DirectedSparseGraph<String, Integer> graph) {

		long start = System.currentTimeMillis() ;
		PageRank<String, Integer> ranker = new PageRank<String, Integer>(graph, alpha);
		//ranker.setTolerance(this.tolerance) ;
		ranker.setMaxIterations(this.maxIterations);

		ranker.evaluate();

		System.out.println ("Tolerance = " + ranker.getTolerance() );
		System.out.println ("Dump factor = " + (1.00d - ranker.getAlpha() ) ) ;
		System.out.println ("Max iterations = " + ranker.getMaxIterations() ) ;
		System.out.println ("PageRank computed in " + (System.currentTimeMillis()-start) + " ms"); 

		Map<String, Double> result = new HashMap<String, Double>();
		for (String n : graph.getVertices()) {
			result.put(n, ranker.getVertexScore(n));
		}
		return result;
	}


	/**
	 * ==============================================================================================
	 * ***********************************	UTILITY METHODS	****************************************
	 * ==============================================================================================
	 */

	/**
	 * Load the documents from Solr.
	 * Save the Solr Json reponse to local file and read data from file
	 */
	public List<Node> loadDocumentsFromSolr(String filePath) {
		File file = new File(filePath);	
		List<Node> nodesForGraph = new ArrayList<Node>();

		//create ObjectMapper instance
		ObjectMapper mapper = new ObjectMapper();        
		try {
			JsonNode rootNode = mapper.readTree(file);			 
			JsonNode responseNode = rootNode.path("response");

			JsonNode documents = responseNode.path("docs");
			Iterator<JsonNode> elements = documents.elements();
			int i=0;

			while(elements.hasNext()){
				JsonNode document = elements.next();
				//System.out.println(document.has("content1"));
				//System.out.println(document.path("_version_").asText());		     
				/**
				 * Map json document to Node object and add Node to nodesForGraph list
				 */
				Node node = mapJSONDocumentToNode(document);			     
				//System.out.println(node);
				nodesForGraph.add(node);
				i++;
			}
			System.out.println(i);

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		return nodesForGraph;
	}

	/**
	 * Map JSON document to Node object. Convert values to appropriate datatype and these
	 * Nodes will be used later for graph creation and computing page rank.
	 * @param document
	 * @return
	 */
	private Node mapJSONDocumentToNode(JsonNode document){
		Node node = new Node();

		node.setId(document.path("id").asText());
		node.setSiteName(document.path("siteName").asText());
		node.setImages(document.path("images").asText());
		node.setKeywords(document.path("keywords").asText());
		node.setTitle(document.path("title").asText());
		node.setContent(document.path("content").asText());
		node.setPrice(document.path("price").asDouble());
		node.setCategory(document.path("category").asText());

		//dummy value is -9999.0
		node.setGeographical_Latitude(document.path("Geographical_Latitude").asDouble());
		//dummy value is -9999.0
		node.setGeographical_Latitude(document.path("Geographical_Longitude").asDouble());
		node.setGeographical_Name_State(document.path("Geographical_Name_State").asText());
		node.setGeographical_Name(document.path("Geographical_Name").asText());
		node.setAvailableFrom(document.path("availableFrom").asText());

		node.setSellerContentDescription(document.path("sellerContentDescription").asText());
		node.setSellerContactName(document.path("sellerContactName").asText());
		node.setSellerTelephoneNumber(document.path("sellerTelephoneNumber").asText());
		node.setSellerUrl(document.path("sellerUrl").asText());
		node.setSellerStartDate(document.path("sellerStartDate").asText());
		node.setSellerType(document.path("sellerType").asText());

		node.setBuyerDescription(document.path("buyerDescription").asText());
		node.setBuyerStartDate(document.path("buyerStartDate").asText());
		node.setBuyerType(document.path("buyerType").asText());
		node.setBuyerOrgName(document.path("buyerOrgName").asText());

		node.setPage_rank(document.path("page_rank").asDouble());
		node.setVersion(document.path("_version_").asText());

		return node;
	}

	private static Double getMaxPageRank(Double categoryPG, Double geographicPG, Double temporalPG){
		Double finalPG = categoryPG;
		if(geographicPG > finalPG){
			finalPG = geographicPG;
		}else if(temporalPG > finalPG){
			finalPG = temporalPG;
		}
		return finalPG;
	}
	
	/**
	 * Created 3 graphs for each meta-data features : We are creating for Gun graphs for Gun categories, Geographic location, Temporal values
	 * We create 3 graphs and then compute PageRank for 3 directed graphs
	 * Then get the final pagerank which is updated in Solr
	 *  
	 * @param filePath
	 * @return
	 */
	public Map<String,Double> getFinalPageRankMap(String filePath){
		//Load Solr JSON documents from and create List of Nodes for Graph
		List<Node> nodesForGraph = loadDocumentsFromSolr(filePath);

		//Node List is populated now. So create directed graph based on meta-data features extracted by NER
		//We are using 3 meta-data below : Categories, Geographic and Temporal features
		
		String metaCategories = "Handguns,Pistols,Semi-automatic,Machine,Revolvers,Revolver,Derringers,"
				+ "Rifles,Rifle,Shotguns,Shotgun,Long,Shoulder,Guns,Gun,Cannons,Tactical,Luger,Double,Single,"
				+ "Action,ACP,Bolt,Striker,Fire,6.8mm,9mm,45mm,Gauge,Magnum,Colt,Pump";

		String metaGeographic = "Alabama, Alaska, Arizona, Arkansas, California, Colorado, Connecticut, Florida, Georgia, "
				+ "Hawaii, Idaho, Illinois, Indiana, Iowa, Kansas, Kentucky, Louisiana, Maine, Massachusetts, Michigan, "
				+ "Minnesota, Mississippi, Missouri, Montana, Nebraska, Nevada, New Hampshire, New Jersey, New Mexico, "
				+ "New York, North Carolina, North Dakota, Ohio, Oklahoma, Oregon, Pennsylvania, Rhode Island, "
				+ "South Carolina, South Dakota, Tennessee, Texas, Utah, Vermont, Virginia, Washington, Washington, "
				+ "D.C., Wisconsin";	

		String metaTemporal = "January, February, March, April, May, June, July, August, September, October, "
				+ "November, December, Jan, Feb, Mar, Apr, May, June, July, Aug, Sept, Oct, Nov, Dec,"
				+ "01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,"
				+ " 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015";

		//	1. Gun Categories
		//Create node Hierarchy Map used to create directed graph
		Map<Integer, List<String>> nodeHierarchyMapCategory = createHirarchyMapForDirectedGraph(metaCategories, nodesForGraph, MetadataTypes.GUN_CATEGORIES);
		//System.out.println(nodeHierarchyMapCategory);
		//Create Directed Graph
		DirectedSparseGraph<String, Integer> categoriesGraph = createDirectedGraph(nodesForGraph, nodeHierarchyMapCategory);		
		//Compute Page Rank
		Map<String, Double> pageRanksCategories = compute(categoriesGraph);
		categoriesGraph = null; System.gc();
		//System.out.println("Categories PG => " + pageRanksCategories);


		// 	2. Geographic
		//Create node Hierarchy Map used to create directed graph
		Map<Integer, List<String>> nodeHierarchyMapGeographic = createHirarchyMapForDirectedGraph(metaGeographic, nodesForGraph, MetadataTypes.GEOGRAPHIC);
		//System.out.println(nodeHierarchyMapGeographic);
		//Create Directed Graph
		DirectedSparseGraph<String, Integer> geographicGraph = createDirectedGraph(nodesForGraph, nodeHierarchyMapGeographic);		
		//Compute Page Rank
		Map<String, Double> pageRanksGeographic = compute(geographicGraph);
		geographicGraph = null; System.gc();
		//System.out.println("Geographic PG => " + pageRanksGeographic);


		//	3. Temporal
		//Create node Hierarchy Map used to create directed graph
		Map<Integer, List<String>> nodeHierarchyMapTemporal = createHirarchyMapForDirectedGraph(metaTemporal, nodesForGraph, MetadataTypes.TEMPORAL);
		//System.out.println(nodeHierarchyMapTemporal);
		//Create Directed Graph
		DirectedSparseGraph<String, Integer> temporalGraph = createDirectedGraph(nodesForGraph, nodeHierarchyMapTemporal);		
		//Compute Page Rank
		Map<String, Double> pageRanksTemporal = compute(temporalGraph);
		temporalGraph = null;  System.gc();
		//System.out.println("Temporal PG => " + pageRanksTemporal);

		//Loop all nodes in Graph and compute final page rank
		Map<String, Double> finalPageRank = new HashMap<String, Double>();
		for(Node n : nodesForGraph) {
			Double categoryPG = pageRanksCategories.get(n.getId());
			Double geographicPG = pageRanksGeographic.get(n.getId());
			Double temporalPG = pageRanksTemporal.get(n.getId());

			Double finalPG = getMaxPageRank(categoryPG, geographicPG, temporalPG);
			//System.out.println(categoryPG + " , " + geographicPG + " , " + temporalPG + " => " + finalPG);
			finalPageRank.put(n.getId(), finalPG);			
		}
		//System.out.println("Final PG : " + finalPageRank);
		return finalPageRank;
	}
	
	/**
	 * Used in RESTful service to show the distribution of documents as per page rank.
	 * Using Circle packing D3 visualization to visualize below Page Rank distribution
	 * 
	 * @param finalPageRank
	 * @return
	 */
	public Map<Double, List<String>> getFinalPageRankDistribution(Map<String,Double> finalPageRank){
		Map<Double, List<String>> pageRankCheck = new TreeMap<Double, List<String>>();
		
		//Loop through finalPageRank map and group documents with same page rank together
		for(Map.Entry<String, Double> entry : finalPageRank.entrySet()){
			Double finalPG = entry.getValue();			
			
			//checking docs with same page rank
			if(pageRankCheck.containsKey(finalPG)) {
				List<String> docs = pageRankCheck.get(finalPG);
				docs.add(entry.getKey());
				pageRankCheck.put(finalPG, docs);
			} else {
				List<String> docs = new ArrayList<String>();
				docs.add(entry.getKey());
				pageRankCheck.put(finalPG, docs);
			}
		}
		return pageRankCheck;
	}
	

	/**
	 * Main function to drive the Page Rank Algorithm as stand-alone java program
	 * We also make similar call to get computed page rank from the RESTful service.	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PageRankAlgorithm pageRankAlgo = new PageRankAlgorithm(10, 0.1, 0.85);

		
		String[] files = {"workfile1.json", "workfile5001.json", "workfile10001.json",
				"workfile15001.json","workfile20001.json","workfile25001.json","workfile30001.json",
				"workfile35001.json","workfile40001.json","workfile45001.json","workfile50001.json",
				"workfile55001.json","workfile60001.json","workfile65001.json","workfile70001.json",
				"workfile75001.json","workfile80001.json","workfile85001.json","workfile90001.json",
				"workfile95001.json","workfile100001.json","workfile105001.json","workfile110001.json",
				"workfile115001.json","workfile120001.json","workfile125001.json","workfile130001.json",
				"workfile135001.json","workfile140001.json","workfile145001.json","workfile150001.json"
				};
		
		for(String file : files) {
			Map<String, Double> finalPageRank = pageRankAlgo.getFinalPageRankMap(JSON_FILE_PATH + file);
			
			Map<Double, List<String>> pageRankCheck = pageRankAlgo.getFinalPageRankDistribution(finalPageRank);
			
			System.out.println("finalPageRank : "+ finalPageRank);
			System.out.println("pageRankCheck : " + pageRankCheck);
			
			//Update Solr with PageRank			
			//System.out.println("###### Updating PG for file : " + file);
			updateSolrDocumentsPageRank(finalPageRank);			
		}

		System.out.println("------------- END --------------");
	}


	/**
	 * Update the Page Rank for all the documents in Solr. Using Solrj to update 
	 * page rank to Solr by making a Rest Post request.
	 * 
	 * @param finalPageRank
	 */
	private static void updateSolrDocumentsPageRank(Map<String, Double> finalPageRank) {
		String solrUrl = "http://10.120.116.62:8983/solr/memexcollection";
		SolrServer server = new HttpSolrServer(solrUrl);
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

		//Loop PageRank Map
		for (Map.Entry<String, Double> entry : finalPageRank.entrySet()) {
			Map<String,Object> fieldModifier = new HashMap<String,Object>(1);
			fieldModifier.put("set", entry.getValue());
			SolrInputDocument doc1 = new SolrInputDocument();
			doc1.addField( "id", entry.getKey());
			doc1.addField("page_rank", fieldModifier);  // add the map as the field value
			docs.add(doc1);
		}

		try {
			System.out.println("Updating page rank to Solr");
			server.add( docs );
			server.commit();
			System.out.println("Success");
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
