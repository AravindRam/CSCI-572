package com.csci572hw.rest;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;

import com.csci572hw.solr.QuerySolr;

@Path("/api/query2")
public class Query2Service {
	
	@GET
	@Path("/getBubbleMenu")
	@Produces(MediaType.APPLICATION_JSON)
	public String getBubbleMenu(){
		return "bubblemenu";
	}

	@GET
	@Path("/getChordDiagram")
	@Produces(MediaType.APPLICATION_JSON)
	public String getChordDiagram(){
		return "ChordDiagram";
	}
	

	@GET
	@Path("/getAreaChart")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAreaChart(){
		return "AreaChart";
	}
	
	
	@GET
	@Path("/getBubbleChart")
	@Produces(MediaType.APPLICATION_JSON)
	public String getBubbleChart(){
		return "BubbleChart";
	}
	
	
	@GET
	@Path("/getTimeSeries")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTimeSeries(){
		return "TimeSeries";
	}
	
	
	@GET
	@Path("/getDendogram")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDendogram(){
			int flag1 = 0;
			int flag2 = 0;
			QuerySolr querySolr=new QuerySolr("localhost",8983,"memexcollection");
			SolrDocumentList docs = querySolr.getQueryFromSolr("content%3A+%2F.*shotgun.*%2F+AND+(ctakes_Date_Annotation%3A+%2F2015-01.*%2F+sellerStartDate%3A+%2F2010-01.*%2F+buyerStartDate%3A+%2F2010-01.*%2F)&rows=100000&wt=json&indent=true",50);
			//JSONArray ja = new JSONArray();
			JSONObject jo = new JSONObject();
			jo.put("name", "Weapons Data");
			for(int i=0; i< docs.size(); i++)
			{
				SolrDocument doc = docs.get(i);
				
				if(!doc.getFieldValue("images").equals("N/A") && !doc.getFieldValue("Geographical_Name_State").equals("N/A") && !doc.getFieldValue("category").equals("N/A"))
				{
					if(jo.has("children"))
					{
						org.json.JSONArray tempJSONArraylevel1 = jo.getJSONArray("children");
						for(int j=0;j<tempJSONArraylevel1.length();j++)
						{
							JSONObject jsonObjectlevel1 = tempJSONArraylevel1.getJSONObject(j);
							if(jsonObjectlevel1.getString("name").equalsIgnoreCase(doc.getFieldValue("Geographical_Name_State").toString()))
							{
								flag1 = 1;
								org.json.JSONArray tempJSONArrayLevel2 = jsonObjectlevel1.getJSONArray("children");
								for(int k=0;k<tempJSONArrayLevel2.length();k++)
								{
									JSONObject jsonObjectlevel2 = tempJSONArrayLevel2.getJSONObject(k);
									if(jsonObjectlevel2.getString("name").equalsIgnoreCase(doc.getFieldValue("category").toString()))
									{
										flag2 = 1;
										org.json.JSONArray tempJSONArrayLevel3 = jsonObjectlevel2.getJSONArray("children");
										String tempString = doc.getFieldValue("images").toString();
										String[] tempStringArray = tempString.split(",");
										for(int l=0;l<tempStringArray.length;l++)
										{
											JSONObject UrlJsonObject= new JSONObject();
											UrlJsonObject.put("name", tempStringArray[l]);
											UrlJsonObject.put("size", 743);
											tempJSONArrayLevel3.put(UrlJsonObject);
										}
										
									}
									
								}
								if(flag2 == 0)
								{									
									JSONObject jsonObjectlevel3 = new JSONObject();
									jsonObjectlevel3.put("name", doc.getFieldValue("category").toString());
									
									org.json.JSONArray tempJSONArrayLevel4 = new org.json.JSONArray();
									
									String tempString = doc.getFieldValue("images").toString();
									String[] tempStringArray = tempString.split(",");
									for(int l=0;l<tempStringArray.length;l++)
									{
										JSONObject UrlJsonObject= new JSONObject();
										UrlJsonObject.put("name", tempStringArray[l]);
										UrlJsonObject.put("size", 743);
										tempJSONArrayLevel4.put(UrlJsonObject);
									}
									
									jsonObjectlevel3.put("children",tempJSONArrayLevel4);
									tempJSONArrayLevel2.put(jsonObjectlevel3);
									
									flag2 = 0;
								}
								else
								{
									flag2 = 0;
								}
							}
							
						}
						if(flag1 == 0)
						{
							JSONObject JsonObject1 = new JSONObject();
							JsonObject1.put("name", doc.getFieldValue("Geographical_Name_State").toString());
							org.json.JSONArray tempJSONArraylevel2 = new org.json.JSONArray();
							
							tempJSONArraylevel2.put(new JSONObject());
							JSONObject jsonObjectlevel3 = tempJSONArraylevel2.getJSONObject(0);
							jsonObjectlevel3.put("name", doc.getFieldValue("category").toString());
							org.json.JSONArray tempJSONArrayLevel4 = new org.json.JSONArray();
							
							
							String tempString = doc.getFieldValue("images").toString();
							String[] tempStringArray = tempString.split(",");
							for(int l=0;l<tempStringArray.length;l++)
							{
								JSONObject UrlJsonObject= new JSONObject();
								UrlJsonObject.put("name", tempStringArray[l]);
								UrlJsonObject.put("size", 743);
								tempJSONArrayLevel4.put(UrlJsonObject);
							}
							
							
							jsonObjectlevel3.put("children", tempJSONArrayLevel4);
							JsonObject1.put("children", tempJSONArraylevel2);
							tempJSONArraylevel1.put(JsonObject1);
							
							flag1 = 0;
							
						}
						else
						{
							flag1 = 0;
						}
						
					}
					else
					{
						org.json.JSONArray tempJSONArraylevel1 = new org.json.JSONArray();
						tempJSONArraylevel1.put(new JSONObject());
						JSONObject jsonObjectlevel2 = tempJSONArraylevel1.getJSONObject(0);
						jsonObjectlevel2.put("name", doc.getFieldValue("Geographical_Name_State").toString());
						
						
						org.json.JSONArray tempJSONArraylevel2 = new org.json.JSONArray();
						tempJSONArraylevel2.put(new JSONObject());
						
						JSONObject jsonObjectlevel3 = tempJSONArraylevel2.getJSONObject(0);
						jsonObjectlevel3.put("name", doc.getFieldValue("category").toString());
						
						org.json.JSONArray tempJSONArrayLevel4 = new org.json.JSONArray();
						
						String tempString = doc.getFieldValue("images").toString();
						String[] tempStringArray = tempString.split(",");
						for(int l=0;l<tempStringArray.length;l++)
						{
							JSONObject UrlJsonObject= new JSONObject();
							UrlJsonObject.put("name", tempStringArray[l]);
							UrlJsonObject.put("size", 743);
							tempJSONArrayLevel4.put(UrlJsonObject);
						}
					
						jsonObjectlevel3.put("children", tempJSONArrayLevel4);
						jsonObjectlevel2.put("children", tempJSONArraylevel2);
						jo.put("children", tempJSONArraylevel1);
					}
					
				}
			}
			
			return jo.toString();
	}
	

}
