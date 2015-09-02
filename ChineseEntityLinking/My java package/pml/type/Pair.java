package pml.type;


/**
 * Pair is a Class for holding mutable pair of objects.
 * This is especially useful in functions which return multi values.
 */
public class Pair<T1,T2> {
	
	public T1 first;
	public T2 second;
	
	public Pair()
	{
		// first = null; second = null; -- default initialization
	}
	
	public Pair(T1 first,T2 second)
	{
		this.first = first;
		this.second = second;
	}

}
