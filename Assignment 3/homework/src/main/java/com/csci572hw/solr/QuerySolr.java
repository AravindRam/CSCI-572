package com.csci572hw.solr;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.google.gson.Gson;


public class QuerySolr {
	HttpSolrServer server;
	public QuerySolr(String host, int port,String collection){
		server= new HttpSolrServer("http://"+host+":"+port+"/solr/"+collection+"/");
	    server.setParser(new XMLResponseParser());
	}


public SolrDocumentList getQueryFromSolr(String query,int rows){
      // specify the get request
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		solrQuery.setRows(rows);
      
		solrQuery.setHighlight(true);

	    solrQuery.setHighlightRequireFieldMatch(true);
	    QueryResponse response = null;
		try {
			response = server.query(solrQuery);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    SolrDocumentList docs = response.getResults();
	    return docs;
   }

public static void main(String args[]) throws SolrServerException{
	QuerySolr querySolr=new QuerySolr("localhost",8983,"memexcollection");
	SolrDocumentList response=querySolr.getQueryFromSolr("content%3A+%2F.*shotgun.*%2F+AND+(ctakes_Date_Annotation%3A+%2F2015-01.*%2F+sellerStartDate%3A+%2F2015-01.*%2F+buyerStartDate%3A+%2F2015-01.*%2F)&wt=json&indent=true&rows=100000",100);
	System.out.println(response);
}
}

  

