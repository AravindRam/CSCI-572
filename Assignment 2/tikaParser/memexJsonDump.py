#program to parse the data from the memex dump

import json
import os
import solr

#function to check whether a string is a number
def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False

#function to remove ctrl-characters from a string
stripped = lambda s: "".join(i for i in s if 31 < ord(i) < 127)

#Directory where the memex json files are stored
rootdir="/Users/redshepherd/Downloads/out"

#create a connection to solr
s = solr.SolrConnection('http://localhost:8983/solr/memexcollection')
#set ID
idCount=1
#parse each json file in the directory
for root, subFolders, files in os.walk(rootdir):
    for file in files:
        #parse only json files
        if file.endswith(".json")!=True:
            continue
        #open file
        with open(os.path.join(root,file)) as data_file:
            availableFrom="N/A"
            content="N/A"
            placeName="N/A"
            price="0"
            lat="-9999"
            lon="-9999"
            stateName="N/A"
            title="N/A"
            siteName="N/A"
            category="N/A"
            imageStr="N/A"
            buyerType="N/A"
            keywordStr="N/A"
            buyerOrgName="N/A"
            buyerDescription="N/A"
            buyerStartDate="N/A"

            sellerType="N/A"
            sellerContactName="N/A"
            sellerTelephoneNumber="N/A"
            sellerContentDescription="N/A"
            sellerStartDate="N/A"
            sellerUrl="N/A"
            seriallNumberString="N/A"
            

            #set initial pageRank values
            pagerank="0.15"
        

            print(file)
            #load the file into a json object
            data = json.load(data_file)
            
            #extract all required fields from the json object to be pushed into solr
            if(data):
                if "availableAtOrFrom" in data:
                    if "address" in data["availableAtOrFrom"]:
                        availableFrom=data["availableAtOrFrom"]["address"]["name"]
                if "description" in data:
                    content=data["description"]
                    content = stripped(content)
                #Geolocation Info
                if "geonames_address" in data:
                    placeName=data["geonames_address"][0]["hasPreferredName"]["label"]
                    stateName=data["geonames_address"][0]["fallsWithinState1stDiv"]["hasName"]["label"]
                    lat=data["geonames_address"][0]["geo"]["lat"]
                    lon=data["geonames_address"][0]["geo"]["lon"]
                #Buyer information 
                if "buyer" in data:
                    buyerType=data["buyer"]["a"]
                    if "memberOf" in data["buyer"]:
                        if "name" in data["buyer"]["memberOf"]["memberOf"]:
                            buyerOrgName=data["buyer"]["memberOf"]["memberOf"]["name"]
                        if "startDate" in data["buyer"]["memberOf"]:
                            buyerStartDate=data["buyer"]["memberOf"]["startDate"]
                    buyerDescription=data["buyer"]["description"]
                if "price" in data:
                    price= data["price"]
                    if not is_number(price):
                        price="0.0"
                if "title" in data:
                    title= data["title"]
                if "publisher" in data:
                    siteName= data["publisher"]["name"]
                #Seller Information
                if "seller" in data:
                    sellerType=data["seller"]["a"]
                    if "contactPoint" in data["seller"]:
                        sellerContactName=data["seller"]["contactPoint"]["name"]
                        if "telephone" in data['seller']['contactPoint']:
                            if isinstance(data['seller']['contactPoint']['telephone'], list):
                                sellerTelephoneNumber=data["seller"]["contactPoint"]["telephone"][0]["name"]
                            else:
                                sellerTelephoneNumber=data["seller"]["contactPoint"]["telephone"]["name"]
                    if "description" in data["seller"]:
                        sellerContentDescription=data["seller"]["description"]
                    if "memberOf" in data["seller"]:
                        if "startDate" in data["seller"]["memberOf"]:
                            sellerStartDate=data["seller"]["memberOf"]["startDate"]
                    if "url" in data["seller"]:
                        sellerUrl=data["seller"]["url"]
                if "itemOffered" in data:
                    if "category" in data["itemOffered"]:
                        category= data["itemOffered"]["category"]

                    imageArr=[]
                    keywordArr=[]
                    if "image" in data["itemOffered"]:
                        for i in range(len(data["itemOffered"]["image"])):
                            imageArr.append(data["itemOffered"]["image"][i]["url"])
                        imageStr=",".join(imageArr)

                    if "keywords" in data["itemOffered"]:
                        for i in range(len(data["itemOffered"]["keywords"])):
                            keywordArr.append(data["itemOffered"]["keywords"][i])
                        keywordStr=",".join(keywordArr)
                        
                    if "serialNumber" in data["itemOffered"]:
                        seriallNumberString=data["itemOffered"]["serialNumber"]


                #send to solr to index document
                s.add(id=idCount,availableFrom=availableFrom, content=content,Geographical_Name=placeName,
                      Geographical_Latitude=lat,Geographical_Longitude=lon,Geographical_Name_State=stateName,price=price,
                      title=title,siteName=siteName,category=category,images=imageStr,keywords=keywordStr,page_rank=pagerank,
                       buyerType=buyerType, buyerOrgName=buyerOrgName,buyerDescription=buyerDescription,buyerStartDate=buyerStartDate,
                      sellerType=sellerType,sellerContactName=sellerContactName, sellerTelephoneNumber=sellerTelephoneNumber,
                      sellerContentDescription=sellerContentDescription,sellerStartDate=sellerStartDate,sellerUrl=sellerUrl,serialNumber=seriallNumberString)

                s.commit()
                idCount=idCount+1
