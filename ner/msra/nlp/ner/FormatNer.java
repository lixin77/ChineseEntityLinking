package msra.nlp.ner;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import pml.file.FileAccess;
import pml.file.FileModel;
import pml.file.MyFile;
import edu.stanford.nlp.ling.CoreAnnotations.BeginIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.DocIDAnnotation;

public class FormatNer {
	
	/**
	 * core ner the bridge between stanford ner and user format ner accepting String input
	 * @param passage
	 * 			The passage has been segmented by stanford segmenter with xml label.
	 * @return
	 * 			Tokenizer list with full mention value, offset and type. Note the offset calculating takes whitespace
	 * 			into consideration and some types don't meet the TAC requirment.
	 * 			The return format is somehow like the following example:
	 * 			[Value=中国 Text=中国 OriginalText=中国 CharacterOffsetBegin=210 CharacterOffsetEnd=212 Before=     Position=53 Shape=cc GoldAnswer=null DistSim=106 Answer=GPE]
	 */
	static ChineseNER ner;
	
	public FormatNer() throws ClassCastException, ClassNotFoundException, IOException {
		ner = new ChineseNER();
	}
	
	// Construct a query file
	
	/**
	 * ner with specify model
	 * @param filePath
	 * 			Appoint the path of the model file.
	 */
	public FormatNer(String filePath) throws ClassCastException, ClassNotFoundException, IOException {
		ner = new ChineseNER(filePath);
	}
	
	  // English query, count whitespaces when calculating the indexes of mentions
	/**
	 * customized ner result,the interface to specific user with String input.
	 * @param text
	 * 			The raw passage segmented by stanford segmenter.
	 * @return 
	 * 			A list of Strings with each string recording one mention, which has the tag PER, ORG or GPE. The format
	 * 			of the string for each mention is like the following example:
	 * 			<query id="EL14_CMN_TRAINING_0001">
	 * 			<name>金二胖</name>
	 * 			<docid>bolt-cmn-DF-20-191614-4129558</docid>
	 * 			<beg>6158</beg>
	 * 			<end>6160</end>
	 * 			</query>
	 * @throws IOException
	 */
	public List<String> EnglishQuery(String text,String docID)
	{
		List<Map<String, String>> maps = new ArrayList<>();
		List<String> queryList = new ArrayList<>();
		String str;
		int i=0;
		
		maps =  MergeNerResult(ner.NerText(text));
		
		for(Map map : maps)
		{
			if(MentionFilter(map))
			{
				// Construct a query with the tokenizer information
				str = QueryString(map,docID); 
				queryList.add(str);
			}
		}			
		return queryList;
	}	
	/**
	 * customized ner result with passage stored in a file.
	 * @param filePath
	 * 					Source file path of the passage.
	 * @param what
	 * 					The docID string, default = fileName extracted from the file path
	 * @return
	 * 					A list of strings with each string contains a query of a mention
	 * @throws IOException
	 */
	public List<String> EnglishQuery(Path filePath, String ... what) throws IOException
	{
		String docID;
		if(what.length>0)
		{
			docID = what[0];
		}
		else{
			docID = GetNameFromPath(filePath);
		}
		String passage; 
		File file = filePath.toFile();
		if(!file.exists())
		{
			throw new FileNotFoundException();
		}
		byte[] encoded = Files.readAllBytes(filePath);
		passage = new String(encoded, StandardCharsets.UTF_8);	
		List<String> queryList = EnglishQuery(passage,docID);
		return queryList;
		
	}	
	/**
	 * customized ner result with passage stored in a file and output the result into a file.
	 * @param sourcePath
	 * @param desPath
	 * @throws IOException
	 */
	public void EnglishQuery(Path sourcePath, Path desPath) throws IOException
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
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(bufferedOutputStream));
		List<String> queryList = EnglishQuery(sourcePath);		
		for(String str : queryList)
		{
			bufferedWriter.write(str);
		}
		bufferedWriter.close();
		fileOutputStream.close();
	}
	/**
	 *  customized ner result of a text passage and store the result in a file
	 * @throws IOException 
	 * 
	 */
	public void EnglishQuery(String text,String docID, Path desPath) throws IOException 
	{
		File file = desPath.toFile();
		if(!file.exists())
		{
			file.createNewFile();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
		List<String> queryList = EnglishQuery(text,docID);		
		for(String str : queryList)
		{
			bufferedWriter.write(str);
		}
		bufferedWriter.close();
		fileOutputStream.close();
	}

	 // Chinese query without condidering the whitespace
	
	/**
	 * 	get query from chinese text
	 * @param text
	 * @param docID
	 * @return
	 */
	public  List<String> ChineseQuery(String text,String docID)
	{
		List<Map<String, String>> maps = new ArrayList<>();
		List<String> queryList = new ArrayList<>();
		String str;
		
		maps =  MergeNerResult(ner.NerText(text));
		for(Map<String, String> map : maps)
		{
			if(MentionFilter(map))
			{
				 
				 int begin = Integer.parseInt(map.get("CharacterOffsetBegin"));
				 int end = Integer.parseInt(map.get("CharacterOffsetEnd"));
				 // get the sub string from the begining to the begin of the mention and count the whitespace generated by segmentor.
				 str = text.substring(0,end+1);
				 int whiteSpaceNum = CountWhiteSpace(str);
				 map.put("CharacterOffsetBegin",String.valueOf(begin-whiteSpaceNum));
				 map.put("CharacterOffsetEnd",String.valueOf(end-whiteSpaceNum-1)); // The end offset returned by ner is the index+1 of the end character.
				// Construct a query with the tokenizer information
				str = QueryString(map,docID); 
				queryList.add(str);
			}
		}			
		return queryList;
	}
	public List<String> ChineseQuery(Path filePath, String ... what) throws IOException
	{
		String docID;
		if(what.length>0)
		{
			docID = what[0];
		}
		else{
			docID = GetNameFromPath(filePath);
		}
		String passage; 
		File file = filePath.toFile();
		if(!file.exists())
		{
			throw new FileNotFoundException();
		}
		byte[] encoded = Files.readAllBytes(filePath);
		passage = new String(encoded, StandardCharsets.UTF_8);	
		List<String> queryList = ChineseQuery(passage,docID);
		return queryList;
		
	}	
	/**
	 * customized ner result with passage stored in a file and output the result into a file.
	 * @param sourcePath
	 * @param desPath
	 * @throws IOException
	 */
	public void ChineseQuery(Path sourcePath, Path desPath) throws IOException
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
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(bufferedOutputStream));
		List<String> queryList = ChineseQuery(sourcePath);		
		for(String str : queryList)
		{
			bufferedWriter.write(str);
		}
		bufferedWriter.close();
		fileOutputStream.close();
	}
	/**
	 *  customized ner result of a text passage and store the result in a file
	 * @throws IOException 
	 * 
	 */
	public void ChineseQuery(String text,String docID, Path desPath)throws IOException 
	{
		File file = desPath.toFile();
		if(!file.exists())
		{
			file.createNewFile();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
		List<String> queryList = ChineseQuery(text,docID);		
		for(String str : queryList)
		{
			bufferedWriter.write(str);
		}
		bufferedWriter.close();
		fileOutputStream.close();
	}


 	// Extract simple ner text
 	
 	/**
 	 * ner to construct the contexts of mentions of entity.
 	 * @param text
 	 * 					A string type contains the content of the passage
 	 * @return list
 	 * 					A list of strings contains the mentions extracted from the passage.
 	 */
 	public List<String> NerContext(String text)	
 	{
 		List<Map<String, String>> maps = new ArrayList<>();
		List<String> list = new ArrayList<>();
		String str;
		
		maps =  MergeNerResult(ner.NerText(text));
		for(Map<String, String> map : maps)
		{
			if(MentionFilter(map))
			{
				list.add(map.get("OriginalText"));
			}			
		}
		return list;
 	}
 	/**
 	 * 
 	 * @param filePath
 	 * 					The path of the input file
 	 * @return
 	 * 					A string type contains the content of the passage
 	 * @throws IOException
 	 */
 	public List<String> NerContext(Path filePath) throws IOException
 	{
 		String passage; 
		File file = filePath.toFile();
		if(!file.exists())
		{
			throw new FileNotFoundException();
		}
		byte[] encoded = Files.readAllBytes(filePath);
		passage = new String(encoded, StandardCharsets.UTF_8);	
		List<String> queryList = NerContext(passage);
		return queryList;
 	}
 	/**
 	 * ner for the context feature and store the result into file
 	 * @param passage
 	 * 					The raw text of the passage.
 	 * @param desPath
 	 * 					The destinated path of the file for the storing the result.
 	 * @throws Exception
 	 */
 	public void NerContext(String passage,Path desPath) throws Exception
 	{
 		File file = desPath.toFile();
		if(!file.exists())
		{
			file.createNewFile();
		}
		MyFile myFile = new MyFile(desPath.toString(),FileModel.Open,FileAccess.Write);
		List<String> queryList = NerContext(passage);		
		for(String str : queryList)
		{
			myFile.Write(str+" ");
		}
		myFile.Close();
 	}
 	/**
 	 * ner for the context feature and store the result into file
 	 * @param sourcePath
 	 * 								The path of the input file
 	 * @param desPath
 	 * 								The destinated path of the file for the storing the result.
 	 * @throws Exception
 	 */
 	public void NerContext(Path sourcePath, Path desPath) throws Exception
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
		MyFile myFile = new MyFile(desPath.toString(),FileModel.Open,FileAccess.Write);
		List<String> queryList = ChineseQuery(sourcePath);		
		for(String str : queryList)
		{
			myFile.Write(str+" ");
		}
		myFile.Close();
 	}
 	
	// Script package
	private int CountWhiteSpace(String string) {
		String[] str = string.split("\\s");
		return str.length-1;
	}

	/**
	 * extract file name from the file path: file name exclude suffix
	 * @param filePath
	 * 					Path of the file
	 * @return
	 * 					File name
	 */
 	public static String GetNameFromPath(Path filePath)
	{
		String str = filePath.toString();
		return str.substring(str.lastIndexOf("\\")+1,str.lastIndexOf("."));
	}
	
	/**
	 * Construct xml format query string corresponding to a mention. The query is like:
	 * 			<query id="EL14_CMN_TRAINING_0001">
	 * 			<name>金二胖</name>
	 * 			<type>PER</type>
	 * 			<docid>bolt-cmn-DF-20-191614-4129558</docid>
	 * 			<beg>6158</beg>
	 * 			<end>6160</end>
	 * 			</query>
	 * 
	 * @param line
	 * 			A list of objects carrying the information of a tokenizer. The infomation is like:
	 * 			[Value=中国 Text=中国 OriginalText=中国 CharacterOffsetBegin=210 CharacterOffsetEnd=212 Before=     Position=53 Shape=cc GoldAnswer=null DistSim=106 Answer=GPE]
	 * 			and the input is its simple format:
	 * 			[中国, 中国, 中国, 210, 212, , 53, cc, null, 106, GPE]
	 * @return
	 * 			A xml style string display above.
	 */
 	private String QueryString(Map<String, String> map,String docID)
	{	
		StringBuilder query = new StringBuilder();
		query.append("<query>"+"\r");
		query.append("<name>"+map.get("OriginalText")+"</name>"+"\r");
		query.append("<type>"+map.get("Answer")+"</type>"+"\r");
		query.append("<docid>"+docID+"</docid>"+"\r");
		query.append("<beg>"+map.get("CharacterOffsetBegin")+"</beg>"+"\r");
		query.append("<end>"+map.get("CharacterOffsetEnd")+"</end>"+"\r");
		query.append("</query>"+"\r");
		return query.toString();
		
	}

 	/**
 	 * decide whether a tokenizer is a mention or not
 	 * @param map
 	 * 					The map instored a tokenizer's information
 	 * @return
 	 * 					true if the tokenizer is a mention otherwise false.
 	 */
 	private boolean MentionFilter(Map<String, String> map)
 	{
 		String type = map.get("Answer");
 		if(type.equals("PERSON") || type.equals("GPE") || type.equals("ORG"))
 		{
 			return true;
 		}
 		return false;
 	}
 	
 	/**
 	 * This function deal with the raw result of the ChineseNer,merge continuous same type mentions into one.
 	 * For example: merge 中国(ORG) 国务院(ORG) into 中国国务院(ORG)
 	 * @param maps
 	 * 					The ner result of chineseNer.
 	 * @return
 	 * 					A list of maps after merged.
 	 */
 	public List<Map<String, String>> MergeNerResult(List<Map<String, String>> maps)
 	{
 		// Check if input is null
 		if(maps.isEmpty())
 		{
 			return maps;
 		}
 		
 		String lastType = "",curType="";
 		Map<String, String> lastMap = null;
 		 List<Map<String, String>> list = new ArrayList<>();

 		/*lastMap = maps.get(0);
 		lastType = lastMap.get("Answer");*/
 		for(Map<String, String>map :maps)
 		{
 			curType = map.get("Answer");
 			if(curType.equals(lastType))
 			{
 				lastMap = Merge(lastMap,map); 				
 			}
 			else
 			{
 				if(lastMap!=null)
 				{
 					list.add(lastMap);
 				}
 				lastMap = map;
 				lastType = lastMap.get("Answer");
 			}			
 		}
 		list.add(lastMap);
 		return list;
 	}
 	
 	/**
 	"Value" : String of the tokenizer
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
 	private Map<String, String>  Merge(Map<String,String> lastMap,Map<String,String> curMap)
 	{
 		Map<String,String> map = lastMap;
 		map.put("Value", lastMap.get("Value")+curMap.get("Value"));
 		map.put("OriginalText", lastMap.get("OriginalText")+curMap.get("OriginalText"));
 		map.put("Text", lastMap.get("Text")+curMap.get("Text"));
 		map.put("CharacterOffsetEnd",curMap.get("CharacterOffsetEnd"));
 		return map;
 	}
 	
 	public static void main(String args[]) throws ClassCastException, ClassNotFoundException, IOException
 	{
 		System.out.print((char) 61);
 		FormatNer formatNer = new FormatNer();
 		formatNer.ChineseQuery(Paths.get("D:/Codes/Project/NLP/nlp_ner/stanford-ner/chinese_ner/input/XIN_CMN_19910108.0002.txt"),Paths.get("D:/Codes/Project/NLP/nlp_ner/stanford-ner/chinese_ner/input/test.xml"));
 		
 	}
}
