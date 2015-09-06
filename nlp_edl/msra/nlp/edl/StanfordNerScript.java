package msra.nlp.edl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msra.nlp.ner.NerException;
import msra.nlp.ner.StanfordNer;
import pml.file.reader.FileReader;
import pml.file.reader.LargeFileReader;

public class StanfordNerScript extends StanfordNer 
{
	/**
	 * construct a ner instance with default parameters
	 */
	public  StanfordNerScript() 
	{
		super();
	}
	
	/**
	 * consturct a ner instance with given classifier
	 * @param classiferPath
	 */
	public StanfordNerScript(String classifierPath)
	{
		super(classifierPath);
		
	}

	/**
	 * ner a segmented passage, extract every tokenizer and its infomation such as offset, type and value.
	 * this will adjust the index label returned by stanford ner
	 * @param segedText
	 * 					The raw text segmented by stanford segmenter.
	 * @param rawText
	 * 					The original text(text before segmenting)
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
	public List<Map<String, String>> NerText(String segedText, String rawText) throws NerException {
		List<Map<String, String>> maps = (List<Map<String, String>>) super.NerText(segedText);
		maps = RenewIndex(maps,rawText);
		return maps;
	}
	
	/**
	 * ner a segmented passage, extract every tokenizer and its infomation such as offset, type and value.
	 * this will adjust the index label returned by stanford ner
	 * @param segedText
	 * 					The raw text segmented by stanford segmenter.
	 * @param rawFilePath
	 * 					The path of the original text file(text before segmenting)
	 */	
	public List<Map<String, String>> NerText(String segedText, Path rawFilePath)
	{
		FileReader reader = new LargeFileReader(rawFilePath.toString());
		String rawText = reader.ReadAll();
		reader.Close();
		return NerText(segedText,rawText);
	}
	
	/**
	 * ner a segmented passage, extract every tokenizer and its infomation such as offset, type and value.
	 * this will adjust the index label returned by stanford ner
	 * @param filePath
	 * 					The path of the segmented file.
	 * @param rawText
	 * 					The original text(text before segmenting)
	 */
	public List<Map<String, String>> NerFile(String filePath, String rawText) throws NerException 
	{
		FileReader reader = new LargeFileReader(filePath);
		String segedText = reader.ReadAll();
		reader.Close();
		return NerText(segedText,rawText);
	}

	/**
	 * ner a segmented passage, extract every tokenizer and its infomation such as offset, type and value.
	 * this will adjust the index label returned by stanford ner
	 * @param filePath
	 * 					The path of the segmented file.
	 * @param rawFilePath
	 * 					The path of the original text file(text before segmenting)
	 * @return
	 * 					A List<Map<String,String>> type.
	 */
	public List<Map<String, String>> NerFile(String filePath, Path rawFilePath)
	{
		FileReader reader = new LargeFileReader(filePath);
		String rawText = reader.ReadAll();
		reader.Close();
		return NerFile(filePath,rawText);
	}

	/**
	 * renew the index labels returned by stanfordNer. This will remove the whitespace involved by stanfordNer 
	 * @param maps
	 * @param rawText
	 * 				The orginal text(before segmention)
	 * @return
	 */
	protected List<Map<String, String>> RenewIndex(List<Map<String, String>> maps, String rawText) 
	{
		List<Map<String, String>> ms = new ArrayList<>();
		int curIndex=-1;
		char[] array = IndexInText(rawText);
		
		for(Map<String, String> map:maps)
		{
			String str = map.get("OriginalText");
			curIndex = MatchIndex(array, str.charAt(0), curIndex);
			map.put("CharacterOffsetBegin", String.valueOf(curIndex));
			curIndex = MatchIndex(array, str.charAt(str.length()-1), curIndex);
			map.put("CharacterOffsetEnd", String.valueOf(curIndex));
			ms.add(map);
		}
		return ms;
	}
	
	/**
	 * starting from the beginIndex+1 index to find input in the array and return the matching index 
	 * @param array
	 * 
	 * @param input
	 * @param beginIndex
	 * @return
	 */
	public static Integer MatchIndex(char[] array, char input ,int beginIndex)
	{
		if(beginIndex>=array.length)
		{
			throw new NerException("Begin index is out of list");
		}
		for(int i=beginIndex+1;i<array.length;i++)
		{
			if(input == array[i])
			{
				return i;
			}
		}
		return null;
	}
	
	/**
	 * tag the index of every character, except some seperators defined in Trim function, in the text
	 * @param text
	 * @return
	 */
	public static char[] IndexInText(String text)
	{
		String str = text.trim();
		return str.toCharArray();
	}
	
	/**
	 * tag the index of every character, except some seperators defined in Trim function, in the file
	 * @param filePath
	 * @return
	 */
	public static char[] IndexInFile(String filePath)
	{
		FileReader reader = new LargeFileReader(filePath);
		String text = reader.ReadAll();
		reader.Close();
		return IndexInText(Trim(text));
	}
	
	/**
	 * remove some types of seperators
	 * @param rawText
	 * @return
	 */
	public static String Trim(String rawText)
	{
		String str = rawText.replaceAll("[\\r\\n]", "");
		return str;
	}
}
