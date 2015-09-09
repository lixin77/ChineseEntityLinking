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
	
	/**
	 * get anchor texts linking to the given title
	 * @param desTitle
	 * 		Name of the query title
	 * @return
	 * 		A list of pairs contain all the anchor texts which links to the given title
	 * 		Each pair's first element is a int type records the times the anchor text 
	 * 		links to the title divide the total times the text taken as an anchor and
	 * 		the second element is the string represent of the anchor text  
	 */
	public List<Pair<Float,String>> GetLinkedAnchor(String desTitle)
	{
		return null;
	}
	
	/**
	 * get titles to which the anchor text links
	 * @param anchorText
	 * 		The anchor text
	 * @return
	 * 		The linked titles and it corresponding linked times by the anchor text
	 */
	public List<Pair<Integer, String>> GetAnchorLink(String anchorText)
	{
		return null;
	}
	
	
	
}
