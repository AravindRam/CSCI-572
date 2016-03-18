from urllib2 import *
import simplejson
import json

keywords=[]
solrCallQuery=[]
ip_address = "10.120.115.67"

#Create a list of keywords for each query
#1.1
keywords.append("content: /.*shotgun.*/ AND (ctakes_Date_Annotation: /2015-01.*/ sellerStartDate: /2015-01.*/ buyerStartDate: /2015-01.*/)")
keywords.append("content: /.*handgun.*/ AND (ctakes_Date_Annotation: /2015-03.*/ sellerStartDate: /2015-03.*/ buyerStartDate: /2015-03.*/)")
keywords.append("content: /.*rifle.*/ AND (ctakes_Date_Annotation: /2013-06.*/ sellerStartDate: /2013-06.*/ buyerStartDate: /2013-06.*/)")
keywords.append("content: /.pistol.*/ AND (ctakes_Date_Annotation: /2012-01.*/ sellerStartDate: /2012-01.*/ buyerStartDate: /2012-01.*/)")
keywords.append("content: /.*handgun.*/ AND (ctakes_Date_Annotation: /2014.*/ sellerStartDate: /2014.*/ buyerStartDate: /2014.*/)")

#1.2
keywords.append("Geographical_Name_State: /Florida/ AND buyerStartDate: /2013-01.*/")
keywords.append("Geographical_Name_State: /Texas/ AND buyerStartDate: /2015-01.*/")
keywords.append("Geographical_Name_State: /California/ AND buyerStartDate: /2014-03.*/")
keywords.append("Geographical_Name_State: /Georgia/ AND buyerStartDate: /2013.*/")
keywords.append("Geographical_Name_State: /Arizona/ AND buyerStartDate: /2013-01.*/")

#1.3
keywords.append("assault AND rifle AND Geographical_Name_State: California")

#2.1
keywords.append("content: /.*shotgun.*/ AND Geographical_Name_State: /Florida/ AND (ctakes_Date_Annotation: /2015-01.*/ sellerStartDate: /2015-01.*/ buyerStartDate: /2015-01.*/) AND NOT(images: N/A)")
keywords.append("content: /.*handgun.*/ AND Geographical_Name_State: /California/ AND (ctakes_Date_Annotation: /2013-03.*/ sellerStartDate: /2013-03.*/ buyerStartDate: /2013-03.*/) AND NOT(images: N/A)")
keywords.append("content: /.*rifle.*/ AND Geographical_Name_State: /Georgia/ AND (ctakes_Date_Annotation: /2014-01.*/ sellerStartDate: /2014-01.*/ buyerStartDate: /2014-01.*/) AND NOT(images: N/A)")
keywords.append("content: /.*pistol.*/ AND Geographical_Name_State: /Ohio/ AND (ctakes_Date_Annotation: /2014.*/ sellerStartDate: /2014.*/ buyerStartDate: /2014.*/) AND NOT(images: N/A)")
keywords.append("content: /.*guns.*/ AND Geographical_Name_State: /Arizona/ AND (ctakes_Date_Annotation: /2012.*/ sellerStartDate: /2012.*/ buyerStartDate: /2012.*/) AND NOT(images: N/A)")

#2.2
keywords.append("category: /.*Handgun.*/ AND Geographical_Name_State: /Florida/ AND NOT(images: N/A) AND content: cash")

#3
keywords.append("category: /.*Handgun.*/ AND NOT(images: N/A) AND content: cash AND (ctakes_Date_Annotation: /2015-03.*/ sellerStartDate: /2015-03.*/ buyerStartDate: /2015-03.*/)")

#4
keywords.append("serialNumber: t345643 AND NOT(images: N/A)")

#5
keywords.append("missiles AND NOT(images: N/A)")

#Create a list of solr query calls for the corresponding keywords
#1.1


solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=content%3A+%2F.*shotgun.*%2F+AND+(ctakes_Date_Annotation%3A+%2F2015-01.*%2F+sellerStartDate%3A+%2F2015-01.*%2F+buyerStartDate%3A+%2F2015-01.*%2F)%0A&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=content%3A+%2F.*handgun.*%2F+AND+(ctakes_Date_Annotation%3A+%2F2015-03.*%2F+sellerStartDate%3A+%2F2015-03.*%2F+buyerStartDate%3A+%2F2015-03.*%2F)&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=content%3A+%2F.*rifle.*%2F+AND+(ctakes_Date_Annotation%3A+%2F2013-06.*%2F+sellerStartDate%3A+%2F2013-06.*%2F+buyerStartDate%3A+%2F2013-06.*%2F)&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=content%3A+%2F.pistol.*%2F+AND+(ctakes_Date_Annotation%3A+%2F2012-01.*%2F+sellerStartDate%3A+%2F2012-01.*%2F+buyerStartDate%3A+%2F2012-01.*%2F)&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=content%3A+%2F.*handgun.*%2F+AND+(ctakes_Date_Annotation%3A+%2F2014.*%2F+sellerStartDate%3A+%2F2014.*%2F+buyerStartDate%3A+%2F2014.*%2F)&sort=page_rank+desc&wt=json&indent=true")

#1.2
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=Geographical_Name_State%3A+%2FFlorida%2F+AND+buyerStartDate%3A+%2F2013-01.*%2F&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=Geographical_Name_State%3A+%2FTexas%2F+AND+buyerStartDate%3A+%2F2015-01.*%2F&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=Geographical_Name_State%3A+%2FCalifornia%2F+AND+buyerStartDate%3A+%2F2014-03.*%2F&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=Geographical_Name_State%3A+%2FGeorgia%2F+AND+buyerStartDate%3A+%2F2013.*%2F&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=Geographical_Name_State%3A+%2FArizona%2F+AND+buyerStartDate%3A+%2F2013-01.*%2F&sort=page_rank+desc&wt=json&indent=true")

#1.3
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=assault+AND+rifle+AND+Geographical_Name_State%3A+California&sort=page_rank+desc&wt=json&indent=true")

#2.1
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=content%3A+%2F.*shotgun.*%2F+AND+Geographical_Name_State%3A+%2FFlorida%2F+AND+(ctakes_Date_Annotation%3A+%2F2015-01.*%2F+sellerStartDate%3A+%2F2015-01.*%2F+buyerStartDate%3A+%2F2015-01.*%2F)+AND+NOT(images%3A+N%2FA)%0A&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=content%3A+%2F.*handgun.*%2F+AND+Geographical_Name_State%3A+%2FCalifornia%2F+AND+(ctakes_Date_Annotation%3A+%2F2013-03.*%2F+sellerStartDate%3A+%2F2013-03.*%2F+buyerStartDate%3A+%2F2013-03.*%2F)+AND+NOT(images%3A+N%2FA)&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=content%3A+%2F.*rifle.*%2F+AND+Geographical_Name_State%3A+%2FGeorgia%2F+AND+(ctakes_Date_Annotation%3A+%2F2014-01.*%2F+sellerStartDate%3A+%2F2014-01.*%2F+buyerStartDate%3A+%2F2014-01.*%2F)+AND+NOT(images%3A+N%2FA)&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=content%3A+%2F.*pistol.*%2F+AND+Geographical_Name_State%3A+%2FOhio%2F+AND+(ctakes_Date_Annotation%3A+%2F2014.*%2F+sellerStartDate%3A+%2F2014.*%2F+buyerStartDate%3A+%2F2014.*%2F)+AND+NOT(images%3A+N%2FA)&sort=page_rank+desc&wt=json&indent=true")
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=content%3A+%2F.*guns.*%2F+AND+Geographical_Name_State%3A+%2FArizona%2F+AND+(ctakes_Date_Annotation%3A+%2F2012.*%2F+sellerStartDate%3A+%2F2012.*%2F+buyerStartDate%3A+%2F2012.*%2F)+AND+NOT(images%3A+N%2FA)&sort=page_rank+desc&wt=json&indent=true")

#2.2
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=category%3A+%2F.*Handgun.*%2F+AND+Geographical_Name_State%3A+%2FFlorida%2F+AND+NOT(images%3A+N%2FA)+AND+content%3A+cash&sort=page_rank+desc&rows=14&wt=json&indent=true")

#3
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=category%3A+%2F.*Handgun.*%2F+AND+NOT(images%3A+N%2FA)+AND+content%3A+cash+AND+(ctakes_Date_Annotation%3A+%2F2015-03.*%2F+sellerStartDate%3A+%2F2015-03.*%2F+buyerStartDate%3A+%2F2015-03.*%2F)&sort=page_rank+desc&wt=json&indent=true")

#4
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=serialNumber%3A+t345643+AND+NOT(images%3A+N%2FA)&wt=json&indent=true")

#5
solrCallQuery.append("http://"+ip_address+":8983/solr/memexcollection/select?q=missiles+AND+NOT(images%3A+N%2FA)&sort=page_rank+desc&wt=json&indent=true")

#Retrieve the relevant fields for each query
for i in range(len(keywords)):
    print "\nQuery - "+str(i+1)+" -> "+keywords[i]
    connection = urlopen(solrCallQuery[i])              #Make a REST call to solr and retrieve the relevant documents in JSON format
    rsp = simplejson.load(connection)                   #Parse the JSON file to retrieve the required fields
    print "\nNumber of Documents Found: "+str(rsp["response"]["numFound"])  
    if(rsp["response"]["numFound"] > 10):
        print "\nResults of top 10 documents: "         #Display the top 10 documents of the result
        print "---------------------------"
    elif(rsp["response"]["numFound"] > 0):
        print "\nResults of "+str(rsp["response"]["numFound"])+" document(s):"
        print "------------------------"
    for j in range(len(rsp["response"]["docs"])):
        print "\nDocument - "+str(j+1)+" details:"
        print "--------------------"
        print "Document Id : "+str(rsp["response"]["docs"][j]["id"])
        print "Type of Weapon : "+str(rsp["response"]["docs"][j]["category"])
        print "State : "+str(rsp["response"]["docs"][j]["Geographical_Name_State"])
        print "City : "+str(rsp["response"]["docs"][j]["Geographical_Name"])
        if(rsp["response"]["docs"][j]["sellerStartDate"]!="N/A"):
            print "Date : "+str(rsp["response"]["docs"][j]["sellerStartDate"].split("T")[0])
        elif(rsp["response"]["docs"][j]["buyerStartDate"]!="N/A"):
            print "Date : "+str(rsp["response"]["docs"][j]["buyerStartDate"].split("T")[0])
        print "Title : "+str(rsp["response"]["docs"][j]["title"])
        print "Keywords : "+str(rsp["response"]["docs"][j]["keywords"])
        print "Content : "+str(rsp["response"]["docs"][j]["content"])
        imageUrls = rsp["response"]["docs"][j]["images"].split(",")
        if(len(imageUrls)>1):
            print "Image URLs : "+str(len(imageUrls))
            for k in range(len(imageUrls)):
                print "\t\t\t"+str(imageUrls[k])
        else:
            print "Image URLs : N/A"
