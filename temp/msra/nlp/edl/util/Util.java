package msra.nlp.edl.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Util 
{
	/*								Unique(List<String> words)
	 * Remove the duplicated words in the input and return a list of string with unique elements.
	 */
	public static <T> List<T> Unique(List<T> words)
	{
		int threshold = 20;
		List<T> uniqueWords = new ArrayList<>();
		
		if(words.size()>threshold)
		{
			Set set = new HashSet<>(words.size());
			for(T word:words)
			{
				set.add(word);
			}
			Iterator<T> iterator = set.iterator(); 
			while(iterator.hasNext())
			{
				uniqueWords.add(iterator.next());
			}
		}
		else
		{
			
			for(T word:words)
			{
				if(!uniqueWords.contains(word))
				{
					uniqueWords.add(word);
				}
			}
		}
		return uniqueWords;	
	}

	public static <T> T[] Unique(T[] words) {
		List<T> uniqueWordList = Unique(Arrays.asList(words));
		T[] uniqueWordArray = uniqueWordList.toArray(null);
		return uniqueWordArray;
	}

}
