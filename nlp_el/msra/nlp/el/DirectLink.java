package msra.nlp.el;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.Line;

import msra.nlp.ner.ChineseNER;
import msra.nlp.ner.FormatNer;
import pml.file.*;
import msra.nlp.seg.*;

public class DirectLink extends EntityLink {
	public ChineseNER chineseNER = null;
	public  FormatNer ner = null;
	public Segmenter seg = null;
	public Map<String, String> id2type =new HashMap<>(); 
	
	public DirectLink() throws Exception
	{
		chineseNER = new ChineseNER();
		seg = new Segmenter();
		ner = new FormatNer();
		LoadId2Type();
	}
	
	public  void  LoadId2Type() throws Exception
	{
		MyFile myFile = new MyFile("./data/type.txt",FileModel.Open,FileAccess.Read);
		String id;
		String type;
		String line;
		int index = 0;
		while((line=myFile.ReadLine())!=null)
		{
			try
			{
				String[] str = line.split("\t");
				id2type.put(str[0],str[1]);
				index++;
			}
			catch(Exception e)
			{
				System.out.println(index);
			}
		}
		myFile.Close();
	}
	
	
	public List<Map<String, String>> ReadQuery() throws Exception
	{
		List<Map<String, String>> queries = new ArrayList<>();
		Map<String,String> query = new HashMap<String, String>();
		//String pattern = "<name>(\\S+?)</name>[.\\r\\n]+?<docid>(\\S+?)</docid>[.\\r\\n]+?<entity_id>(\\S+?)</entity_id>";
		MyFile myFile = new MyFile("./input/test/kbp_queries.xml",FileModel.Open,FileAccess.Read);
		while(myFile.ReadLine()!=null)
		{
			try
			{
				query = new HashMap<>();			
				String name = myFile.ReadLine();
				name = name.substring(name.indexOf(">")+1, name.indexOf("</"));
				query.put("name", name);
				String type = myFile.ReadLine();
				type = type.substring(type.indexOf(">")+1, type.indexOf("</"));
				query.put("type", type);
				String docid =myFile.ReadLine();
				docid = docid.substring(docid.indexOf(">")+1, docid.indexOf("</"));
				query.put("docid", docid);
				myFile.ReadLine();
				myFile.ReadLine();
				String entity_id =myFile.ReadLine();
				entity_id = entity_id.substring(entity_id.indexOf(">")+1, entity_id.indexOf("</"));
				query.put("entity_id", entity_id);
				queries.add(query);
				myFile.ReadLine();
			}
			catch(Exception e)
			{
			}
		}
		myFile.Close();
		return queries;
	}	

	public List<String> ConstructFeature(String docID, String name) throws Exception
	{
		MyFile myFile = new MyFile("./input/test/source_doc/"+docID.trim()+".xml",FileModel.Open,FileAccess.Read);
		String text = myFile.ReadAll();
		myFile.Close();
		
		String segedText = seg.segText(text);
		List<String>list = ner.NerContext(segedText);
		List<String> feature =  ConstructFeature(list, name);
		
		return feature;
	}
	
	public List<String> ConstructFeature(List<String> mentions, String name) throws Exception
	{
		byte window = 5;
		List<String> feature =  new ArrayList<>();
		feature.add(name);
		for(int i=0;i<mentions.size();i++)
		{
			if(name.equals(mentions.get(i)))
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
		return feature;
	}

	public  void DirectTest() throws Exception
	{
		List<Map<String, String>> queries = ReadQuery();
		List<String> feature;
		Candidate candidate = new Candidate();
		int canditateNum = 5;
		String[] entity; 
		int total = queries.size();
		int positive = 0;
		
		for(Map<String, String> query: queries)
		{
			
			feature = ConstructFeature(query.get("docid"), query.get("name"));	
			candidate = SelectCandidates(query.get("name"),canditateNum);
			if(candidate.candidates.size()>0)
			{
				candidate = MatchType(query.get("docid"), query.get("name"),query.get("type"),candidate);
			}
			// Choose the most likely candidate from the candidates.
			if(candidate!=null)
			{
				if(candidate.candidates.size()>0)
				{
					entity = SelectEntity(feature,candidate);
					if(entity!=null)
					{
						
						if(entity[3].substring(entity[3].lastIndexOf("/")+1).equals(query.get("entity_id")))
						{
							System.out.print(query.get("name")+" (Y)--> ");
							System.out.println(entity[0]+"\t"+entity[2]);
							positive++;
						}
						else {
							System.out.print(query.get("name")+" (N)--> ");
							System.out.println(entity[0]+"\t"+entity[2]);
						}
					}
					else
					{
						if(query.get("entity_id").equals("nil"))
						{
							System.out.print(query.get("name")+" (Y)--> ");
							System.out.println(entity[0]+"\t"+entity[2]);
							positive++;
						}
						else
						{
							System.out.print(query.get("name")+" (N)--> ");
							System.out.println(entity[0]+"\t"+entity[2]);
						}
					}
				}
				else 
				{
					if(query.get("entity_id").equals("nil"))
					{
						System.out.println(query.get("name")+" (Y)--> nil ");
						positive++;
					}
					else
					{
						System.out.println(query.get("name")+" (N)--> nil ");
					}
				}
			}
			else
			{
				if(query.get("entity_id").equals("nil"))
				{
					System.out.println(query.get("name")+" (Y)--> nil ");
					positive++;
				}
				else
				{
		
					System.out.println(query.get("name")+" (N)--> nil ");
				}
			}
		}
		float precision = (float) (1.0*positive/total);
		System.out.println("total: "+total);
		System.out.println("right number: "+positive);
		System.out.println("precision: "+precision);
	}

	public  Candidate MatchType(String docid,String mentionName,String type,Candidate candidate) throws Exception
	{

		MyFile myFile = new MyFile("./input/test/source_doc/"+docid.trim()+".xml",FileModel.Open,FileAccess.Read);
		String text = myFile.ReadAll();
		myFile.Close();
		
		String segedText = seg.segText(text);
		List<Map<String, String>>m = chineseNER.NerText(segedText);
		List<Map<String, String>> maps = ner.MergeNerResult(m);
		//String type =null;
		List<String[]> candidates = new ArrayList<>();
		List<Float> score = new ArrayList<>(); 
		float s =0;
		float maxScore = 0;
		
/*		for(Map<String, String> map: maps)
		{
			s = ScoreWords(mentionName, map.get("OriginalText"));
			if(s>maxScore)
			{
				maxScore = s;
				type = map.get("Answer");
			}
		}
		if(type==null)
		{
			return null;
		}*/
		for(int i=0;i< candidate.candidates.size();i++)
		{
			String[] entity = candidate.candidates.get(i);
			String id = ExtractEntityID(entity[3]);
			String entityType = id2type.get(id);
			if(entityType!=null)
			{
				if(type.equals(entityType) || (type.equals("PERSON") && entityType.equals("PER")) ||(type.equals("LOC") && entityType.equals("GPE")))
				{
					candidates.add(candidate.candidates.get(i));
					score.add(candidate.score.get(i));
				}
			}
		}
		Candidate can = new Candidate();
		can.candidates = candidates;
		can.score = score;
		return can;
	}


	public String ExtractEntityID(String link)
	{
		return link.substring(link.lastIndexOf("/m.")+1);
	}

	public static void main(String[] args) throws Exception 
	{
		DirectLink directLink = new DirectLink();
		directLink.DirectTest();
	}
}
