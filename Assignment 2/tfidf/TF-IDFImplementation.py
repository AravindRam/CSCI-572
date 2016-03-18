import requests
import tfidf

stop_words= list()

#load stop words in a list to remove stop words in list
for line in open('english.txt'):
    words = line.split()
    stop_words.append(words[0])

table = tfidf.tfidf()
count=0

# query to fire
query = "MSGUNTRADER shotguns colorodo"
final_word_list = list()
query_words = query.split()

#remove stop words from query and get list of filtered words in from query
for word in query_words:
    if word not in stop_words:
        final_word_list.append(word)

print(final_word_list)

#create a query filtered from stop words
query_filtered_line = ""
for item in final_word_list:
    query_filtered_line = query_filtered_line + item + " "

print(query_filtered_line)

#make REST call to solr to get documents relevant to query in json form
r = requests.get("http://10.120.120.102:8983/solr/collection1/select?q="+query_filtered_line+"&wt=json&indent=true&rows=100")
json_object=r.json()

#extract each document's content and store it in table in the form of words
for docs in json_object["response"]["docs"]:
    line = docs["content"][0].encode("utf8")
    string_array=line.split()
    table.addDocument(docs["id"].encode("utf8"),line.split())
    count = count +1


#calculate tf-idf value of each document with respect to query
TF_IDF_dict = dict()
TF_IDFList = table.similarities (final_word_list)

for item in TF_IDFList:
    TF_IDF_dict[item[0]]=item[1]

#store tf-idf values in the form of decending order of tf-idf values of documents
sorted_docs=sorted(TF_IDF_dict.items(), key=lambda x:x[1],reverse=True)

print(sorted_docs)