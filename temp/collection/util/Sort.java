package pml.collection.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.Pair.ByFirstPairComparator;
import edu.stanford.nlp.util.Pair.ByFirstReversePairComparator;

public class Sort<T>
{
	public static enum Order{ascend,descend};
	
	
	/**
	 * sort the unsorted list in defined order(ascend or descend).
	 * The return is a list of Pairs with each Pair the "first" field 
	 * store the value and the "second" field store its corresponding index 
	 * @param usList
	 * 		The unsorted list
	 * @param order
	 * 		The order(Order.ascend, Order.descend) fo the sort method
	 * @return
	 * 		A sorted list of Pairs. Each pair contains
	 * 		the value in usList and its corresponding index in "first" and "second"
	 * 		fields respectively
	 */
	public static <T> List<Pair<T, Integer>> sort(List<T> usList,Order order)
	{
		if(order==Order.ascend)
		{
			return sort(usList);
		}
		else
		{
			return ReverseSort(usList);
		}
	}
	
	/**
	 * sort the unsorted array in defined order(ascend or descend).
	 * The return is a list of Pairs with each Pair the "first" field 
	 * store the value and the "second" field store its corresponding index
	 * @param usList
	 * 		The unsorted array
	 * @param order
	 * 		The order(Order.ascend, Order.descend) fo the sort method
	 * @return
	 * 		A sorted list of pairs. Each pair contains
	 * 		the value in usList and its corresponding index in "first" and "second"
	 * 		fields respectively
	 */
	public static <T> List<Pair<T, Integer>> sort(T[] usArray,Order order)
	{
		if(order==Order.ascend)
		{
			return sort(usArray);
		}
		else
		{
			return ReverseSort(usArray);
		}
	}

	
	/**
	 * sort the unsorted list in ascend order.
	 * @param usList
	 * 		The unsorted list
	 * @return
	 * 		A sorted list of pairs. Each pair contains
	 * 		the value in usList and its corresponding index
	 */
	protected static <T> List<Pair<T, Integer>> sort(List<T> usList)
	{
		List<Pair<T, Integer>> pairs = new ArrayList<>();
		for(int i=0;i<usList.size();i++)
		{
			pairs.add(new Pair(usList.get(i),i));
		}
		List<Pair<T, Integer>> ps = new ArrayList<>(pairs);
		ByFirstPairComparator<T, Integer> indexComparator = new ByFirstPairComparator<>();
		Collections.sort(pairs,indexComparator);
		return pairs;
	}
	
	/**
	 * sort the unsorted list in descend order.
	 * @param usList
	 * 		The unsorted list
	 * @return
	 * 		A sorted list of pairs. Each pair contains
	 * 		the value in usList and its corresponding index
	 */
	protected static <T> List<Pair<T, Integer>> ReverseSort(List<T> usList)
	{
		List<Pair<T, Integer>> pairs = new ArrayList<>();
		for(int i=0;i<usList.size();i++)
		{
			pairs.add(new Pair(usList.get(i),i));
		}
		List<Pair<T, Integer>> ps = new ArrayList<>(pairs);
		ByFirstReversePairComparator<T, Integer> indexComparator = new ByFirstReversePairComparator<>();
		Collections.sort(pairs,indexComparator);
		return pairs;
	}	

	/**
	 * sort the unsorted array in ascend order.
	 * @param usArray
	 * 		The unsorted array
	 * @return
	 * 		A sorted Array of pairs. Each pair contains
	 * 		the value in usList and its corresponding index
	 */
	protected static <T> List<Pair<T, Integer>> sort(T[] usArray)
	{
		List<T> usList = Arrays.asList(usArray);
		return sort(usList);
	}
	
	/**
	 * sort the unsorted list in descend order.
	 * @param usList
	 * 		The unsorted list
	 * @return
	 * 		A sorted list of pairs. Each pair contains
	 * 		the value in usList and its corresponding index
	 */
	protected static <T> List<Pair<T, Integer>> ReverseSort(T[] usArray)
	{
		List<T> usList = Arrays.asList(usArray);
		return ReverseSort(usList);
	}
	
	
}
