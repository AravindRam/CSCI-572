package SolrJTest.SolrTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;

/**
 * This code can be used to convert the mallet output to the one required by LDA to run.
 * To generate the mallet output run the follwing command
 *  >bin/mallet import-dir --input ~/Downloads/out2/ --output tutorial.mallet --keep-sequence --remove-stopwords
 *	>bin/mallet train-topics --input tutorial.mallet --num-topics 20 --output-state topic-state.gz --output-topic-keys tutorial_keys.txt --output-doc-topics tutorial_compostion.txt --output-state output-state.txt --topic-word-weights-file topic_word_weights.txt --word-topic-counts-file word_topic_count.txt
 * Then give the path of the files in the program below.
 * @author rrgirish
 *
 */

public class ConvertMalletToLDA{
	
	public static void main(String args[]) throws Exception{
	
	//From the mallet folder pick the required files.	
	String topicStatefileName="/Users/rrgirish/Documents/CSCI_572/Mallet/topic-state";
	String topicCompositionFileName="/Users/rrgirish/Documents/CSCI_572/Mallet/tutorial_compostion.txt";
	String wordWeightFileName="/Users/rrgirish/Documents/CSCI_572/Mallet/topic_word_weights.txt";
	FileReader fileReader = new FileReader(topicStatefileName);
	FileReader fileReader2 = new FileReader(topicCompositionFileName);
	FileReader fileReader3 = new FileReader(wordWeightFileName);

    // Create readers for each file
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
    BufferedReader bufferedReader3 = new BufferedReader(fileReader3);
    
    //Create Writers for each LDA File.
    Writer vocabFileWriter =	new OutputStreamWriter(new FileOutputStream(new File("vocab.dat")), "UTF-8");
    Writer fileNameFileWriter =	new OutputStreamWriter(new FileOutputStream(new File("files.dat")), "UTF-8");
    Writer thetaWriter =	new OutputStreamWriter(new FileOutputStream(new File("theta.dat")), "UTF-8");
    Writer wordsWriter =	new OutputStreamWriter(new FileOutputStream(new File("words.dat")), "UTF-8");

    
    
    //Create the vocabulary file(list of words called vocab.dat)
    HashSet<String> set =new HashSet<String>();
    HashSet<String> fileList =new HashSet<String>();
    String line="";
    int i=0;
    while((line = bufferedReader.readLine()) != null) {
    	if( i<3){
    		i++;
    		continue;
    	}
        String[] contents=line.split("\\s+");
        //the fifth element is the word
        set.add(contents[4]);
        //the second element is the filename
        fileList.add(contents[1]);
        
    }   
   
    for(String word: set){
    	vocabFileWriter.append(word+"\n");
    }
    
    //Create the file list file(files.dat)
    
    for(String word: fileList){
    	fileNameFileWriter.append("# "+word+" #\n");
    }
    
    
    
    //Create theta.dat
    while((line = bufferedReader2.readLine()) != null) {
    	  String[] contents=line.split("\\s+");
    	  
    	  for(int count=2;count<contents.length;count++){
    		  thetaWriter.append(contents[count]+" ");
    	  }
    	  thetaWriter.append("\n");
    	  
          
    }
    
    //Create words.dat(Probablity weight of each word in each topic)
    while((line = bufferedReader3.readLine()) != null) {
        String[] contents=line.split("\\s+");
        for(int count=0;count<set.size();count++){
        	wordsWriter.append(contents[2]+" ");
        }
        wordsWriter.append("\n");
    }
    
    
    // Always close files.
    bufferedReader.close();
    bufferedReader2.close();
    bufferedReader3.close();
    vocabFileWriter.close();
    fileNameFileWriter.close();
    thetaWriter.close();
    wordsWriter.close();
    
    System.out.println("Success");
	}
}
