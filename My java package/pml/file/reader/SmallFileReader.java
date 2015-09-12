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
import pml.file.ReadModel;

public class SmallFileReader implements pml.file.reader.FileReader
{
	private File file = null;
	private BufferedReader bufferedReader = null;
	
	private Integer markPos = 0; // position of the last mark related to the begining, labeled 0, of the document
	private Integer curPos = 0; // current position of the reader pointer
	private String chaBuffer = null;
	
	/**
	 * size of buffer for read or write
	 */
	public static int bufferSize = 100000;
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
	public SmallFileReader()
	{
		
	}
	
	/**
	 * appoint file path
	 * @param filePath
	 */
	public SmallFileReader(String filePath)
	{
		file = new File(filePath);
	}
	
	/**
	 * appoint file path and character set to be used for reading the file
	 * @param filePath
	 * @param charset
	 */
	public SmallFileReader(String filePath,Charset charset)
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
		Close();
		if(this.file == null)
		{
			throw new FileException("No file has been defined!");
		}
		if(!this.file.isFile())
		{
			throw new FileException(this.file.toString()+" is a invalid file path!");
		}
		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file.toString()),this.charset),this.bufferSize);
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
		this.markPos = 0; 
		this.curPos = 0;
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
			Open();
		}
		int c;
		try {
			if(this.chaBuffer!=null)
			{
				String word = this.chaBuffer;
				this.chaBuffer = null;
				return word;
			}
			c = this.bufferedReader.read();
			this.curPos++;
		} catch (IOException e) {
			throw new FileException(e.getCause());
		}
		if(c == -1)
		{
			return null;		
		}
		return String.valueOf((char) c);
	}

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
	public  String Read(int beginIndex, int endIndex) throws FileException
	{
		if(this.bufferedReader==null)
		{
			Open();
		}
		pSet(beginIndex);
		StringBuilder text = new StringBuilder();
		for(int i=beginIndex;i<endIndex;i++)
		{
			String str = Read();
			if(str!=null)
			{
				text.append(str);
			}
			else
			{
				throw new FileException("Index out of  file size!");
			}
		}
		return text.toString();
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
	 *  		Read(3,5,ReadModel.Beg) is "ik"
	 *  		Read(3,5,ReadModel.Beg) is "ja"
	 *  
	 * @param beginIndex
	 * 					Integer offset of the first character related to the beginning of the document
	 * @param endIndex
	 * 					Integer offset+1 of the last character related to the beginning of the document
	 * @param readModel:
	 * 					Decide the related position of the read pointer. 
	 * 					ReadModel.Beg: beginning of the document
	 * 					ReadModel.Cur: current postion of the reader
	 * @return
	 * @throws FileException
	 */
	public  String Read(int beginIndex, int endIndex, ReadModel readModel) throws FileException
	{
		if(readModel==ReadModel.Beg)
		{
			return Read( beginIndex,  endIndex);
		}
		else
		{
			return Read( this.curPos+beginIndex,this.curPos+endIndex);
		}
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
			Open();
		}
		String word;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			while(true)
			{	
				word = Read();
				if(word!=null)
				{
					if(word.equals("\r"))
					{
						word = Read();
						if(word!=null)
						{
							if(! word.equals("\n"))
							{
								this.chaBuffer = word;
							}
						}
						break;
					}
					else if(word.equals("\n"))
					{
						break;
					}
					stringBuilder.append(word);
				}
				else
				{
					break;
				}
			}
		} catch (Exception e) {
			throw new FileException(e.getCause());
		}
		if(stringBuilder.length()==0)
		{
			return null;
		}
		return stringBuilder.toString();
	}

	/**
	 * read all of the file from current position as a string
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
	 * read all lines of the file from 
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
				
				while((line = ReadLine())!=null)
				{
					lines.add(line);	
				}
			} catch (Exception e) {
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
				markPos = curPos;
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
				curPos = markPos;
			} catch (IOException e) {
				throw new FileException(e.getCause());
			}
		}
	}
	
	/**
	 * return current position of the reader pointer
	 */
	public  Integer pTell() throws FileException
	{
		if(bufferedReader!=null)
		{
			return curPos;
		}
		return null;
		
	}
	
	/**
	 * reset the current position of the reader pointer to the given index
	 * @param index
	 * 				Destinate position of the reader pointer
	 */
	public  boolean pSet(Integer index) throws FileException
	{
		if(bufferedReader!=null)
		{
			try 
			{
				if(index>curPos)
				{
					this.bufferedReader.skip(index-curPos);
				}
				else if(index>markPos)
				{
					Reset();
					this.bufferedReader.skip(index-curPos);
				}
				else {
					Open();
					this.bufferedReader.skip(index);
				}
			} catch (IOException e) {
				throw new FileException(e.getCause());
			}
			curPos = index;
			return true;
		}
		return false;
	}
	
	
	public static void main(String args[]) throws Exception
	{
		
		LargeFileReader reader = new LargeFileReader("D:/Codes/Project/NLP/input/test.txt");
		String string = reader.Read(3,5);
		string = reader.Read(3,5,ReadModel.Cur);
		string = reader.ReadAll();
		List<String>lines = reader.ReadAllLine();
		reader.Close();
//		pml.file.reader.FileReader reader = new LargeFileReader("D:/Project/NLP/nlp/seg/SegFeature.java");
//		String text = reader.ReadAll();
		
	}
}