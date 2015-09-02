package msra.nlp.ner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.geom.NoninvertibleTransformException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;

import pml.file.*;
import msra.nlp.entityLinking.*;

/**
 * @author Minlong Peng
 *
 */
public class ChineseNER {

	/**
	 * @param args
	 */
	
	String serializedClassifier = "D:/Codes/Project/NLP/nlp_ner/stanford-ner/chinese_ner/data/chinese.misc.distsim.crf.ser.gz";
	public static AbstractSequenceClassifier<CoreLabel> classifier;
	
	/**
	 * ner with default model
	 * @throws ClassCastException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public ChineseNER() throws ClassCastException, ClassNotFoundException, IOException
	{
		
		classifier = CRFClassifier.getClassifier(serializedClassifier);
	}
	
	/**
	 * ner with specify model
	 * @param filePath
	 * 			Appoint the path of the model file.
	 * @throws ClassCastException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public ChineseNER(String filePath) throws ClassCastException, ClassNotFoundException, IOException
	{
		serializedClassifier = filePath;
		classifier = CRFClassifier.getClassifier(serializedClassifier); 
	}
	
	/**
	 *  ner a passage, extract every tokenizer and its infomation such as offset, type and value.
	 * @param passage
	 * 					The raw text segmented by stanford segmenter.
	 * @return
	 * 					A list of map with each map contains the information of a tokenizer. The keys of each map
	 * 					are listed as following:
	 * 					"Value" : String of the tokenizer
	 * 					"Text" : similar to the Value
	 * 					"OriginalText" : similar to the Value
	 * 					"CharacterOffsetBegin" : The offset of the first character of the tokenizer.
	 * 					"CharacterOffsetEnd" : The offset of the last character of the tokenizer.
	 * 					"Before" : ?
	 * 					"Position" : ?
	 * 					"Shape" : ?
	 * 					"GoldAnswer" : ?
	 * 					"DistSim" : ?
	 * 					"Answer" : the mention type
	 */				
	public List<Map<String, String>> NerText(String passage)
	{
		Map<String, String> map;
		List<Map<String, String>> maps = new ArrayList<>();
		
		for (List<CoreLabel> lcl : classifier.classify(passage)) {
	          for (CoreLabel cl : lcl) {	
	        	  map = new HashMap<>();
	        	  for(Class key : cl.keySet())
	        	  {
	        		  String name = key.getSimpleName();
        			  name = name.substring(0, name.indexOf("Anno"));
	        		  if(cl.get(key) instanceof String)
	        		  {	        			  
	        			  map.put(name, (String) cl.get(key));
	        		  }
	        		  else{
	        			  map.put(name, String.valueOf(cl.get(key)));
	        		  }
	        	  }
	        	  maps.add(map);
	          }
		}
		return maps;
	}
		
	/**
	 * ner a passage stored in a file
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, String>> NerFile(Path filePath) throws IOException
	{		
		String passage; 
		File file = filePath.toFile();
		if(!file.exists())
		{
			throw new FileNotFoundException();
		}
		byte[] encoded = Files.readAllBytes(filePath);
		passage = new String(encoded, StandardCharsets.UTF_8);	
		List<Map<String, String>> maps =  NerText(passage);
		return maps;
	}
	
	/**
	 * ner a passage stored in a source file and write the result into a des file
	 * @param sourcePath
	 * @param desPath
	 * @throws IOException
	 */
	public void NerFile(Path sourcePath, Path desPath) throws IOException
	{
		File file = sourcePath.toFile();
		if(!file.exists())
		{
			throw new FileNotFoundException();
		}
		file = desPath.toFile();
		if(!file.exists())
		{
			file.createNewFile();
		}
		List<Map<String, String>> maps = NerFile(sourcePath);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
		for(int i = 0;i<maps.size();i++)
		{
			Map<String, String> map = maps.get(i);
			for(String key : map.keySet())
			{
				bufferedWriter.write(key+" : "+map.get(key)+"\t");
			}
			bufferedWriter.write("\r");
		}
		bufferedWriter.close();
		fileOutputStream.close();
	}

	
	/**
	
	public String nerFile(Path inputFilePath) throws IOException
	{
		// TODO
		String text; 
		byte[] encoded = Files.readAllBytes(inputFilePath);
		text = new String(encoded, StandardCharsets.UTF_8);
		//text = nerText(text);
		return text;
	}
	public void nerFile(Path inputFilePath,Path outputFilePath) throws IOException
	{
		// TODO
		File outputFile =outputFilePath.toFile();
		if(! outputFile.exists())
		{
			outputFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(outputFile);
		String text = nerFile(inputFilePath);
		fileWriter.write(text);
	}
	public String nerFile(String inputFilePath) throws IOException
	{
		Path path = Paths.get(inputFilePath);
		String text = nerFile(path);
		return text;
	}
	public void nerFile(String inputFile,String outputFile) throws IOException
	{
		Path inputFilePath = Paths.get(inputFile);
		Path outputFilePath =Paths.get(outputFile);
		nerFile(inputFilePath,outputFilePath);
	}

	
 	public void ExtractNER(Path sourceFilePath, Path destFilePath) throws IOException, ClassCastException, ClassNotFoundException{


		String fileContents = IOUtils.slurpFile(sourceFilePath.toString());
		String[] lines;
		String line;
		File inputFile = sourceFilePath.toFile();
		File outputFile = destFilePath.toFile();
		if(! outputFile.exists()){
			outputFile.createNewFile();
		}
		FileInputStream fileInputStream = new FileInputStream(inputFile);
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileInputStream));
		BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));


		// Read each sentence from source file
		int count =0;
		while((line = fileReader.readLine()) != null)
		{

			line = classifier.classifyWithInlineXML(line);
			//System.out.println(line);
			line = DeleteTag(line);	
			//System.out.println(line);
			fileWriter.write(line+"\n");			
			count++;
			if(count%5000==0)
			{
				System.out.println(count+" passages nered!");
			}
		}
		fileReader.close();
		fileWriter.close();
		fileInputStream.close();
		fileOutputStream.close();		 
	}

	private String DeleteTag(String line){
		// TODO 
		String rgex = "<\\w*>(.*?)</\\w*>";
		Pattern pattern = Pattern.compile(rgex);
		Matcher matcher = pattern.matcher(line);
		List<String> list =  new ArrayList<String>();
		String mention;
		String lineDelTag;


		while(matcher.find())
		{
			mention = matcher.group(1);
			mention = mention.replaceAll(" ", "");
			if(mention.length()>=2*"我".length())
			{
				list.add(mention);
			}
		}
		lineDelTag = String.join("\t", list);
		return lineDelTag;
	}

	/*public String nerText(String text)
	{
		String line;
		
		line =  classifier.classify(text);
		line = DeleteTag(line);
		return line;
	}
	 * @throws Exception */

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ChineseNER ner = new ChineseNER();
		
		MyFile myFile = new MyFile("D:/Codes/Project/NLP/nlp_entitylink/Chinese_entity_linking_java/ChineseEntityLinking/data/title.txt",FileModel.Open,FileAccess.Read);

		MyFile myFile2 = new MyFile("D:/Codes/Project/NLP/nlp_entitylink/Chinese_entity_linking_java/ChineseEntityLinking/data/description.txt",FileModel.Open,FileAccess.Read);

		
		String title="";
		String description ="";
		List<Map<String, String>> maps; 
		List<String> types = new ArrayList<>(); 
		int i=0;
		while((title=myFile.ReadLine())!=null)
		{
			description = myFile2.ReadLine();
			maps = ner.NerText(description);
			if(EntityLink.ScoreWords(maps.get(0).get("Value"),title)>0.5)
			{
				types.add(maps.get(0).get("Answer"));
			}
			else
			{
				types.add("unknow");
			}
		}
		myFile.Close();
		myFile2.Close();
		myFile = new MyFile("D:/Codes/Project/NLP/nlp_entitylink/Chinese_entity_linking_java/ChineseEntityLinking/data/type.txt",FileModel.OpenOrCreate,FileAccess.Write);
		for(String type : types)
		{
			myFile.Writeln(type);
		}
		myFile.Close();
		//ner.NerFile(Paths.get("D:/Codes/Project/NLP/nlp_entitylink/Chinese_entity_linking_java/ChineseEntityLinking/data/title.txt"));
		//ner.ExtractNER(Paths.get("./input/ChineseSample.txt"),Paths.get("./output/ChineseSample.seg"));
	}
}
