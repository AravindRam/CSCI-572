import hashlib
import sys
import os
import tika
from tika import parser

fnamesDict = {}                                     # dictionary used to store the names of the filenames.
checksumDict = {}                                   # dictionary used to store the fingerprints of each image file.
countDict = {}                                      # dictionary containing the count of duplicate images
hashinput=""                                        # input string for the hashing algorithm
id = 0                                              # counter to keep track of the number of the images
No_of_duplicates=0                                  # counter to keep track of the number of duplicates
duplicate_list = []                                 # list containing the duplicate image filenames

for filename in os.listdir(sys.argv[1]):       # read one file at a time from the image directory passed as command line argument
    if(filename!=".DS_Store"):
        parsed = parser.from_file(os.getcwd()+"/"+sys.argv[1]+"/"+filename) #use tika-python parser to retrieve the metadata
        fnamesDict[id]=filename
        if("metadata" in parsed):
           
            if("Content-Length" in parsed["metadata"]):
                hashinput+=str(parsed["metadata"]["Content-Length"].split(" bytes")[0])
            elif("File Size" in parsed["metadata"]):
                hashinput+=str(parsed["metadata"]["File Size"].split(" bytes")[0])
            else:
                hashinput+=""

            if("tiff:ImageLength" in parsed["metadata"]):
                hashinput+=str(parsed["metadata"]["tiff:ImageLength"])
            elif("ImageLength" in parsed["metadata"]):
                hashinput+=str(parsed["metadata"]["ImageLength"])
            else:
                hashinput+=""

            if("tiff:ImageWidth" in parsed["metadata"]):
                hashinput+=str(parsed["metadata"]["tiff:ImageWidth"])
            elif("ImageWidth" in parsed["metadata"]):
                hashinput+=str(parsed["metadata"]["ImageWidth"])
            else:
                hashinput+=""

            if("Content-Type" in parsed["metadata"]):
                hashinput+=str(parsed["metadata"]["Content-Type"])
            else:
                hashinput+=""

            if("File Modified Date" in parsed["metadata"]):
                hashinput+=str(parsed["metadata"]["File Modified Date"])
            else:
                hashinput+=""

            h1 = hashlib.sha1()                          #compute the hash value using SHA-1 algorithm
            h1.update(str(hashinput))
            checksum= h1.hexdigest()                     # get the message digest
            if checksum in checksumDict.values():
                countDict[checksum].append(id)           #append the message digest to the list
            else:
                countDict[checksum] = []
            checksumDict[id]=checksum
            id = id+1
            hashinput=""

for i in sorted(countDict.keys()):
    if(countDict[i] != []):
        No_of_duplicates+=len(countDict[i])             #increment the counter for the duplicate images
        for index in countDict[i]:
            if(index not in duplicate_list):
                duplicate_list.append(index)

print "No of exact duplicates : "+str(No_of_duplicates)         #print the number of exact duplicates
if(No_of_duplicates > 0):
    print "Exact Duplicate Image(s):"
for index in duplicate_list:
    print fnamesDict[index]                                     #print the name of the exact duplicate images
