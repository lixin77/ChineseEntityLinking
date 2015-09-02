package pml.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyFile {
	
	public File file = null;
	public BufferedReader bufferedReader = null;
	public BufferedWriter bufferedWriter = null;
	private FileModel fileModel = FileModel.Open;
	private FileAccess fileAccess = FileAccess.Read;	
	/**
	 * size of buffer for read or write
	 */
	protected int bufferSize = 10000;
	/**
	 * threshold of file size. if the file size is below the threshold it will use Files.ReadAllines method otherwise the buffered method.
	 */
	protected long fileSizeThresh = 100;// 100M
	/**
	 * the character set used in the input file. Default is utf-8
	 */
	public Charset charset = StandardCharsets.UTF_8;
	
	/**
	 * create a file object
	 */
	public MyFile()
	{
		
	}
	
	/**
	 *  open a file with defined model and access and character set
	 * @param fileName
	 * 					The path of the file to be operated.
	 * @param fileModel
	 * 					A enum type defining the Operating model to the file
	 * 					Append: write at the end of the file
	 * 					Open: open a file, if file not exists throw a FileNotFound exception
	 * 					OpenOrCreate: open a file, if file not exists, create a new file
	 * @param fileAccess
	 * 					A enum type defining the access to the file;
	 * 					Read: read the file
	 * 					Write: write into the file
	 * 
	 * @throws Exception 
	 */
	public MyFile(String fileName, FileModel fileModel, FileAccess fileAccess) throws Exception
	{
		Open(fileName,fileModel,fileAccess);
	}

	/**
	 *  open a file with defined model and access and character set
	 * @param fileName
	 * 					The path of the file to be operated.
	 * @param fileModel
	 * 					A enum type defining the Operating model to the file
	 * 					Append: write at the end of the file
	 * 					Open: open a file, if file not exists throw a FileNotFound exception
	 * 					OpenOrCreate: open a file, if file not exists, create a new file
	 * @param fileAccess
	 * 					A enum type defining the access to the file;
	 * 					Read: read the file
	 * 					Write: write into the file
	 * @param charset
	 * 					The character set used to read file;
	 * @throws Exception 
	 */
	public MyFile(String fileName, FileModel fileModel, FileAccess fileAccess, Charset charset) throws Exception
	{
		this.charset = charset;
		Open(fileName,fileModel,fileAccess);
	}

	/**
	 * set buffer size of the bufferdReader or bufferedWriter
	 * @param size
	 * 					The buffer size
	 */
	public void BufferSize(int size)
	{
		this.bufferSize = size;
	}
	
	/**
	 * set character set for the file reading or writing
	 * @param charset
	 * 					The character set to be used
	 */
	public void SetCharset(Charset charset)
	{
		this.charset = charset;
	}
	
	/**
	 * open a file with default read access.
	 * @param fileName
	 * @throws Exception 
	 */
	public void Open(String fileName) throws Exception
	{	
		File file = new File(fileName);
		if(!file.isFile())
		{
			throw new Exception("Is not a file!");
		}
		bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),this.charset),this.bufferSize);
	}
	
	/**
	 *  append, open, open or create a file to read or write
	 * @param fileName
	 * 					The path of the file to be operated.
	 * @param fileModel
	 * 					A enum type defining the Operating model to the file
	 * 					Append: write at the end of the file
	 * 					Open: open a file, if file not exists throw a FileNotFound exception
	 * 					OpenOrCreate: open a file, if file not exists, create a new file
	 * @param fileAccess
	 * 					A enum type defining the access to the file;
	 * 					Read: read the file
	 * 					Write: write into the file
	 * @throws Exception 
	 */	
	public void Open(String fileName,FileModel fileModel, FileAccess fileAccess) throws Exception
	{
		file = new File(fileName);
		switch(fileModel)
		{
		case Append: // Write to the end of file
			if(!file.isFile())
			{
				throw new Exception("Is not a file!");
			}
			if(fileAccess == FileAccess.Write)
			{
				bufferedWriter = new BufferedWriter(new FileWriter(fileName,true),this.bufferSize);
			}
			else {
				throw new Exception("Invalid Command!");
			}
			break;
		case Open: // Open a file and create a write or reader according to the access menthod
			if(!file.isFile())
			{
				throw new Exception("Is not a file!");
			}
			if(!file.exists())
			{
				throw new FileNotFoundException(file.toString());
			}
			if(fileAccess == FileAccess.Read)
			{
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),this.charset),this.bufferSize);
			}
			else if(fileAccess == FileAccess.Write)
			{
				bufferedWriter = new BufferedWriter(new FileWriter(fileName,false),this.bufferSize);
			}
			break;
		case OpenOrCreate: // open a file, if file not exists, create a new file
			if(!file.exists())
			{
				file.createNewFile();
			}
			if(fileAccess == FileAccess.Read)
			{
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),this.charset),this.bufferSize);
			}
			else if(fileAccess == FileAccess.Write)
			{
				bufferedWriter = new BufferedWriter(new FileWriter(fileName,false),this.bufferSize);
			}
			break;
		default:
			throw new Exception("Invalid Command!");
		}
	}
	
	public void Close() throws IOException
	{
		if(bufferedReader != null)
		{
			bufferedReader.close();
		}
		if(bufferedWriter !=null)
		{
			bufferedWriter.close();
		}
	}
	
	/**
	 * write limited object into file
	 * @param object
	 * 					The object to be writen into the file, the types of the object are limited to:
	 * 					byte, char, short, int, long, float, double, String or other types with override toString functions
	 * @throws Exception 
	 */
	public void Write(Object object) throws Exception
	{
		if(this.bufferedWriter==null)
		{
			throw new Exception("No File Opened For Writing!");
		}
		bufferedWriter.write(String.valueOf(object));
	}
	
	/**
	 * write a line of string into file
	 * @param string
	 * 					The String without line terminal
	 * @throws Exception - If no file has been opened for writing.
	 */
	public void Writeln(String string) throws Exception
	{
		if(this.bufferedWriter==null)
		{
			throw new Exception("No File Opened For Writing!");
		}
		bufferedWriter.write(string+"\r");
	}

	/**
	 * read a character and turn it into a string type
	 * @return
	 * 				A string type of character.
	 * @throws Exception - If no file opened for reading or file reached the end.
	 */
	public String Read() throws Exception
	{
		if(this.bufferedReader==null)
		{
			throw new Exception("No File Opened For Reading!");
		}
		int c = this.bufferedReader.read();
		if(c == -1)
		{
			return null;		
		}
		return String.valueOf((char) c);
	}
	
	/**
	 * read a line of the file and return a string without line terminal.
	 * @return
	 * @throws Exception 
	 */
	public String ReadLine() throws Exception
	{
		if(this.bufferedReader==null)
		{
			throw new Exception("No File Opened For Reading!");
		}
		String line = this.bufferedReader.readLine();
		return line;
	}

	/**
	 * read all of the file as a string
	 * @return
	 * @throws Exception 
	 */
	public String ReadAll() throws Exception
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
	public List<String> ReadAllLine() throws IOException
	{
		List<String> lines = new ArrayList<>(); 
		String line;
		if(this.file.length()< fileSizeThresh*1024*1024) // 100M Byte
		{
			lines = Files.readAllLines(file.toPath(),this.charset);			
		}
		else
		{
			while((line = bufferedReader.readLine())!=null)
			{
					lines.add(line);			
			}
		}
		return lines;
	}
	
	
	public boolean IsPathValid()
	{
		return false;
	}
	
	public boolean IsFile()
	{
		return false;
	}
	
	public boolean IsDir()
	{
		return false;
	}

	public static void main(String args[]) throws Exception
	{
		MyFile myFile = new MyFile("./input/ChineseSample.txt",FileModel.Open,FileAccess.Read);
		myFile.bufferedReader.mark(1000);
		myFile.Read();
		myFile.bufferedReader.mark(1000);
		myFile.bufferedReader.reset();
		String str;
		for(int i=0;i<10;i++)
		{
			 str = myFile.Read();
		}
/*		Byte by = myFile.ReadByte();
		Integer it = myFile.ReadInt();
		Long l = myFile.ReadLong();*/
		myFile.bufferedReader.reset();
		String line = myFile.ReadLine();
		myFile.bufferedReader.reset();
		List<String> lines = myFile.ReadAllLine();
		myFile.bufferedReader.reset();
		String all = myFile.ReadAll();
		MyFile file = new MyFile("./input/test.txt",FileModel.OpenOrCreate,FileAccess.Write);
		file.Write(all);
		file.Close();
	}
}



