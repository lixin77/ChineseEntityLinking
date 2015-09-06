package script;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import msra.nlp.ner.FormatNer;
import pml.type.Tuple;
import pml.file.*;

public class WordIndex {
	static int  Inf = 65535;
	
	public static void main(String args[]) throws Exception
	{
		WordIndex.CountIndexInDir("./input/test/source_document/", "./input/test/source_document_with_index/");
	}

	public static List<Tuple<String,Integer>> CountIndex(String text)
	{
		List<Tuple<String, Integer>> list = new ArrayList();
		
		char[] array = text.toCharArray();
		int j=0;
		for(int i=0;i<array.length;i++)
		{
			Tuple wordIndex = new Tuple<>();
			String word = String.valueOf(array[i]);
			wordIndex.key =  word;
			if(!word.equals("\r") && !word.equals("\n") && !word.equals(" ") && !word.equals("\t"))
			{
				wordIndex.value = j++;
			}
			else
			{
				wordIndex.value = Inf;
			}
			list.add(wordIndex);
		}
		
		return list;
	}

	public static  List<Tuple<String,Integer>> CountIndexInFile(String fileName) throws Exception
	{
		MyFile myFile = new MyFile(fileName, FileModel.Open, FileAccess.Read);
		String text = myFile.ReadAll();
		myFile.Close();
		List<Tuple<String, Integer>> list = CountIndex(text);
		
		return list;
	}

	public static void  CountIndexInFile(String souce,String des) throws Exception
	{
		MyFile myFile = new MyFile(souce, FileModel.Open, FileAccess.Read);
		String text = myFile.ReadAll();
		myFile.Close();
		myFile = new MyFile(des, FileModel.OpenOrCreate, FileAccess.Write);
		List<Tuple<String, Integer>> list = CountIndex(text);
		for(int i=0;i<list.size();i++)
		{
			myFile.Write(list.get(i).key);
			if(list.get(i).value!=Inf)
			{
				myFile.Write("("+list.get(i).value+")");
			}
		}
		myFile.Close();
	}

	public static void CountIndexInDir(String sourceDir, String desDir) throws Exception
	{
		File src = new File(sourceDir);
		File des = new File(desDir);
		if(!src.isDirectory())
		{
			throw new Exception("Not a direcotry");
		}
		else
		{
			if(!des.exists())
			{
				des.mkdir();
			}
			// construct a file name filter to extract xml files
			FilenameFilter xmlFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".xml")) {
						return true;
					} else {
						return false;
					}
				}
			};
			File[] files = src.listFiles(xmlFilter);
			for(File f  : files)
			{
				CountIndexInFile(f.toString(),des.toString()+"\\"+FormatNer.GetNameFromPath(f.toPath())+".xml");
			}
		}
	}

	
}
