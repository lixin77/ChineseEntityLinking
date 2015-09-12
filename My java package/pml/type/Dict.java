package pml.type;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.SpringLayout.Constraints;

public class Dict <K,V>
{
	public static int sizeThresh = 100;
	
	private List<K> keys = new ArrayList<>();
	
	private List<V> values = new ArrayList();
	
	private Map<K,V> map = null;
	
	private int size = 0;
	
	
	public Dict()
	{
		
	}
	
	public Dict(int thresh)
	{
		this.sizeThresh = thresh;
	}
	
	/**
	 * put a tuple to the dictionary
	 * @param key
	 * @param value
	 */
	public void put(K key, V value)
	{
		if(this.map==null)
		{
			if(this.size>this.sizeThresh)
			{
				ToMap();
				this.map.put(key,value);
				this.size++;
			}
			else 
			{
				int index = contains(key);
				if(index!=-1)
				{
					this.keys.remove(index);
					this.values.remove(index);
					this.keys.add(index,key);
					this.values.add(index,value);
				}
				else
				{
					this.keys.add(key);
					this.values.add(value);
					this.size++;
				}
			}
			
		}
		else
		{
			this.map.put(key,value);
			this.size++;
		}
	}
	
	

	/**
	 * get the value mapped by given key
	 * @param key
	 * @return
	 */
	public V get(K key)
	{
		if(this.map==null)
		{
			for(int i=0;i<this.size;i++)
			{
				if(this.keys.get(i).equals(key))
				{
					return this.values.get(i);
				}
			}
			return null;
		}
		else
		{
			return this.map.get(key);
		}
	}

	/**
	 * get the dictionary size
	 * @return
	 */
	public int size()
	{
		return this.size();
	}
	
	public Set<K> keySet()
	{
		if(this.map==null)
		{
			Set<K> set = new HashSet<>();
			set.addAll(this.keys);
			return set;
		}
		else 
		{
			return this.map.keySet();
		}
	}
	
	public List<V> valueList()
	{
		if(map==null)
		{
			return this.values;
		}
		else
		{
			return (List<V>) this.map.values();
		}
	}
	
	public boolean IsContains(K key)
	{
		if(this.map==null)
		{
			if(contains(key)!=-1)
			{
				return true;
			}
			return false;
		}
		else {
			return map.containsKey(key);
		}
	}
	
 	private void ToMap()
	{
		this.map = new HashMap<>();
		int index = 0;
		for(K key: this.keys)
		{
			this.map.put(key, this.values.get(index++));
		}
		this.keys.clear();
		this.values.clear();
	}
	
 	private int contains(K key) 
	{
		for(int i=0;i<this.size;i++)
		{
			if(this.keys.equals(key))
				return i;
		}
		return -1;
	}
	
}
