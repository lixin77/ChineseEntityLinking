package pml.file.reader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import pml.file.FileAccess;
import pml.file.FileException;
import pml.file.FileModel;

public class LargeFileReader implements pml.file.reader.FileReader
{
	public File file = null;
	public BufferedReader bufferedReader = null;
	
	
	/**
	 * size of buffer for read or write
	 */
	public static int bufferSize = 10000;
	/**
	 * threshold of file size. if the file size is below the threshold it will use Files.ReadAllines method otherwise the buffered method.
	 */
	public static long fileSizeThresh = 100;// 100M
	/**
	 * the character set used in the input file. Default is utf-8
	 */
	public Charset charset = StandardCharsets.UTF_8;
	
	/**
	 * create an empty instance
	 */
	public LargeFileReader()
	{
		
	}
	
	/**
	 * appoint file path
	 * @param filePath
	 */
	public LargeFileReader(String filePath)
	{
		file = new File(filePath);
	}
	
	/**
	 * appoint file path and character set to be used for reading the file
	 * @param filePath
	 * @param charset
	 */
	public LargeFileReader(String filePath,Charset charset)
	{
		this.charset = charset;
		this.file = new File(filePath);
	}
	
	/**
	 *  set buffer size of the bufferdReader
	 * @param size
	 * 					The buffer size
	 */
	public void SetBufferSize(int size)
	{
		bufferSize = size;
	}

	/**
	 * set character set for the file reading
	 * @param charset
	 * 					The character set to be used
	 */
	public void SetCharset(Charset charset)
	{
		this.charset = charset;
	}

	
	/**
	 * open file in default character set.
	 * @throws FileNotFoundException
	 */
	public void Open() throws FileException
	{
		if(file == null)
		{
			throw new FileException("No file has been defined!");
		}
		if(!file.isFile())
		{
			throw new FileException(file.toString()+" is a invalid file path!");
		}
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.toString()),this.charset),this.bufferSize);
		} catch (FileNotFoundException e) {
			throw new FileException(e.getCause());
		}
	}	
	
	/**
	 * open file defined by given file path with default character set.
	 * @param filePath
	 * @throws IOException
	 */
	public void Open(String filePath) throws FileException
	{
		Close();
		this.file = new File(filePath);
		Open();
	}
	
	/**
	 * open file defined by given file path with given characer set
	 * @param filePath
	 * @param charset
	 * @throws IOException
	 */
	public void Open(String filePath, Charset charset) throws FileException
	{
		this.charset = charset;
		Open(filePath);
	}

	/**
	 * Close the bufferReader.
	 * @throws IOException
	 */
	public void Close() throws FileException
	{
		if(bufferedReader != null)
		{
			try {
				bufferedReader.close();
			} catch (IOException e) {
				throw new FileException(e.getCause());
			}
		}
	}

	/**
	 * read a character and turn it into a string type
	 * @return
	 * 				A string type of character.
	 * @throws IOException 
	 */
	public String Read() throws FileException
	{
		
		if(this.bufferedReader==null)
		{
			if(IsFile())
			{
				Open();
			}
			else
			{
				if(this.file!=null)
				{
					throw new FileException(file.toString()+" is an invalid file path!");
				}
				else
				{
					throw new FileException("No File appointed For Reading!");
				}
			}
		}
		int c;
		try {
			c = this.bufferedReader.read();
		} catch (IOException e) {
			throw new FileException(e.getCause());
		}
		if(c == -1)
		{
			return null;		
		}
		return String.valueOf((char) c);
	}

	public Object Scan(Object object)
	{
		return null;
	}
	
	public Object Scan(Object object, String delimer)
	{
		return null;
	}
	
	/**
	 * read a line of the file and return a string without line terminal.
	 * @return
	 * @throws IOException 
	 */

	public String ReadLine() throws FileException
	{
		if(this.bufferedReader==null)
		{
			if(IsFile())
			{
				Open();
			}
			else
			{
				if(this.file!=null)
				{
					throw new FileException(file.toString()+" is an invalid file path!");
				}
				else
				{
					throw new FileException("No File appointed For Reading!");
				}
			}
		}
		String line;
		try {
			line = this.bufferedReader.readLine();
		} catch (IOException e) {
			throw new FileException(e.getCause());
		}
		return line;
	}

	/**
	 * read all of the file as a string
	 * @return
	 * @throws IOException 
	 */
	public String ReadAll() throws FileException
	{
		StringBuilder text = new StringBuilder();
		while(true)
		{
			String str = Read();
			if(str!=null)
			{
				text.append(str);
			}
			else
			{
				break;
			}
		}
		return text.toString();
	}

	/**
	 * read all lines of the file.
	 * @return lines
	 * 					A list of lines
	 * @throws IOException
	 */
	public List<String> ReadAllLine() throws FileException
	{
		List<String> lines = new ArrayList<>(); 
		String line;
		if(this.file.length()< fileSizeThresh*1024*1024) // 100M Byte
		{
			try {
				lines = Files.readAllLines(file.toPath(),this.charset);
			} catch (IOException e) {
				throw new FileException(e.getCause());
			}			
		}
		else
		{
			try {
				while((line = bufferedReader.readLine())!=null)
				{
					lines.add(line);			
				}
			} catch (IOException e) {
				throw new FileException(e.getCause());
			}
		}
		return lines;
	}

	/**
	 * judge if the path given is a file path
	 * @return
	 */
	private boolean IsFile()
	{
		if(file!=null)
		{
			return file.isFile();
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * judge if the file stream is ready for reading
	 * @return
	 * @throws IOException
	 */
	public boolean IsReady() throws FileException
	{
		if(bufferedReader!=null)
		{
			try {
				return bufferedReader.ready();
			} catch (IOException e) {
				throw new FileException(e.getCause());
			}
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * mark of the bufferedReader
	 * @param readAheadLimit
	 * @throws IOException
	 */
	public void Mark(int readAheadLimit) throws FileException
	{
		if(bufferedReader!=null)
		{
			try {
				bufferedReader.mark(readAheadLimit);
			} catch (IOException e) {
				throw new FileException(e.getCause());
			}
		}
	}
	
	/**
	 * reset to the mark position of the bufferedReader
	 * @throws IOException
	 */
	public void Reset() throws FileException
	{
		if(bufferedReader!=null)
		{
			try {
				bufferedReader.reset();
			} catch (IOException e) {
				throw new FileException(e.getCause());
			}
		}
	}
	
	public static void main(String args[]) throws Exception
	{
		pml.file.reader.FileReader reader = new LargeFileReader("D:/Project/NLP/nlp/seg/SegFeature.java");
		String text = reader.ReadAll();
		
	}
}