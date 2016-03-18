__author__ = 'grao'
#!/usr/bin/env python2.7

import tika
import os
import solr
from tika import parser
import json


# create a connection to a solr server
s = solr.SolrConnection('http://localhost:8983/solr/memexcollection')

#Directory where data is dumped from nutch
rootdir='/Users/redshepherd/Desktop/assignment1/nutch/runtime/local/bin/DATA_DUMP'
#set counter
i=151682
flag=False
#recursively read all files in the input directory
for root, subFolders, files in os.walk(rootdir):
    for file in files:
        #set PageRank initial values
        pagerank=0.15

        #Parse all html files
        if file.endswith(".html"):
            print file
            #check if file is already indexed. If yes, then proceed with next file
            response = s.query('id: '+str(i))
            if response.results.numFound != "0":
                print response.results.numFound
                print "Found"
                i=i+1
                continue
                
            #parse file using tika
            parsed = parser.from_file(os.path.join(root, file))

            titleString="N/A"
            contentString="N/A"
            contentLocationString="N/A"
            keywordsString="N/A"

            #Fields for GeoTopic Parser

            geo_name="N/A"
            geo_lat="-9999"
            geo_long="-9999"

            #Fields for cTakes Parser
            ctakes_Date_Annotation="N/A"
            ctakes_Range_Annotation="N/A"

            if parsed:
                #set metadata fields
                flag=True
                if "title" in parsed["metadata"].keys():
                    titleString= parsed["metadata"]["title"]
                if "Content-Location" in parsed["metadata"].keys():
                    contentLocationString= parsed["metadata"]["Content-Location"]
                if "keywords" in parsed["metadata"].keys():
                    keywordsString= parsed["metadata"]["keywords"]

            if parsed["content"]:
                flag=True
                contentString=parsed["content"].encode('ascii', 'ignore')

                #Parsing the geonames, latitude and longitude

                file = open("/Users/redshepherd/Documents/temp.geot",'w+')   # Trying to create a new file or open one
                file.write(contentString)
                file.close()
                contentString=' '.join(contentString.replace('\n','').split())

                try:
                    #send to tika server enabled with geoparser
                    result=os.popen("curl -T /Users/redshepherd/Documents/temp.geot -H 'Content-Disposition: attachment; filename=temp.geot' http://localhost:9988/rmeta").read()
                    parsed_json = json.loads(result)

                    #set the Geo Fields for the document
                    if "Geographic_LATITUDE" in parsed_json[0]:
                        geo_lat = parsed_json[0]["Geographic_LATITUDE"]

                    if "Geographic_LONGITUDE" in parsed_json[0]:
                        geo_long = parsed_json[0]["Geographic_LONGITUDE"]

                    if "Geographic_NAME" in parsed_json[0]:
                        geo_name = parsed_json[0]["Geographic_NAME"]

                except:
                    continue

                #Parsing using cTakes

                #send to tika server with ctakes enabled
                result=os.popen("curl -T /Users/redshepherd/Documents/temp.geot -H 'Content-Disposition: attachment; filename=temp.geot' http://localhost:9987/rmeta").read()
                parsed_json = json.loads(result)
                ctakes_Date_Annotation = parsed_json[0]["ctakes:DateAnnotation"]
                ctakes_Range_Annotation = parsed_json[0]["ctakes:RangeAnnotation"]

            try:
                #send data to solr to index document
                s.add(id=i, title=titleString, ContentLocation=contentLocationString,keywords=keywordsString,
                      content=contentString,Geographical_Name=geo_name,Geographical_Latitude=geo_lat,
                      Geographical_Longitude=geo_long,ctakes_Date_Annotation=ctakes_Date_Annotation,ctakes_Range_Annotation=ctakes_Range_Annotation,page_rank=pagerank)
                s.commit()
            except:
                continue
        
        #do the same for image files.
        elif file.endswith(".png") or file.endswith(".jpg") or file.endswith(".jpeg") or file.endswith(".gif"):
            fileModifiedDateString=""
            resourceNameString=""
            fileNameString=""
            contentTypeString=""
            parsed = parser.from_file(os.path.join(root, file))
            print("File : "+str(file)+"\n")
            if parsed:
                if "File Modified Date" in parsed["metadata"].keys():
                    fileModifiedDateString= parsed["metadata"]["File Modified Date"]
                if "resourceName" in parsed["metadata"].keys():
                    resourceNameString= parsed["metadata"]["resourceName"]

                if "File Name" in parsed["metadata"].keys():
                    fileNameString= parsed["metadata"]["File Name"]

                if "Content-Type" in parsed["metadata"].keys():
                    contentTypeString= parsed["metadata"]["Content-Type"]

            s.add(id=i, fileName=fileNameString, fileModifiedDate=fileModifiedDateString, resourceName=resourceNameString, contentType=contentTypeString,tf_idf=tfidf,page_rank=pagerank)
            s.commit()
        if flag:
            i=i+1
            print i
print i
