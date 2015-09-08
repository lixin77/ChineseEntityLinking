package pml.file.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import pml.file.FileException;
import pml.file.FileModel;
import pml.file.util.Util;

public class LargeFileWriter implements FileWriter 
{
	protected File file = null;
	protected BufferedWriter bufferedWriter = null;
	protected FileModel fileModel = FileModel.Open;
	
	/**
	 * size of buffer for read or write
	 */
	public static int bufferSize = 10000;
	/**
	 * threshold of file size. if the file size is below the threshold it will use Files.ReadAllines method otherwise the buffered method.
	 */
	protected long fileSizeThresh = 100;// 100M
	/**
	 * the character set used in the input file. Default is utf-8
	 */
	public Charset charset = StandardCharsets.UTF_8;
	
	/**
	 * create an empty instance
	 */
	public LargeFileWriter()
	{
		
	}
	
	/**
	 * appoint file path
	 * @param filePath
	 */
	public LargeFileWriter(String filePath)
	{
		file = new File(filePath);
	}
	
	/**
	 * appoint file path and file open model
	 * @param filePath
	 * @param fileModel
	 */
	public LargeFileWriter(String filePath,FileModel fileModel)
	{
		this.fileModel = fileModel;
		this.file = new File(filePath);
	}
	
	/**
	 * appoint file path and character set to be used for reading the file
	 * @param filePath
	 * @param charset
	 */
	public LargeFileWriter(String filePath,Charset charset)
	{
		this.charset = charset;
		this.file = new File(filePath);
	}
	
	/**  
	 * appoint file path,file open model and character set
	 * @param filePath
	 * @param fileModel
	 * @param charset
	 */
	public LargeFileWriter(String filePath,FileModel fileModel, Charset charset)
	{
		this.file = new File(filePath);
		this.fileModel = fileModel;
		this.charset = charset;
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
			throw new FileException("No file has been appointed for writing!");
		}
		if(!file.isFile())
		{
			if(fileModel == FileModel.OpenOrCreate)
			{
				if(Util.IsValidFilePath(file.toString()))
				{
					try {
						file.createNewFile();
					} catch (IOException e) {
						throw new FileException(e.getCause());
					}
				}
				else
				{
					throw new FileException(file.getAbsolutePath()+" is an invalid file path!");
				}
			}
			else
			{
				throw new FileException(file.getAbsolutePath()+" does not exist!");
			}
		}
		try {
			if(fileModel == FileModel.Append)
			{
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.toString(),true),this.charset),bufferSize);
			}
			else
			{
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.toString(),false),this.charset),bufferSize);
			}
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
		Open(filePath,null,null);
	}
	
	/**
	 * open file defined by given file path with given characer set
	 * @param filePath
	 * @param charset
	 * @throws IOException
	 */
	public void Open(String filePath, Charset charset) throws FileException
	{
		Open(filePath,null,charset);
	}
	
	/**
	 * open file defined by given file path and file model
	 * @param filePath
	 * @param fileModel
	 */
	public void  Open(String filePath,FileModel fileModel) 
	{
		Open(filePath,fileModel,null);
	}
	
	/**
	 * open file whoes path given by filePath with given fileModel(Open,OpenOrCreate,Append) and given charater set
	 * @param filePath
	 * @param fileModel
	 * @param charset
	 */
	public void Open(String filePath, FileModel fileModel, Charset charset)
	{
		if(fileModel!=null)
		{
			this.fileModel = fileModel;
		}
		if(charset!=null)
		{
			this.charset = charset;
		}
		Close();
		this.file = new File(filePath);
		Open();
	}

	/**
	 * Close the bufferReader.
	 * @throws IOException
	 */
	public void Close() throws FileException
	{
		if(bufferedWriter!= null)
		{
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				throw new FileException(e.getCause());
			}
		}
	}

	
	@Override
	public boolean Write(Object object) throws FileException
	{
		if(this.bufferedWriter==null)
		{
			Open();
		}
		try
		{
			bufferedWriter.write(String.valueOf(object));
		}
		catch(Exception exception)
		{
			return false;
		}
		return true;
	}

	
	@Override
	public boolean Writeln(String string) throws FileException
	{
		if(this.bufferedWriter==null)
		{
			Open();
		}
		try
		{
			if(Write(string))
			{
				this.bufferedWriter.newLine();
			}
		}
		catch(Exception exception)
		{
			return false;
		}
		return true;
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
		if(bufferedWriter!=null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
		
}
