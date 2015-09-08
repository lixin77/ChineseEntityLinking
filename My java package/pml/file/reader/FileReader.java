package pml.file.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.MarkingAnnotation;
import pml.file.FileException;
import pml.file.ReadModel;

public interface FileReader 
{
	/**
	 * set buffer size of the read stream.
	 * Only the buffered reader will implement this function, for others
	 * this function has no effect.
	 * @param size
	 * @throws FileException
	 */
	public default void SetBufferSize(int size) throws FileException
	{
		
	}
	
	/**
	 * set default charset for the file reader
	 * @param charset
	 * @throws FileException
	 */
	public default void SetCharset(Charset charset) throws FileException
	{
		
	}

	/**
	 * mark current position
	 * @param readAheadLimit
	 * 		Limit on the number of characters that may be read while still preserving the mark.
	 *  	An attempt to reset the stream after reading characters up to this limit or beyond
	 *  	may fail. A limit value larger than the size of the input buffer will cause a new 
	 *  	buffer to be allocated whose size is no smaller than limit. Therefore large values
	 *  	should be used with care.
	 * @throws FileException
	 */
	public default void Mark(int readAheadLimit) throws FileException
	{
		
	}

	/**
	 * reset read pointer at the position last marked
	 * @throws FileException -- if no mark has been made
	 */
	public default void Reset() throws FileException
	{
		
	}
	
	/**
	 * open file with default character set(initial is UTF-8).
	 * @throws FileNotFoundException
	 */
	public void Open() throws FileException;
	
	/**
	 * open file defined by given file path with default character set.
	 * @param filePath
	 * @throws IOException
	 */
	public void Open(String path) throws FileException;
	
	/**
	 * open file defined by given file path with given characer set
	 * @param filePath
	 * @param charset
	 * @throws IOException
	 */
	public void Open(String path, Charset charset);
	
	/**
	 * Close the reader
	 */
	public void Close();
	
	/**
	 * report if the reader is ready to read
	 * @return
	 * @throws FileException
	 */
	public boolean IsReady() throws FileException;
	
	/**
	 * read a character(applied to chinese and other languages) and turn it into a string type
	 * @return
	 * 				A string type of character.
	 * @throws IOException 
	 */
	public String Read() throws FileException;
	
	
	/**
	 * read string whose first character offset is beginIndex and last character offset is endIndex(excluded)
	 * The offset is counted from the begin of the document and the first character offset of the document is 0.
	 * Note: This method will reset the read pointer position and not return back.
	 * @param beginIndex
	 * 					Integer offset of the first character related to the beginning of the document
	 * @param endIndex
	 * 					Integer offset+1 of the last character related to the beginning of the document
	 * @return
	 * @throws FileException
	 */
	public default String Read(int beginIndex, int endIndex) throws FileException
	{
		return null;
		
	}
	
	/**
	 * read string whose first character offset is beginIndex and last character offset is endIndex(excluded) related to given position
	 * The offset is counted from the begin of the document or current position.
	 *  The first character offset of the document is labeled 0;
	 *  Current position is labeled 0;
	 *  Note: This method will reset the read pointer position and not return back.
	 *  	
	 *  	Example: 
	 *  		Given document: "I like java"
	 *  		Given current position: 4(k)
	 *  		Read(3,5,0) is "ik"
	 *  		Read(3,5,1) is "ja"
	 *  
	 * @param beginIndex
	 * 					Integer offset of the first character related to the beginning of the document
	 * @param endIndex
	 * 					Integer offset+1 of the last character related to the beginning of the document
	 * @param readModel:
	 * 					Decide the related position of the read pointer. 
	 * 					0 or ReadModel.Beg: beginning of the document
	 * 					1 or ReadModel.Cur: current postion of the reader
	 * @return
	 * @throws FileException
	 */
	public default String Read(int beginIndex, int endIndex, ReadModel readModel) throws FileException
	{
		return null;

	}
	
	public Object Scan(Object object);
	
	public Object Scan(Object object, String delimer);
	
	/**
	 * read a line of the file and return a string without line terminal.
	 * @return
	 * @throws IOException 
	 */
	public String ReadLine() throws FileException;
	
	/**
	 * read all of the file as a string
	 * @return
	 * @throws IOException 
	 */
	public String ReadAll() throws FileException;
	
	/**
	 * read all lines of the file.
	 * @return lines
	 * 					A list of lines
	 * @throws IOException 
	 */
	public List<String> ReadAllLine() throws FileException;
	
	/**
	 * read defined number of lines from file
	 * @param lineNum
	 * @return
	 * @throws IOException 
	 */
	public default List<String> ReadLines(int lineNum) throws FileException
	{
		List<String>lines = new ArrayList<>();
		String line;
		
		for(int i=0;i<lineNum;i++)
		{
			line = ReadLine();
			if(line==null)
			{
				break;
			}
			lines.add(line);
		}
		if(lines.size()==0)
		{
			return null;
		}
		return lines;
	}

	
	/**
	 * return current position of the reader pointer
	 */
	public default Integer pTell() throws FileException
	{
		return null;
		
	}
	
	
	/**
	 * reset the current position of the reader pointer to the given index
	 * @param index
	 * 				Destinate position of the reader pointer
	 */
	public default void  pSet() throws FileException
	{
		
	}
	

}
