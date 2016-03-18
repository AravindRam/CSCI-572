import nutch
import requests
import subprocess
from nutch.nutch import Server
from nutch.nutch import SeedClient

#URL of the nutch rest server
url='http://localhost:8081'

#get the nutch server object
def get_nutch():
    return nutch.Nutch()
    
#create a seedObject    
def get_seed():
    seed_urls=('http://www.zidaho.com','http://www.lionseek.com')
    sc = get_seed_client()
    return sc.create('gun_seed', seed_urls)
    

#start the crawl
def get_crawl_client():
    seed = get_seed()
    return get_nutch().Crawl(seed)

#optional method to start the nutch rest server. This program assumes that it is present in the runtime/local folder
def start_nutch_rest_server():
    subprocess.Popen(["bin/nutch", "startserver","&"])

#optional method to stop the rest server.
def stop_nutch_rest_server():
    requests.post(url+'/admin/stop')
    
    
#create a seedClientObject and return    
def get_seed_client():
    sv=Server(url)
    sc=SeedClient(sv)
    return sc
    

#method to run the crawl client    
def test_crawl_client():
    cc = get_crawl_client()
    rounds = cc.waitAll()
    jobs = rounds[0]


#start_nutch_rest_server()
test_crawl_client()
#stop_nutch_rest_server()             
