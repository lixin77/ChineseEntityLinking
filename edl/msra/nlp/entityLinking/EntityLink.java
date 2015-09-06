package msra.nlp.entityLinking;

import java.awt.datatransfer.FlavorTable;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.ServiceUnavailableException;

import org.omg.CORBA.PRIVATE_MEMBER;

import msra.nlp.ner.ChineseNER;
import msra.nlp.seg.Segmenter;
import pml.file.FileAccess;
import pml.file.FileModel;
import pml.file.MyFile;
import msra.nlp.ner.FormatNer;

public class EntityLink {
	
	public static LoadKB KB;
	public static Segmenter seg;
	public static FormatNer ner;
	private static final String[] nil = new String[]{"nil","","","nil"};
	protected static List<List<String>> aliasList = new ArrayList<>();
	protected static Map<String, Integer> aliasMap = new HashMap<>();
	
	/*								main(args): Main function
	 * 
	 */
  	public static void main(String[] args) throws Exception {
		
 		/*String[] array = new String[]{"a","b","c"};
 		Unique(array);
 		Show();
		*/
// 		EntityLink el = new EntityLink();
//		el.Test();
 		Show();
	}
 	public  static void Show() throws Exception
 	{
 		EntityLink el = new EntityLink();
		long timeStart = System.currentTimeMillis();
		Tuple<String, String[]> mention2entities = el.Link(Paths.get("./input/shortTest.txt"));
		long timeEnd = System.currentTimeMillis();
		String[] entity;
		for(int i=0;i<mention2entities.size();i++){
			entity = mention2entities.values.get(i);
			System.out.println(mention2entities.keys.get(i)+" --> "+entity[0]+" : "+entity[2]+"\n");
		}
		System.out.println("Time elapse(seconds): "+(timeEnd-timeStart)/1000.0+"s\n");
 	}
    
	/*								EntityLink() : The constructor
     * */
	public  EntityLink() throws Exception
	{
		if(KB == null)
		{
			KB = new LoadKB();
			seg = new Segmenter();
			ner = new FormatNer();
			Initiate();
		}		
		
	}
	/**
	 * load alias.
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
			for(String s:str)
			{
				if(aliasMap.get(s)==null)
				{
					aliasMap.put(s, index);
				}
			}
			index++;
		}
		myFile.Close();
	}
	
	/*								Link(text) or Link(<path of file>)
	 *  Extract mentions from passage and link them to the entities in KB
	 *  INPUT:
	 *  	String text: the content of the passage
	 *  	Path filePath : the path of file wanna to be Linked
	 *  OUTPUT:
	 *  	Tuple<String,List<String>> mention2entities: this is a personal type, it store the mentions and corresponding
	 *  												 entities as pair. Use Tuple::keys get mentions and Tuple::valus get
	 *  											     entities. The entity stored in Tuple include all its information.					
	 *  							
	 */
	/*public Tuple<String,String[]> Link(String text) throws IOException
	{
		int i = 0, j=0;
		String segedText = seg.segText(text);
		List<String>list = ner.NerContext(segedText);
		String[] nerArray = new String[list.size()];
		list.toArray(nerArray);
		nerArray = Unique(nerArray);
		TrimResult tr = Trim(nerArray);
		String[] trimedMentions=tr.trimedMentions;
		List<Integer> indexes = tr.indexes;
		
		List<List<String>> featureList = new ArrayList<>();
		list = new ArrayList<>();
		Tuple<String,String[]> mention2entity = new Tuple<String,String[]>();
		
		// Construct feature for each word
		 TODO:
		 * 	The method may need to be modified.
		

		byte window = 5;
		for(i=0;i<trimedMentions.length;i++)
		{ 
			list = new ArrayList<>();
			for(j=indexes.get(i)-window;j<=indexes.get(i)+window;j++)
			{
				if((j<0) || (j>nerArray.length-1))
				{
					list.add("");
				}
				else {
					list.add(nerArray[j]);
				}
			}
			featureList.add(list);
		}
		// Get entity candidates for each mention just by mention and title
		int canditateNum = 5; // Select the first 5 candidates
		Candidate candidate = new Candidate();
		String[] entity; 
		int index = 0;
		for(String mention : trimedMentions)
		{
			candidate = SelectCandidates(mention,canditateNum);
			
			// Choose the most likely candidate from the candidates.
			if(candidate.size()>1)
			{
				entity = SelectEntity(featureList.get(index),candidate);
				if(entity!=null)
				{
					mention2entity.put(mention, entity);
				}
				else
				{
					mention2entity.put(mention, nil);
				}
			}
			else if(candidate.size()==1)
			{
				mention2entity.put(mention, candidate.candidates.get(0));
			}
			else {
				mention2entity.put(mention, nil);
			}
			index++;
		}
		
		return mention2entity;
	}	
	*/
	public Tuple<String,String[]> Link(String text) throws IOException
	{
		int i = 0, j=0;
		byte window = 5;
		String segedText = seg.segText(text);
		List<String>list = ner.NerContext(segedText);
		List<String> uniqueMentions = Unique(list); 
		List<List<String>> featureList = ConstuctQueryFeature(list, window);
		Tuple<String,String[]> mention2entity = new Tuple<String,String[]>();
		
		// Get entity candidates for each mention just by mention and title
		int canditateNum = 5; // Select the first 5 candidates
		Candidate candidate = new Candidate();
		String[] entity; 
		int index = 0;
		for(String mention : uniqueMentions)
		{
			candidate = SelectCandidates(mention,canditateNum);
			
			// Choose the most likely candidate from the candidates.
			if(candidate.candidates.size()>1)
			{
				entity = SelectEntity(featureList.get(index++),candidate);
				if(entity!=null)
				{
					mention2entity.put(mention, entity);
				}
				else
				{
					mention2entity.put(mention, nil);
				}
			}
			else if(candidate.size()==1)
			{
				mention2entity.put(mention, candidate.candidates.get(0));
			}
			else {
				mention2entity.put(mention, nil);
			}
		}
		
		return mention2entity;
	}
	
	/*
	 * 									ConstructQueryFeature(mentions, window)
	 */
	/**
	 * consture feature for every mention. This method merge the context of the same mention within a passage.
	 * @param mentions
	 * 							A mention list ordered by the position in passage.
	 * @param window
	 * 							A byte type defining the window size of the context
	 * @return
	 */
	private List<List<String>>ConstuctQueryFeature(List<String> mentions, byte window)
	{
		List<List<String>> featureList = new ArrayList<>();
		List<String> uniqueMentions = Unique(mentions);
		List<String> feature = null;
		
		for(String mention : uniqueMentions)
		{
			feature = new ArrayList<>();
			for(int i=0;i<mentions.size();i++)
			{
				if(mention.equals(mentions.get(i)))
				{
					if(i<window)
					{
						feature.addAll(mentions.subList(0, i));
					}
					else
					{
						feature.addAll(mentions.subList(i-window, i));
					}
					if(i+window+1<=mentions.size())
					{
						
						feature.addAll(mentions.subList(i, i+window+1));
					}
					else
					{
						feature.addAll(mentions.subList(i, mentions.size()));
					}
				}
			}
			feature = Unique(feature);
			featureList.add(feature);
		}
		return featureList;
	}

	public Tuple<String, String[]> Link(Path filePath) throws IOException
	{
		byte[] byteFile = Files.readAllBytes(filePath);
		String text = new String(byteFile,StandardCharsets.UTF_8);
		Tuple<String, String[]> mention2entity = Link(text);

		return mention2entity;
	}
	
	/**
	 * delete misc type of mentions
	 * @param nerArray
	 * @return
	 */
	private TrimResult Trim(String[] nerArray) 
	{
		List<Integer> indexes = new ArrayList<>();
		int i=0;
		List<String> strList = new ArrayList<>();
		for(String str : nerArray){
			if(!str.matches(".*\\d.*"))
			{	
				strList.add(str);
				indexes.add(i);
			}
			i++;
		}
		String[] strArray = strList.toArray(new String[strList.size()]);
		return new TrimResult(strArray,indexes);
	}

	/***
	 * 	SelectCandidates(mention,candidateNum)
	 *  Extract candidate entities for a mention from KB by title matching.
	 *  INPUT:
	 *  	String mention: the mention for which to find candidates
	 *  	int candidateNum : the number of candidate designing to find
	 *  OUTPUT:
	 *  	Candidate candidates: this is a personal type, it store candidate entities and their corresponding scores
	 *  						  use Candidate::candidates to get the candidate entites and Candidate::score to get scores				
	 *  							
	 */
	public Candidate SelectCandidates(String mention, int canditateNum)
	{
		int stop=0;
		float threshold = (float) (0.5);
		int i=0;
		float[] score = new float[KB.titles.size()];
		ArrayIndexComparator comparator;
		Integer[] indexes;
		Candidate candidate = new Candidate();
		List<String> list = new ArrayList<>(); 
		
		for(String title : KB.titles)
		{
			score[i] = MatchTitle(mention, title);
			i++;
		}
		// Sort the score descendly and get the corresponding index
		comparator = new ArrayIndexComparator(score);
		indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);		
		// Return the first canditateNum candidates.
		for(i=0;i<canditateNum;i++)
		{
			if(score[indexes[i]]>threshold)
			{
				candidate.candidates.add(KB.featureList.get(indexes[i]));
				candidate.score.add(score[indexes[i]]);
			}
			
		}
		return candidate;
	}
	/**
	 * if there are redirection information, we can find the alias of mention and just consider the best matching case.
	 * @param mention
	 * 					Query mention: we can get its alias
	 * @param title
	 * 					Entity name.
	 * @return
	 */
	public  float MatchTitle(String mention, String title)
	{
		// Get alias of mention
		List<String> alias = new ArrayList<>();
		alias.add(mention);
		List<String> list =  GetAlias(mention);
		if(list!=null)
		{
			alias.addAll(list);
		}
		float score=0;
		float maxScore =0;
		// Get the score of the best matching
		for(String name: alias)
		{
			score = ScoreWords(name, title);
			if(score>maxScore)
			{
				maxScore = score;
			}
		}
		return  maxScore;
	}	
	public static float ScoreWords(String mention,String title)
	{
		char[] words = mention.toCharArray();
		
		float score =0;
		int initIndex = 0;
		int matchNum = 0;
		int maxNum = 0;
		for(char word : words)
		{
			matchNum = title.indexOf(word);
			if(matchNum!= -1 )
			{
				if(matchNum>initIndex)
				{
					score++;
					initIndex = matchNum;
				}			
			}
			if(matchNum>matchNum)
				maxNum = matchNum;
		}
		score = (float) score/Math.max(mention.length(), title.length());
		//score = (float) (2.0*score/(mention.length()+title.length()));
		return score;
	}
	public  List<String> GetAlias(String mention)
	{
		Integer index = aliasMap.get(mention);
		if(index!=null)
		{
			return aliasList.get(index);
		}
		return null;
	}
	
	/*								SelectEntity(mentions,candidate)
	 *  Select the most possible entity for a mention from KB by context mention matching.
	 *  INPUT:
	 *  	list<String> mentions: the mentions around the object mention
	 *  	Candidate candidate : the candidates selected by last function
	 *  OUTPUT:
	 *  	String[] entity: the destinate linked entity			
	 *  							
	 */
	public String[] SelectEntity(List<String> mentions, Candidate candidate)
	{
		List<String> namedEntities = new ArrayList<>();
		float[] score = new float[candidate.candidates.size()];
		ArrayIndexComparator comparator;
		Integer[] indexes;
		
		float threshold = (float) 0.0;
		
		int i=0;
		
		for(String[] entity : candidate.candidates)
		{
			namedEntities.add(entity[1]);
			// Weight scores calculated by title and named entities.
			float fs = ScoreFeature(mentions,entity[1]);
			if(fs<threshold)
			{
				score[i] = -1;
			}
			else
			{
				score[i] = (float) (0.6*ScoreFeature(mentions,entity[1])+0.4*candidate.score.get(i));
			}
			i++;
		}
		// Get the entity with the highest score
		
		comparator = new ArrayIndexComparator(score);
		indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		if(score[indexes[0]]<0)
		{
			return null;
		}
		return candidate.candidates.get(indexes[0]);
	}
	private float ScoreFeature(List<String> mentions, String namedEntities) {
		float score = 0;
		List<String> ne = Arrays.asList(namedEntities.split(" "));
		for(String mention : mentions)
		{
			if(mention.length()>0)
			{
				if(ne.contains(mention))
				{
					try
					{
						score++;
						//score +=Math.log10(2)/Math.log10(KB.wordFreqMap.get(mention)+1);
					}
					catch(Exception exception)
					{
						continue;
					}
				}
			}
		}
		return (float) (score*1.0/Math.min(mentions.size(), ne.size()));
	}

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

	/*								Test(): Get precision and recall of entity linking
	 * */
	public  void Test() throws IOException
	{
		
		// Load annotated data(in order)
		Tuple<String, String> entities = new Tuple<String, String>();
		String title,link;
		String[] str = new String[2];
		File file = new File("./input/statistic.txt");
		List<String>lines = new ArrayList<>();
		lines = Files.readAllLines(file.toPath());
		for(String line:lines){
			str = line.split("\t");
			title = str[0];
			if(str.length<1)
			{
				System.err.println(" ");
			}
			link = str[1];
			entities.put(title.trim(),link.trim());
		}
		
		// Load test passage
		int passageNum = 3;
		Tuple<String, String[]> mention2entities = new Tuple<String, String[]> ();
		for(int i=1;i<=passageNum;i++)
		{			
			mention2entities.putAll(Link(Paths.get(String.format("./input/passage%d.txt",i))));			
		}
		
		// Calculate positive number
		List<String> rightMentions = new ArrayList<>(); 
		List<String> wrongMentions = new ArrayList<>();
		int positive=0;										// Number of right linking
		int nilFoundNum = 0;								// Number of nil mentions from the passage
		int nilFoundPosNum = 0;								// Number of the right mentions with nil Tag
		int noneNilFoundNum = 0;							// Number of non nil mentions found in the passage
		int totalFound = mention2entities.size();			// Number of mentions found in the passage
		int total = entities.size();						// Number of mentions annotated in the passage
		int nilNum = entities.getByValue("nil").size();		// Number of nil mentions annotated
		int noneNilNum = total-nilNum;						// Number of none nil mentions annotated
		int endIndex = 0;
		for(int i=0;i<totalFound;i++)
		{	
			link = mention2entities.values.get(i)[3];
			endIndex = link.lastIndexOf('/');
			if(endIndex!=-1)
			{
				link = link.substring(endIndex+1);
			}
			if(mention2entities.values.get(i)[3].equals("nil"))
			{
				nilFoundNum++;
				if(entities.contains(mention2entities.keys.get(i),link))
				{
					nilFoundPosNum++;
					rightMentions.add(mention2entities.keys.get(i));
				}
				else
				{
					wrongMentions.add(mention2entities.keys.get(i));
				}
			}
			else if(entities.contains(mention2entities.keys.get(i), link))
			{
				positive++;
				rightMentions.add(mention2entities.keys.get(i));
			}
			else {
				wrongMentions.add(mention2entities.keys.get(i));
			}
		}
		/*
		 * TODO
		 */
		String[] entity;
		for(int i=0;i<mention2entities.size();i++){
			entity = mention2entities.values.get(i);
			System.out.println(mention2entities.keys.get(i)+" --> "+entity[0]+" : "+entity[2]+"\n");
		}
		System.out.println("\t\t\tRight mentions\n");
		for(String mention : rightMentions)
		{
			System.out.println(mention);
		}
		System.out.println("\t\t\tWrong mentions\n");
		for(String mention : wrongMentions)
		{
			System.out.println(mention);
		}
		
		noneNilFoundNum = totalFound-nilFoundNum;		
		float nilPrecision = (float) (1.0*nilFoundPosNum/nilFoundNum);
		float foundPrecision = (float) (1.0*positive/noneNilFoundNum);
		float nilRecall = (float) (1.0*nilFoundPosNum/nilNum);
		float foundRecall = (float) (1.0*positive/noneNilNum);
		float nilF = 2*nilPrecision*nilRecall/(nilPrecision+nilRecall);
		float foundF = 2*foundPrecision*foundRecall/(foundPrecision+foundRecall);
		System.out.println("total annotated mention num: "+total);
		System.out.println("found mentions num: "+totalFound);
		System.out.println("total nil mentions num: "+nilNum);
		System.out.println("found nil mentions num: "+nilFoundNum);
		System.out.println("total noneNil mentions num: "+noneNilNum);
		System.out.println("found noneNil mentions num: "+positive);
		System.out.println("nil precision: "+nilPrecision);
		System.out.println("nil recall: "+nilRecall);
		System.out.println("nil F:" + nilF);
		System.out.println("found precision: "+foundPrecision);
		System.out.println("found recall: "+foundRecall);
		System.out.println("found F:" + foundF);
				
	}
	
}














// Class
class Tuple<T,V>
{
	public List<T> keys = new ArrayList<>();
	public List<V> values = new ArrayList<>();
	
	public void put(T key,V value)
	{
		keys.add(key);
		values.add(value);
	}
	public List<V> get(T key)
	{
		List<V> results = new ArrayList<>();
		for(int i=0;i<keys.size();i++)
		{
			if(keys.get(i)==key)
			{
				results.add(values.get(i));
			}
		}
		return results;

	}	
	public void putAll(Tuple<T,V> tuple)
	{
		for(int i=0;i<tuple.keys.size();i++)
		{
			this.keys.add(tuple.keys.get(i));
			this.values.add(tuple.values.get(i));
		}
	}
	public boolean contains(T key,V value)
	{
		for(int i=0;i<keys.size();i++)
		{
			if(keys.get(i).equals(key) && values.get(i).equals(value))
			{
				return true;
			}
		}
		return false;		
	}
	public int size()
	{
		return keys.size();
	}
	public List<T> getByValue(V value)
	{
		List<T> results = new ArrayList<>();
		for(int i=0;i<keys.size();i++){
			if(values.get(i).equals(value))
			{
				results.add(keys.get(i));
			}
		}
		return results;
	}

}

// Class
class Result<T,V>
{
	T key;
	V value;
	public Result(T key,V value)
	{
		this.key = key;
		this.value = value;
	}
}

// Class
class Candidate
{
	public List<String[]> candidates = new ArrayList<>();
	public List<Float> score = new ArrayList<>(); 
	
	public int size()
	{
		return score.size();
	}
}


class TrimResult
{
	public String[] trimedMentions;
	public List<Integer> indexes;
	
	public TrimResult(String[] trimedMentions,List<Integer> indexes) {
		this.trimedMentions = trimedMentions;
		this.indexes = indexes;
	}
}
