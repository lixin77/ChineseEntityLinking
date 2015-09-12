package msra.nlp.el;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.util.Pair;
import msra.nlp.edl.Query;
import msra.nlp.edl.QueryByStanford;
import msra.nlp.kb.Freebase;
import msra.nlp.kb.Wikipedia;

public class EL 
{
	// query mention's information
	protected String text = null;							// raw text of the passage
	protected Map<String, String> query = null; 			// a map type query 
	protected List<Map<String, Object>> mentions = null;	// mentions extracted from the given text
	// wikipedia and freebase interface
	public Wikipedia wiki = null;
	public Freebase freebase = null;
	public Query ner = null;
	
	//		Build an entity linking system
	public EL()
	{
		Initial();
	}
	
	/**
	 * initial this system
	 */
	private void Initial()
	{
		this.wiki = new Wikipedia();
		this.freebase = new Freebase();
		this.ner = new QueryByStanford();
	}
	
	//		Entity linking
	
	/**
	 * link the given query to the freebase
	 * @param query
	 * 			A map type of query. 
	 * 			A query should at least contain "name","begin" and "end" fields 
	 * 			given the mention name, begin offset and end offset 
	 * @param text
	 * 			The raw text of the passage to which the mention belongs.
	 * @return
	 * 			The linked id of the query in freebase or nil if the mention not found in freebase
	 */
	public String Entitylink(Map<String, String> query,String text)
	{
		this.text = text;
		this.query = query;
		List<Pair<Float, Integer>> candidates = NameScore();
		return null;
	}
	
	//		Name score
	
	private List<Pair<Float, Integer>> NameScore() 
	{
		
		return null;
	}

	//		Get alias
	
	private List<String> GetMentionAlias(String name)
	{
		return null;
	}
	
	private List<String> GetEntityAlias(String title)
	{
		List<String> alias = new ArrayList<>();
		alias.add(title);
		List<String> list; 
		list = this.wiki.GetAmbigTitles(title); // get title whose ambiugous titles include given title 
		if(list!=null)
		{
			alias.addAll(list);
		}
		list = this.wiki.GetRedirectedTitles(title); // get titles which redirect to the given title
		if(list!=null)
		{
			alias.addAll(list);
		}
		list = this.wiki.GetLinkedInAnchors(title);
		if(list!=null)
		{
			alias.addAll(list);
		}
		return alias;
	}
	

	public static void main(String args[])
	{
		Map map = new HashMap<>();
		map.put("name", 10);
		map.put("type", "fine");
		map.put(10,"ok!");
		System.out.println(map.get("name"));
		System.out.println(map.get("type"));
		System.out.println(map.get(10));
	}
}
