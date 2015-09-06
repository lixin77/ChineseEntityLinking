package msra.nlp.edl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msra.nlp.seg.Seg;
import msra.nlp.seg.Segmenter;
import pml.file.FileException;
import pml.file.reader.FileReader;
import pml.file.reader.LargeFileReader;
import pml.file.util.Util;
import pml.file.writer.FileWriter;
import pml.file.writer.LargeFileWriter;

public class QueryByStanford implements Query
{
	/**
	 * TODO: Add control to the segmenter and ner
	 */
	public static Seg seg = new Segmenter();
	public static StanfordNerScript ner = new StanfordNerScript();

	public QueryByStanford() 
	{
	}
	
	/**
	 * for every mention extracted from the raw text,constuct a Map<String,Stirng> type query.
	 * Every query include five fields:
	 * 		name:	the name of the mention
	 * 		type:	the type of the mention
	 * 		docid:	identifier of the document in which the mentions exists
	 * 		begin:	the offset of the first character of the mention in the document
	 * 		end:	the offset of the last character of the mention in the  document
	 * @param text
	 * 		The raw text of the document
	 * @param docID
	 * 		The idenifier of the document
	 * @return
	 * 		A list of Maps, each corresponding a query.
	 */
	@Override
	public List<Map<String, String>> QueryText(String text, String docID) 
	{
		List<Map<String, String>> maps = new ArrayList<>();
		List<Map<String, String>> ms = new ArrayList<>();
		
		maps =  MergeNerResult(ner.NerText(seg.SegText(text),text));
		for(Map<String, String> map : maps)
		{
			if(IsMention(map.get("Answer")))
			{
				Map<String, String> m = new HashMap<>();
				m.put(name, map.get("OriginalText"));
				m.put(type, map.get("Answer"));
				m.put(docid, docID);
				m.put(begin, map.get("CharacterOffsetBegin"));
				m.put(end, map.get("CharacterOffsetEnd"));
				ms.add(m);
			}
		}			
		return ms;
	}
	

	@Override
	public void QueryText(String text, String docID, String desPath) {
		List<Map<String, String>> maps = QueryText(text, docID);
		FileWriter writer = new LargeFileWriter(desPath);
		for(Map<String,String>map :maps)
		{
			try
			{
			writer.Write(map.get(name)+"\t");
			writer.Write(map.get(type)+"\t");
			writer.Write(map.get(docid)+"\t");
			writer.Write(map.get(begin)+"\t");
			writer.Write(map.get(end)+"\t");
			}
			catch(FileException e)
			{
				throw new EdlException(e.getCause());
			}
		}
	}

	@Override
	public List<Map<String, String>> QueryFile(String sourcePath, String docID)
	{
		FileReader reader = new LargeFileReader(sourcePath);
		String text = reader.ReadAll();
		reader.Close();
		List<Map<String, String>> maps = QueryText(text, docID);
		return maps;
	}
	
	@Override
	public List<Map<String, String>> QueryFile(String sourcePath)
	{
		
		String docID;
		try {
			docID = Util.Path2Name(sourcePath,false);
		} catch (FileException e) {
			throw new EdlException(e.getCause());
		}
		List<Map<String, String>> maps = QueryFile(sourcePath, docID);
		return maps;
	}
	
	@Override
	public void QueryFile(String sourcePath, String docID, String desPath)
	{
		FileReader reader = new LargeFileReader(sourcePath);
		String text = reader.ReadAll();
		reader.Close();
		QueryText(text, docID,desPath);
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
 	 *"Value" : String of the tokenizer
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
 	public Map<String, String>  Merge(Map<String,String> lastMap,Map<String,String> curMap)
 	{
 		Map<String,String> map = lastMap;
 		map.put("Value", lastMap.get("Value")+curMap.get("Value"));
 		map.put("OriginalText", lastMap.get("OriginalText")+curMap.get("OriginalText"));
 		map.put("Text", lastMap.get("Text")+curMap.get("Text"));
 		map.put("CharacterOffsetEnd",curMap.get("CharacterOffsetEnd"));
 		return map;
 	}
 	
 	/**
 	 * decide whether a tokenizer is a mention or not
 	 * @param map
 	 * 					The map instored a tokenizer's information
 	 * @return
 	 * 					true if the tokenizer is a mention otherwise false.
 	 */
 	public static boolean IsMention(String type)
 	{
 		if(type.equals("PERSON") || type.equals("GPE") || type.equals("ORG"))
 		{
 			return true;
 		}
 		return false;
 	}
 	
 	public static void main(String args[])
 	{
 		//System.out.println(QueryByStanford.test);
 		//QueryByStanford queryByStanford = new QueryByStanford();
 		//QueryByStanford query = new QueryByStanford();			
 	}
}
