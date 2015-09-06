package pml.type;

import java.util.HashMap;
import java.util.Map;

import edu.stanford.nlp.pipeline.CoreMapAggregator;

public class Tuple<K,V>
{
	public K key;
	public V value;
	
	public static void main(String args[])
	{
		Map map = new HashMap<String, Integer>();
		map.put("test", 3);
		System.out.print(map.keySet());
	}
}
