package pml.file.util;

import java.io.File;
import java.io.IOException;

import pml.file.FileException;

public class Util 
{
	/*
	 * 					File or directory Path checker
	 */
	
	/**
	 * check if the filePath if path of an existed file
	 * @param filePath
	 * @return
	 */
	public static boolean IsFile(String filePath)
	{
		File file = new File(filePath);
		return file.isFile();
	}
	
	/**
	 * check if the dirPath is path of an existed directory
	 * @param dirPath
	 * @return
	 */
	public static boolean IsDir(String dirPath)
	{
		File file = new File(dirPath);
		return file.isDirectory();
	}
	
	/**
	 * check if path is path of an existed file or direcotry
	 * @param path
	 * @return
	 */
	public static boolean IsExistPath(String path)
	{
		File file = new File(path);
		return file.exists();
	}

	/**
	 * check if "path" is a valid(exist or can be made) file path
	 * @param path
	 * @return
	 */
	public static boolean IsValidFilePath(String path)
	{
		File file = new File(path);
		if(file.exists())
		{
			if(file.isFile())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		try
		{
			if(file.createNewFile())
			{
				file.delete();
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * checi if "path" is a valid directory path
	 * @param path
	 * @return
	 */
	public static boolean IsValidDirPath(String path)
	{
		File file = new File(path);
		if(file.exists())
		{
			if(file.isDirectory())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		try
		{
			if(file.mkdir())
			{
				file.delete();
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	/*
	 * 					Extract file name from file path
	 */
	
	/**
	 * extract file name with suffix(default) from file path
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String Path2Name(String path) throws FileException
	{
		return Path2Name(path,true);
	}
	
	/**
	 * extract file name from file path with suffix or not
	 * @param path
	 * @param withSuffix
	 * @return
	 * @throws Exception
	 */
	public static String Path2Name(String path,boolean withSuffix) throws FileException
	{
		if((path.contains("\\")&& path.contains("/")) || (!path.contains("\\") && !path.contains("/")))
		{
			throw new FileException("Invalid format of file name");
		}
		if(path.contains("/"))
		{
			if(withSuffix)
			{
				return path.substring(path.lastIndexOf("/")+1);
			}
			else
			{
				try
				{
					return path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
				}
				catch(Exception exception)
				{
					return path.substring(path.lastIndexOf("/")+1);
				}
			}
		}
		else
		{
			if(withSuffix)
			{
				return path.substring(path.lastIndexOf("\\")+1);
			}
			else
			{
				try
				{
					return path.substring(path.lastIndexOf("\\")+1,path.lastIndexOf("."));
				}
				catch(Exception exception)
				{
					return path.substring(path.lastIndexOf("\\")+1);
				}
			}
		}
	}

	/*
	 * 					Make file or directory
	 */
	
	public static File MakeFile(String path) throws FileException
	{
		if(IsValidFilePath(path))
		{
			File file = new File(path);
			if(!file.exists())
			{
				try
				{
					file.createNewFile();
				}
				catch(IOException e)
				{
					throw new FileException(e.getCause());
				}
			}
			return file;
		}
		else
		{
			throw new FileException(path+" is not a valid file path!");
		}
	}
	
	public static File MakeDir(String path)
	{
		if(IsValidDirPath(path))
		{
			File file = new File(path);
			if(!file.exists())
			{
				file.mkdir();
			}
			return file;
		}
		else
		{
			throw new FileException(path+" is not a valid directory path!");
		}

	}
	
	/*
	 * 					Main function for a test
	 */
	
	public static void main(String args[])
	{
		String dir = "D:/Project/NLP/nlp/";
		File file = new File(dir);
		System.out.println(file.toString());
		
	}
}
