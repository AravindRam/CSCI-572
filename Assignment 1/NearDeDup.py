import sys
import os
import tika
from tika import parser

#Class used to invoke SimHash to compute the fingerprint and has a method to compute the Hamming Distance
class Hamming(object):

    def __init__(self, value='', hash=None):
  
        if hash:
            self.hash = hash
        else:
            self.hash_function(value)

    def hex(self):
        return hex(self.hash)
    
    def find_Hamming_Distance(a,b):
        xorValue = (a.hash ^ b.hash) & ((1 << 96) - 1)
        distance = 0
        while xorValue:
            distance += 1
            xorValue &= xorValue-1
        return distance

#Class to compute the fingerprint and return the similarity measure
class SimHash(Hamming):

    def hash_function(self, tokens):

        if type(tokens) == str:
            tokens = tokens.split()
        V = [0]*96
        for hashed_token in [self.token_to_hash(x) for x in tokens]:
            bitmask = 0
            for i in xrange(96):
                bitmask = 1 << i    #left shifts 1 by 'i' bits
                if hashed_token & bitmask:     #bitwise and with each bit position
                    V[i] += 1       # add 1 if the bit in t is 0
                else:
                    V[i] -= 1       # subtract 1 if the bit in t is 1

        fingerprint = 0
        for i in xrange(96):
            if V[i] >= 0:
                fingerprint += 1 << i   #compute fingerprint by adding the positions with 1.
        self.hash = fingerprint
    
    def token_to_hash(self, V):

        if V == "":
            return 0
        else:
            hashed_token = ord(V[0])<<7 #ord() gives unicode equivalent of a character and left shift to 7 positions
            mul = 1000003
            mask = 2**96-1
            for character in V:
                hashed_token = ((hashed_token*mul)^ord(character)) & mask
            hashed_token ^= len(V)
            if hashed_token == -1:
                hashed_token = -2
            return hashed_token

    def similarity(self, other_hash):
        return float(96 - self.find_Hamming_Distance(other_hash)) / 96

# Main Function
fnamesDict = {}                                  # dictionary used to store the names of the filenames.
hashlist=[]                                      # list to append the fingerprint of each image
flaglist=[]                                      # list containing the flag of each image where it is marked as duplicate or not
hashinput=""                                     # input string for the hashing algorithm
id=0                                             # counter to keep track of the number of the images
No_of_duplicates=0                               # counter to keep track of the number of duplicates

for filename in os.listdir(sys.argv[1]):
    if(filename!=".DS_Store"):
        parsed = parser.from_file(os.getcwd()+"/"+sys.argv[1]+"/"+filename) # Retrieve the metadata
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

            hash = SimHash(hashinput)      #Compute simhash
            hashlist.append(hash)          #append the fingerprint to the list
            id=id+1
            hashinput=""


flaglist = [0] * id
duplicate_list = []

for hashValue1 in hashlist:
    for hashValue2 in hashlist[hashlist.index(hashValue1)+1:]:
        if(flaglist[hashlist.index(hashValue1)] == 0 and flaglist[hashlist.index(hashValue2)] == 0):  #Check if image is already classified as near duplicate
            percentSimilar = hashValue1.similarity(hashValue2)*100      #get the similarity percentage
            if(percentSimilar > 80):                                    #check if fingerprints are 80% similar
                flaglist[hashlist.index(hashValue2)]=1
                No_of_duplicates+=1                                     #increment the counter for the duplicate images
                duplicate_list.append(hashlist.index(hashValue2))

print "No of near duplicates : "+str(int(No_of_duplicates))             #print the number of near duplicates
if(No_of_duplicates > 0):
    print "Near Duplicate Image(s):"
for index in duplicate_list:
    print fnamesDict[index]                                          #print the name of the near duplicate images
