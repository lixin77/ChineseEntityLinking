package msra.nlp.kb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;


import edu.stanford.nlp.util.Pair;

public interface KnowledgeBase<L> 
{
	//				Get Info by entity ID or index
	
	/**
	 * get name of the entity with given id
	 * @param id
	 * 		A string type of id of the entity
	 * @return
	 * 		A String type of the name of the entity
	 */
	public String GetName(String id);
	
	/**
	 * get type fo the entity with given id
	 * @param id
	 * @return
	 */
	public default EntityType GetType(String id)
	{
		return EntityType.UD;
	}
	
	/**
	 * get description of the entity with given id
	 * @param id
	 * @return
	 */
	public String GetText(String id);
	
	/**
	 * get context information of the entity with given id.
	 * The context information format is defined by the implementer(maybe a relation net).
	 * @param id
	 * @return
	 */
	public Object GetContext(String id);
	
	/**
	 * get external link id of the entity with given id
	 * @param id
	 * @return
	 * 		A string type of external link id
	 */
	public default String GetExternalId(String id)
	{
		return null;
	}

	public Map<String, Object> GetEntity(String id);
	
	//			Get Info by index	
	
	/**
	 * get name of the entity with given index
	 * @param id
	 * 		A string type of id of the entity
	 * @return
	 * 		A String type of the name of the entity
	 */
	public String GetName(Integer index);
	
	/**
	 * get type fo the entity with given index
	 * @param id
	 * @return
	 */
	public default EntityType GetType(Integer index)
	{
		return EntityType.UD;
	}
	
	/**
	 * get description of the entity with given index
	 * @param id
	 * @return
	 */
	public String GetText(Integer id);
	
	/**
	 * get context information of the entity with given index.
	 * The context information format is defined by the implementer(maybe a relation net).
	 * @param id
	 * @return
	 */
	public Object GetContext(Integer index);
	
	/**
	 * get external link id of the entity with given index
	 * @param id
	 * @return
	 * 		A string type of external link index
	 */
	public default Integer GetExternalId(Integer index)
	{
		return null;
	}

	public Map<String, Object> GetEntity(Integer index);
	
	
	/**
	 * get entity id by index
	 * @param index
	 * 		A integer type of index
	 * @return
	 * 		The id of the entity corresponding the index
	 */
	public String GetId(int index);
	
	public Integer GetIndex(String id);
		
	
	//				Get info of all entites
	/**
	 * get the frequency map of mentions exist in knowledge base
	 * @return
	 */
	public Map<String, Integer> GetMentionFreqTable();
	
	/**
	 * get size of knowledge base
	 * @return
	 * 		A int type of knowledge base size
	 */
	public int GetSize();
	
	/**
	 * get all names of entities
	 * @return
	 * 		A list of Strings, with each contains name of an entity
	 */
	public List<String> GetNameTable();
	
	public List<EntityType> GetTypeTable();
	
	public List<String> GetTextTable();
	
	/**
	 * get context information of all the entities
	 * The context information format is defined by the implementer(maybe a relation net).
	 * @return
	 */
	public List<Object> GetContextTable();
	
	/**
	 * get all the external links of all the entities
	 * @return
	 */
	public default List<String> GetExIdTable()
	{
		return null;
	}
	
	
	
}
