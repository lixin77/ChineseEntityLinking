package pml.scanner;

import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.tokensregex.MatchedExpression;
import pml.file.*;

public class MyFileScanner extends MyFile {
	
public Scanner scanner = null;
	
	/**
	 * open defined file and scan from the beginning.
	 * @param fileName
	 * 					The path of the file to be scanned.
	 * @throws Exception
	 */
	public MyFileScanner(String fileName) throws Exception 
	{
		super(fileName,FileModel.Open,FileAccess.Read);
		scanner  = new Scanner(super.bufferedReader);
		super.bufferedReader.read();
	}
	
	public MyFileScanner(String fileName,Charset charset) throws Exception 
	{
		super(fileName,FileModel.Open,FileAccess.Read,charset);
		scanner  = new Scanner(super.bufferedReader);
		super.bufferedReader.read();
	}
	
	/**
	 * read a byte from the input file stream
	 * @return by
	 * 					If there is a byte then return the value else return null;
	 * @throws Exception
	 */
	public Byte ReadByte() throws Exception
	{
		Byte by = null;
		if(scanner == null)
		{
			throw new Exception("No file opened for reading!");
		}
		if(scanner.hasNextByte())
		{
			by = scanner.nextByte();
		}
		return by;
	}
	
	/**
	 * read a Short from the input file stream
	 * @return by
	 * 					If there is a Short instance then return the value else return null;
	 * @throws Exception
	 */
	public Short ReadShort() throws Exception
	{
		Short st = null;
		if(scanner == null)
		{
			throw new Exception("No file opened for reading!");
		}
		if(scanner.hasNextShort())
		{
			st = scanner.nextShort();
		}
		return  st;
	}
	
	/**
	 *  read a Integer from the input file stream
	 * @return by
	 * 					If there is a Integer instance then return the value else return null;
	 * @throws Exception
	 */
	public Integer ReadInt() throws Exception
	{
		Integer it = null;
		if(scanner == null)
		{
			throw new Exception("No file opened for reading!");
		}
		if(scanner.hasNextInt())
		{
			it = scanner.nextInt();
		}
		return  it;
	}
	
	/**
	 *  read a Long from the input file stream
	 * @return lng
	 * 					If there is a Long instance then return the value else return null;
	 * @throws Exception
	 */
	public Long ReadLong() throws Exception
	{
		Long lng= null;
		if(scanner == null)
		{
			throw new Exception("No file opened for reading!");
		}
		if(scanner.hasNextLong())
		{
			lng = scanner.nextLong();
		}
		return  lng;
	}
	
	/**
	 *  read a Double from the input file stream
	 * @return doub
	 * 					If there is a Double instance then return the value else return null;
	 * @throws Exception
	 */
	public Double ReadDouble() throws Exception
	{
		Double doub= null;
		if(scanner == null)
		{
			throw new Exception("No file opened for reading!");
		}
		if(scanner.hasNextDouble())
		{
			doub = scanner.nextDouble();
		}
		return  doub;
	}
	
	public String ReadString()
	{
		if(scanner.hasNext(Pattern.compile("([\\p{L}\\p{Po}]+)(?=\\h)", Pattern.MULTILINE)))
		{
			String str = scanner.next(Pattern.compile("([\\p{L}\\p{Po}]+)(?=\\h)", Pattern.MULTILINE)).trim();
			return str;
		}	
		return null;
	}
	
	public static void main(String args[]) throws Exception
	{
		String string = "小明 硕士 毕业 于 中国科学院计算所 后 在 日本 京都大学 深造。 我 来自 江西 抚州 金溪，是个学生。 \r普金访问中国 ";
		Pattern pattern = Pattern.compile("([\\p{L}\\p{Po}]+)(?=\\h)");
		Matcher matcher = pattern.matcher(string);
		while(matcher.find()){
			System.out.print(matcher.group());
		}
		MyFileScanner myFileScanner = new MyFileScanner("./input/ChineseSample.txt");
		String str;
		for(int i = 0;i<10;i++)
		{
			 str = myFileScanner.ReadString();
		}
	}

}
