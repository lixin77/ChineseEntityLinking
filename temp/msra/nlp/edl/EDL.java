package msra.nlp.edl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Parameterizable;
import javax.naming.PartialResultException;

import org.omg.CORBA.PRIVATE_MEMBER;

import edu.stanford.nlp.ling.CoreAnnotations.MentionsAnnotation;
import edu.stanford.nlp.time.SUTime.InexactDuration;
import edu.stanford.nlp.util.Pair;
import msra.nlp.edl.util.Util;
import msra.nlp.kb.EntityType;
import msra.nlp.kb.Freebase;
import msra.nlp.kb.KnowledgeBase;
import msra.nlp.ner.Ner;
import pml.collection.util.Sort;
import pml.collection.util.Sort.Order;
import pml.file.FileAccess;
import pml.file.FileModel;
import pml.file.MyFile;


public class EDL
{
	protected static List<List<String>> aliasList = new ArrayList<>();
	protected static Map<String, List<Integer>> aliasMap = new HashMap<>();
	
	
	Query query = new QueryByStanford();
	
	
	protected static KnowledgeBase kb = new Freebase();
	
	
	//				Alias
	/**
	 * load alias
	 * @throws Exception
	 */

	private void  Initiate() throws Exception
	{
		MyFile myFile = new MyFile("./data/redirect_total.txt",FileModel.Open,FileAccess.Read);
		String line;
		int index = 0;
		while((line=myFile.ReadLine())!=null)
		{
			String[] str = line.split("\t");
			aliasList.add(Arrays.asList(str));
			List<Integer> list;
			for(String s:str)
			{
				if((list = aliasMap.get(s))==null)
				{
					list = new ArrayList<>();
					list.add(index);
					aliasMap.put(s, list);
				}
				else
				{
					list.add(index);
					aliasMap.put(s,list);
				}
			}
			index++;
		}
		myFile.Close();
	}
		
	public  List<String> GetAlias(String mention)
	{
		List<Integer> indexes = aliasMap.get(mention);
		List<String> alias = new ArrayList<>(); 
		if(indexes!=null)
		{
			for(int index:indexes)
			{
				alias.addAll(aliasList.get(index));
			}
		}
		alias = Util.Unique(alias);
		return null;
	}
	
	//				Context
	/**
	 * This function add context information into the mentions.
	 * The context information is flexible and is stored as value of field "context"
	 * @param text
	 * @param docID
	 * @return
	 */
	public List<Map<String, Object>> ExtractMention(String text, String docID)
	{
		List<Map<String, String>> temp = (List<Map<String, String>>) query.QueryText(text,docID);
		List<Map<String, Object>> mentions = ConvertType(temp);  
		List<Object> contexts = GetContextInfo(text, mentions);
		int i=0;
		for(Object context:contexts)
		{
			mentions.get(i++).put("context", context);
		}
		return mentions;
		
	}	
	
	/**TODO:
	 * extract context information of each mention
	 * @param text
	 * 			The raw text from which the mentions extracted
	 * @param mentions
	 * 			The formated mentions(name,type,docid,offset)extracted from the raw text
	 * @return
	 */
	private List<Object> GetContextInfo(String text, List<Map<String, Object>> mentions) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * Convert List<Map<String, String>> type to List<Map<String, Object>> type
	 * @param temp
	 * @return
	 */
	private List<Map<String, Object>> ConvertType(List<Map<String, String>> temp)
	{
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> object = new HashMap<>(); 
		for(Map<String,String> map :temp )
		{
			object = new HashMap<>();
			for(String key: map.keySet())
			{
				 object.put(key,map.get(key));
			}
			list.add(object);
		}
		return list;
	}
	
	//				NameScore
	public List<Pair<Float, Integer>> NameScore(String mention)
	{
		List<Float> scores = new ArrayList<>(kb.GetSize());
		List<String> alias = new ArrayList<>(kb.GetSize()); 
		float score = 0;
		float maxScore = 0;
		
		for(int i=0;i<kb.GetSize();i++)
		{
			maxScore = 0;
			String title = kb.GetName(i);
			for(String alia:alias)
			{
				score = GetWordSimilarity(alia,title);
				if(score>maxScore)
				{
					maxScore = score;
				}
			}
			scores.add(maxScore);
		}
		List<Pair<Float, Integer>> scorePairs = Sort.sort(scores,Order.descend);
		return scorePairs;
	}
	
	public static float GetWordSimilarity(String mention,String title)
	{
		
		if(mention==null || title==null)
		{
			return 0;
		}
		String w1;
		String w2;
		if(mention.length()>=title.length())
		{
			w1 = mention;
			w2 = title;
		}
		else
		{
			w1 = title;
			w2 = mention;
		}
		
		int matchNum = 0;
		int maxMatchNum = 0;
		int L1 = w1.length();
		int L2 = w2.length();
		int L3 = 0;
		int i=0;
		
		while(L1>0)
		{
			L3 = Math.min(L1--, L2);
			matchNum = MatchWord(w1.substring(i++,L3-1), w2.substring(0,L3-1));
			if(matchNum>maxMatchNum)
			{
				maxMatchNum = matchNum;
			}
		}
		return (float) 1.0*maxMatchNum/w1.length();
	}

	private static int MatchWord(String w1, String w2)
	{
		int matchNum = 0;
		int size = Math.min(w1.length(), w2.length());
		for(int i=0;i<size;i++)
		{
			if(w1.charAt(i)==w2.charAt(i))
			{
				matchNum++;
			}
		}
		return matchNum;
	}
	

	//				TypeScore
	
	/**
	 * Check if the types of mention and entity are matching, if so the score is 1.0
	 * else 0.0
	 * @param entityIndex
	 * 		The indexes of entity in freebase
	 * @param type
	 * 		The type of the mention
	 * @return
	 */
	public List<Pair<Float, Integer>> TypeScore(List<Integer> entityIndex, EntityType type)
	{
		List<Pair<Float, Integer>> pairs = new ArrayList<>(entityIndex.size());
		for(int index : entityIndex)
		{
			Pair<Float, Integer> pair = new Pair<>();
			if(kb.GetType(index)==type)
			{
				pair.first = (float) 1.0;
			}
			else
			{
				pair.first = (float) 0.0;
			}
			pair.second = index;
			pairs.add(pair);
		}
		return pairs;
	}
	
	public List<Pair<Float, Integer>> TypeScore(List<Integer> entityIndex, String type)
	{
		return TypeScore(entityIndex, EntityType.toEntityType(type));
	}

	//				ContextScore
	
	
	
	public static void main(String args[])
	{
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> object = new HashMap<>(); 
		List<Map<String, String>> temp = test();
		for(Map<String,String> map :temp )
		{
			object = new HashMap<>();
			for(String key: map.keySet())
			{
				 object.put(key,map.get(key));
			}
			list.add(object);
		}
	}
	
	public static List<Map<String, String>> test()
	{
		List<Map<String, String>> maps = new ArrayList<>();
		Map<String,String> map = new HashMap<>();
		map.put("one", "yes!");
		maps.add(map);
		return maps;
	}
	
}
