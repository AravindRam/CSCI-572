from urllib2 import *
import simplejson
import json

'''
Program to dump all the solr documents to json files for use with the PageRank code
'''
for i in range(1,455000,5000):
    #get 5000 documents
    conn = urlopen('http://10.120.115.67:8983/solr/memexcollection/select?q=*%3A*&rows=5000&start='+str(i)+'&wt=json&indent=true')
    rsp = simplejson.load(conn)
    #write to file
    f = open('workfile'+str(i)+'.json', 'w')
    json.dump(rsp, f)
    