package pml.collection.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Unique
{
	/*								Unique(List<String> words)
	 * Remove the duplicated words in the input and return a list of string with unique elements.
	 */
	public static List<String> Unique(List<String> words)
	{
		int threshold = 20;
		List<String> uniqueWords = new ArrayList<>();
		
		if(words.size()>threshold)
		{
			// Deal with map
			Map<String, Integer> uniqueWordMap  = new HashMap<>();
			for(String word:words)
			{
				if(uniqueWordMap.get(word)==null)
				{
					uniqueWordMap.put(word,1);
				}
			}
			String[] array = uniqueWordMap.keySet().toArray(new String[uniqueWordMap.size()]);
			for(String word : array)
			{
				uniqueWords.add(word);
			}
		}
		else
		{
			
			for(String word:words)
			{
				if(!uniqueWords.contains(word))
				{
					uniqueWords.add(word);
				}
			}
		}
		return uniqueWords;	
	}
	public static String[] Unique(String[] words) {
		List<String> uniqueWordList = Unique(Arrays.asList(words));
		String[] uniqueWordArray = uniqueWordList.toArray(new String[uniqueWordList.size()]);
		return uniqueWordArray;
	}

}
