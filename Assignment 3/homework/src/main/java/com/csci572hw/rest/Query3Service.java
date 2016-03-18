package com.csci572hw.rest;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.csci572hw.solr.QuerySolr;

@Path("/api/query3")
public class Query3Service {
		
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
