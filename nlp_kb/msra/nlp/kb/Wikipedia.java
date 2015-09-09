package msra.nlp.kb;

import java.io.ObjectInputStream.GetField;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.util.Pair;

public class Wikipedia 
{
	/**
	 * get given title's redirected title if exist
	 * @param title
	 * 			A String type title 
	 * @return
	 * 			The redirected title of the queried title
	 */
	public String GetRedirect(String title)
	{
		return null;
	}
	
	/**
	 * get disambiguous items of the given title in wikipedia 
	 * @param title
	 * 			The query title 
	 * @return
	 * 			A pair type of disambigous items of the query title(or null if none exists).
	 * 			The element "first" of a pair represent the title of the disambiguous item and the element "second" of
	 * 			the pair is the corresponding abstract.
	 */
	public List<Pair<String, String>> GetDisambiguity(String title)
	{
		return null;
	}
	
	
}
