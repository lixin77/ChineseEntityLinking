package msra.nlp.el;

import java.util.Comparator;

/*						Sort arrary with descend order.
 * 
 */
public class ArrayIndexComparator implements Comparator<Integer>
{
    private final float[] array;

    public ArrayIndexComparator(float[] array)
    {
        this.array = array;
    }

    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
        {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }
    
    
    public int compare(Integer index1, Integer index2)
    {
         // Autounbox from Integer to int to use as array indexes
    	if(array[index1]>array[index2])
    		return -1;
    	else if(array[index1]==array[index2])
    		return 0;    
    	else {
			return 1;
		}
    }
	
}
