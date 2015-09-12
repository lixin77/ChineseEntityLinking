package msra.nlp.ner;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import pml.file.reader.FileReader;
import pml.file.reader.LargeFileReader;
import pml.file.writer.FileWriter;
import pml.file.writer.LargeFileWriter;

public class StanfordNer implements Ner
{
	protected String serializedClassifier = "D:/Codes/Project/NLP/nlp_ner/stanford-ner/chinese_ner/data/chinese.misc.distsim.crf.ser.gz";
	public AbstractSequenceClassifier<CoreLabel> classifier;
	
	/**
	 * construct a ner instance with default parameters
	 */
	public  StanfordNer() 
	{
		Initial();
	}
	
	/**
	 * consturct a ner instance with given classifier
	 * @param classiferPath
	 */
	public StanfordNer(String classifierPath)
	{
		this.serializedClassifier = classifierPath;
		Initial();
	}
	
	/**
	 * load stanford ner classifier
	 */
	public void Initial() 
	{
		try {
			classifier = CRFClassifier.getClassifier(serializedClassifier);
		} catch (Exception e) {
			throw new NerException(e.getCause());
		} 
	}
	
	/**
	 *  ner a segmented passage, extract every tokenizer and its infomation such as offset, type and value.
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
	@Override
	public List<Map> NerText(String segedText) throws NerException 
	{
		Map<String, String> map;
		List<Map> maps = new ArrayList<>();
		
		for (List<CoreLabel> lcl : classifier.classify(segedText))
		{
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
	 * @throws FileException
	 */
	@Override
	public List<Map> NerFile(String filePath) throws NerException {
		FileReader reader = new LargeFileReader(filePath,StandardCharsets.UTF_8);
		String passage = reader.ReadAll();
		reader.Close();
		List<Map> maps =  NerText(passage);
		return maps;
	}
	
	public static void main(String args[])
	{
		System.out.println("StanfordNer.main()");
	}
}
