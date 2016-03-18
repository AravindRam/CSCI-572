import nutchpy


try:
    #read the data file to anlyze the metadata
    node_path = "/Users/redshepherd/Desktop/assignment1/crawldbs/crawldbs/26/crawldata26/crawldb/current/part-00000/data"
    seq_reader = nutchpy.sequence_reader
    dataArray = seq_reader.slice(0,seq_reader.count(node_path),node_path)

    asciEncodedDataArray = list()
    count = 0

    #convert url encoded data to ASCII format
    for item in dataArray:
        asciEncodedDataArray.append(list())
        for item1 in item:
            try:
                asciEncodedDataArray[count].append(item1.encode('ascii','ignore'))
            except Exception, err:
                print err

    #extract metadata from the ascEncodedDataArray
    metadataArray = list()
    for item in asciEncodedDataArray:
        for item1 in item:
            try:
                metadataArray.append(item1.split('\n'))
            except Exception, err:
                print err

    #extract the medataData which has contents
    InformationArray = list()
    for item in metadataArray:
            if len(item)>1:
                InformationArray.append(item)


    #extract content-type from information array
    contentTypeArray = list()
    for item in InformationArray:
        for item1 in item:
            try:
                temparray = item1.split('=')
                if(temparray[0] == "\tContent-Type"):
                    contentTypeArray.append(temparray[1])
            except Exception, err:
                print err

    #check if content-type is image to extract image MIME types and store it in dictionary
    imageDictionary = dict()
    for item in contentTypeArray:
            try:
                temparray = item.split('/')
                if(temparray[0] == "image"):
                    if temparray[1] in imageDictionary.keys():
                        imageDictionary[temparray[1]] = imageDictionary[temparray[1]] + 1
                    else:
                        imageDictionary[temparray[1]] = 1
            except Exception, err:
                print err

    print(imageDictionary)

except Exception, err:
    print err