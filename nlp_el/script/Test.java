package script;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Test {

	/**
	 * Test File input and output
	 * @throws IOException
	 */
	public static void fileInputTest() throws IOException
	{
		File file = new File("./data/KB.txt");
		long tBegin;
		long tEnd;
		/**
		 * Test fileInputStream  speed.
		 */
		/*tBegin = System.currentTimeMillis();
		FileInputStream fileInputStream = new FileInputStream(file);
		while(fileInputStream.read()!= -1)
		{
			continue;
		}
		fileInputStream.close();
		tEnd = System.currentTimeMillis();
		System.out.println("FileInputStream cost: "+(tEnd-tBegin)/1000.0+"s\n");*/
		/**
		 * Test BufferedInputStream  speed.
		 */
		tBegin = System.currentTimeMillis();
		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream,100000);
		while(bufferedInputStream.read()!= -1)
		{
			continue;
		}
		bufferedInputStream.close();
		fileInputStream.close();		
		tEnd = System.currentTimeMillis();
		System.out.println("BufferedInputStream cost: "+(tEnd-tBegin)/1000.0+"s\n");
		/**
		 * Test ByteArrayInputStream  speed.
		 */
		tBegin = System.currentTimeMillis();
		
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[100000]);
		while(byteArrayInputStream.read()!= -1)
		{
			continue;
		}
		tEnd = System.currentTimeMillis();
		System.out.println("byteArrayInputStream cost: "+(tEnd-tBegin)/1000.0+"s\n");
		/**
		 * Test bufferedReader  speed.
		 */
		tBegin = System.currentTimeMillis();
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file),100000);
		while(bufferedReader.read()!= -1)
		{
			continue;
		}
		tEnd = System.currentTimeMillis();
		System.out.println("bufferedReader cost: "+(tEnd-tBegin)/1000.0+"s\n");
		
		/**
		 * Test charArrayReader  speed.
		 */
		tBegin = System.currentTimeMillis();
		
		CharArrayReader charArrayReader = new CharArrayReader(new char[100000]);
		while(charArrayReader.read()!= -1)
		{
			continue;
		}
		tEnd = System.currentTimeMillis();
		System.out.println("charArrayReader cost: "+(tEnd-tBegin)/1000.0+"s\n");
		/**
		 * Test StringReader  speed.
		 */
		tBegin = System.currentTimeMillis();
		
		StringReader stringReader = new StringReader(new String());
		while(stringReader.read()!= -1)
		{
			continue;
		}
		tEnd = System.currentTimeMillis();
		stringReader.close();
		System.out.println("stringReader cost: "+(tEnd-tBegin)/1000.0+"s\n");	
		
	}


	public static void main(String args[])
	{
		String string = "，我来自——是个  中";
		//Pattern pattern = Pattern.compile("\\S\\p{sc=Han}+\\s"); // match chinese words but punctuations		
		//Pattern pattern = Pattern.compile("\\S[\\p{sc=Han} \\p{P}]+\\s"); // match chinese words and punctuations
		Pattern pattern = Pattern.compile("\\S[\\p{Lo}  \\p{P}]+\\s");
		Matcher matcher = pattern.matcher(string);
		if(matcher.find())
		{
			System.out.print(matcher.group());
		}
	}
}

