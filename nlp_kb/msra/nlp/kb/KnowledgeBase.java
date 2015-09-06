package msra.nlp.kb;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;

import edu.stanford.nlp.util.Pair;

public interface KnowledgeBase 
{
	//				Get Info by entity ID
	
	/**
	 * get name of the entity with given id
	 * @param id
	 * 		A string type of id of the entity
	 * @return
	 * 		A String type of the name of the entity
	 */
	public String GetName(Object id);
	
	/**
	 * get type fo the entity with given id
	 * @param id
	 * @return
	 */
	public default EntityType GetType(Object id)
	{
		return EntityType.UD;
	}
	
	/**
	 * get description of the entity with given id
	 * @param id
	 * @return
	 */
	public String GetText(Object id);
	
	/**
	 * get context information of the entity with given id.
	 * The context information format is defined by the implementer(maybe a relation net).
	 * @param id
	 * @return
	 */
	public Object GetContext(Object id);
	
	/**
	 * get external link id of the entity with given id
	 * @param id
	 * @return
	 * 		A string type of external link id
	 */
	public default String GetExternalId(Object id)
	{
		return null;
	}

	public Map<String, Object> GetEntity(Object id);
	
	//			Get Info by index	
	
	/**
	 * get entity id by index
	 * @param index
	 * 		A integer type of index
	 * @return
	 * 		The id of the entity corresponding the index
	 */
	public String GetId(int index);
	
	
	/**
	 * get name of the entity in given index
	 * @param id
	 * 		A string type of id of the entity
	 * @return
	 * 		A String type of the name of the entity
	 */
	public String GetName(int index);
	
	/**
	 * get type fo the entity in given index
	 * @param id
	 * @return
	 */
	public default EntityType GetType(int index)
	{
		return EntityType.UD;
	}
	
	/**
	 * get description of the entity in given index
	 * @param id
	 * @return
	 */
	public String GetText(int index);
	
	/**
	 * get context information of the entity in given index
	 * The context information format is defined by the implementer(maybe a relation net).
	 * @param id
	 * @return
	 */
	public Object GetContext(int index);
	
	/**
	 * get external link id of the entity in given index
	 * @param id
	 * @return
	 * 		A string type of external link id
	 */
	public default String GetExternalId(int index)
	{
		return null;
	}
	
	public Map<String, Object> GetEntity(int index);
	
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
