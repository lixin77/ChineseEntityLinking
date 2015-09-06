package pml.file.writer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import pml.file.FileException;
import pml.file.FileModel;

public interface FileWriter 
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
	 * open file whoes path is given in Constructor with default characer set
	 * and default file model(Open,OpenOrCreate,Append)
	 * @throws FileNotFoundException
	 */
	public void Open() throws FileException;
	
	/**
	 * open file whoes path is given by "path" with default characer set
	 * and default file model(Open,OpenOrCreate,Append)
	 * @param filePath
	 * @throws IOException
	 */
	public void Open(String path) throws FileException;
	
	/**
	 * open file whoes path is given by "path" with given characer set
	 * and default file model(Open,OpenOrCreate,Append)
	 * @param filePath
	 * @param charset
	 * @throws IOException
	 */
	public void Open(String path, Charset charset);

	/**
	 * open file whoes path given by filePath with given fileModel(Open,OpenOrCreate,Append) and default charater set
	 * @param path
	 * @param fileModel
	 */
	public void Open(String path, FileModel fileModel);
	
	/**
	 * open file whoes path given by filePath with given fileModel(Open,OpenOrCreate,Append) and given charater set
	 * @param path
	 * @param fileModel
	 * @param charset
	 */
	public void Open(String path, FileModel fileModel, Charset charset);
	
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

	public boolean Write(Object object) throws FileException;
	
	public boolean Writeln(String string) throws FileException;
	
}
