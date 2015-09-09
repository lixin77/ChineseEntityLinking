package msra.nlp.kb;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import pml.file.reader.FileReader;
import pml.file.reader.LargeFileReader;

public class Freebase implements KnowledgeBase
{
	private static String namePath = "./data/title.txt";
	private static String namedEntityPath = "./data/namedEntity.txt";
	private static String textPath = "./data/description.txt";
	private static String idPath = "./data/id.txt";
	private static String typePath = "./data/type.txt";
	
	private Map<String,Integer> mentionFreqMap=new HashMap<>(400000);
	private List<String> names = null;
	private List<String> namedEntities = null;
	private List<EntityType> types = null;
	private List<Object> contexts = null;
	private List<String> texts = null;
	private List<String> ids = null;
	private Map<String, Integer> id2index = new HashMap<>(400000);
	private List<Map<String, Object>> entities = new ArrayList<>(); 
	private String words = "";	
	
	public Freebase()
	{
		long tBegin = System.currentTimeMillis();
		System.out.print("loading Freebase...\r");	
		Initial();
		System.out.print("Done!");
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tBegin;
		double elapsedSeconds = tDelta / 1000.0;
		System.out.println(String.format(" [%d sec]: "+elapsedSeconds));

	}
	
	public Freebase(Properties props)
	{
		long tBegin = System.currentTimeMillis();
		System.out.print("loading Freebase...\r");	
		if(props.get("namePath")!=null)
		{
			Freebase.namePath = props.getProperty("namePath");
		}
		if(props.get("typePath")!=null)
		{
			Freebase.typePath = props.getProperty("typePath");
		}
		if(props.get("textPath")!=null)
		{
			Freebase.textPath = props.getProperty("textPath");
		}
		if(props.get("idPath")!=null)
		{
			Freebase.idPath = props.getProperty("idPath");
		}
		if(props.get("namePath")!=null)
		{
			Freebase.namePath = props.getProperty("namePath");
		}
		
		Initial();
		System.out.print("Done!");
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tBegin;
		double elapsedSeconds = tDelta / 1000.0;
		System.out.println(String.format(" [%d sec]: "+elapsedSeconds));


	}
	
	//			Initial Freebase
	
	private void Initial()
	{
		ReadName();
		ReadNamedEntity();
		ReadType();
		ReadText();
		ReadId();
		Checker();
		Id2Index();
		MakeContext();
		MakeEntities();
		StatisticMentionFreq();
	}

	private void ReadName()
	{
		FileReader reader = new LargeFileReader(namePath);
		this.names = reader.ReadAllLine();
		reader.Close();
	}
	
	private void ReadNamedEntity()
	{
		FileReader reader = new LargeFileReader(namedEntityPath);
		this.namedEntities = reader.ReadAllLine();
		reader.Close();
	}
	
	private void ReadType()
	{
		FileReader reader = new LargeFileReader(typePath);
		List<String> types = reader.ReadAllLine();
		reader.Close();
		this.types = toEntityTypes(types);
	}
	
	private void ReadText()
	{
		FileReader reader = new LargeFileReader(textPath);
		this.texts = reader.ReadAllLine();
		reader.Close();
	}
	
	private void ReadId()
	{
		FileReader reader = new LargeFileReader(idPath);
		this.ids = reader.ReadAllLine();
		reader.Close();
	}
	
	private void Checker()
	{
		if(names.size()!=types.size())
		{
			throw new KnowledgeBaseException("Freebase data have benn disrupt!");
		}
		else if(names.size()!=ids.size())
		{
			throw new KnowledgeBaseException("Freebase data have benn disrupt!");
		}
		else if(names.size()!=texts.size())
		{
			throw new KnowledgeBaseException("Freebase data have benn disrupt!");
		}
		else if(names.size()!=namedEntities.size())
		{
			throw new KnowledgeBaseException("Freebase data have benn disrupt!");
		}
	}
	
	private void Id2Index()
	{
		for(int i=0;i<ids.size();i++)
		{
			id2index.put(ids.get(i), i);
		}
	}
	
	/**
	 * TODO: mining the context information of entities
	 */
	private void MakeContext()
	{
		Object[] temp = new Object[names.size()];
		this.contexts = Arrays.asList(temp);
	}
	
	private void MakeEntities()
	{
		for(int i=0;i<names.size();i++)
		{
			Map<String, Object> map = new HashMap<>();
			map.put("name",names.get(i));
			map.put("type", types.get(i));
			map.put("text", texts.get(i));
			map.put("context", contexts.get(i));
			map.put("id", ids.get(i));
			entities.add(map);
		}
	}
	
	private void StatisticMentionFreq()
	{
		words = String.join(" ", namedEntities);
		String[] wordArray = words.split(" ");
		for(String word : wordArray)
		{
			Integer freq=mentionFreqMap.get(word);
			if(freq==null)
			{
				mentionFreqMap.put(word,1);
			}
			else
			{
				mentionFreqMap.put(word,freq+1);
			}
		}
	}

	//			Get freebase by id
	public String GetName(String id) 
	{
		int index = id2index.get(id);
		return names.get(index);
	}

	public EntityType GetType(Object id) 
	{
		int index = id2index.get(id);
		return EntityType.toEntityType(names.get(index));
	}
	
	public String GetText(Object id) 
	{
		int index = id2index.get(id);
		return texts.get(index);
	}

	public Object GetContext(Object id) 
	{
		int index = id2index.get(id);
		return contexts.get(index);
	}

	public Map<String, Object> GetEntity(Object id)
	{
		int index = id2index.get(id);
		return entities.get(index);
	}
	
	//			Get Freebase by index
	@Override
 	public String GetId(int index) {
		return ids.get(index);
	}

	@Override
	public String GetName(int index) {
		return names.get(index);
	}

	@Override
	public String GetText(int index) {
		return texts.get(index);
	}
	
	public EntityType GetType(int index)
	{
		return types.get(index);
	}
	
	@Override
	public Object GetContext(int index) {
		return contexts.get(index);
	}

	public Map<String,Object> GetEntity(int index)
	{
		return entities.get(index);
	}
	
	
	//				Get freebase info of all entities
	
	@Override
	public Map<String, Integer> GetMentionFreqTable() {
		// TODO Auto-generated method stub
		return null;
	}

	public int GetSize()
	{
		return this.names.size();
	}
	
	@Override
	public List<String> GetNameTable() {
		return names;
	}

	@Override
	public List<EntityType> GetTypeTable() {
		return this.types;
	}

	private static List<EntityType> toEntityTypes(List<String> list)
	{
		List<EntityType> types = new ArrayList<>();
		
		for(String type:list)
		{
			types.add(EntityType.toEntityType(type));
		}
		return types;
	}
	
	@Override
	public List<String> GetTextTable() {
		return texts;
	}

	@Override
	public List<Object> GetContextTable() {
		return contexts;
	}

	
	public List<Map<String, Object>> GetEntities()
	{
		return entities;
	}

	@Override
	public java.lang.String GetName(Object entityLabel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public java.lang.String GetText(Object entityLabel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer GetIndex(Object id) {
		// TODO Auto-generated method stub
		return null;
	}
}
