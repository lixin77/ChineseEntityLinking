package msra.nlp.el;


import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class LoadKB {
	public static Map<String,Integer> wordFreqMap=new HashMap<>(400000);
	public static List<String> titles = new ArrayList<String>(400000);
	public static List<String> namedEntities = new ArrayList<String>(400000);
	public static List<String> descriptions = new ArrayList<String>(400000);
	public static List<String> links = new ArrayList<String>();
	public static List<String[]> featureList = new ArrayList<>(400000); 
	private static String words = "";
	private static List<String> wordList= new ArrayList<>(400000);
	
	public static List<String> id = new ArrayList<>(); 
	
	public LoadKB(String...path) throws IOException {
		long tBegin = System.currentTimeMillis();
		System.out.print("loading KB...\r");	
		String KBPath = new String();
		if(path.length>0)
		{
			KBPath = path[0];
		}
		if(titles.isEmpty())
		{
			if(!KBPath.isEmpty())
			{
				ReadKB(KBPath);
			}
			else {
				ReadKB();
			}
		}
		System.out.print("Done!");
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tBegin;
		double elapsedSeconds = tDelta / 1000.0;
		System.out.println("Time elapsed(seconds): "+elapsedSeconds+"s.");
	}
	
	public void ReadKB() throws IOException
	{
		Path path = Paths.get("./data/title.txt");
		File file = path.toFile();
		if(!file.exists())
		{
			ReadKB("./data/KB.txt");
		}
		else
		{				
			String basedir = "./data/";
			ReadSepKB(basedir+"title.txt",basedir+"namedEntity.txt",basedir+"description.txt",basedir+"link.txt");
		}
	}
	public void ReadKB(String KBPath) throws IOException
	{
		Path pathOfKB = Paths.get(KBPath);
	
		BufferedReader KBReader;
		List<String> list = new ArrayList<>();; 		
		String[] str;
		long count = 0;
				
		// Count time elapsed
		long tStart = System.currentTimeMillis();
		List<String> lines = Files.readAllLines(pathOfKB,StandardCharsets.UTF_8);
		for(String line : lines)
		{
			if(line.length()>0)
			{				
				str = line.split("\t");
				titles.add(str[0]);
				namedEntities.add(str[1]);
				descriptions.add(str[2]);
				links.add(str[3]);
				featureList.add(str);
				wordList.add(str[1]);
				count++;
				if(count%5000==0)
				{
					System.out.println(String.format("Load %d passages",count));
				}
			}
		}		
		words = String.join(" ", wordList);
		String[] wordArray = words.split(" ");
		for(String word : wordArray)
		{
			Integer freq=wordFreqMap.get(word);
			if(freq==null)
			{
				wordFreqMap.put(word,1);
			}
			else
			{
				wordFreqMap.put(word,freq+1);
			}
		}
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		System.out.println("Time elapsed(seconds): "+elapsedSeconds+"\ti.e "+elapsedSeconds/60+" minute.");
	}
	
	public void ReadSepKB(Path titlePath,Path namedEntityPath,Path descriptionPath,Path linkPath) throws IOException
	{
		
		titles = Files.readAllLines(titlePath,StandardCharsets.UTF_8);
		namedEntities = Files.readAllLines(namedEntityPath,StandardCharsets.UTF_8);
		descriptions = Files.readAllLines(descriptionPath,StandardCharsets.UTF_8);
		links = Files.readAllLines(linkPath,StandardCharsets.UTF_8);
		
		if(titles.size()==links.size() && links.size()==namedEntities.size() && links.size() == descriptions.size())
		{

						
			for(int i=0;i<titles.size();i++)
			{	
				String[] str = new String[4];
				str[0] = titles.get(i);
				str[1] = namedEntities.get(i);
				str[2] = descriptions.get(i);
				str[3] = links.get(i);
				featureList.add(str);
				// The following argument is time consuming.
				wordList.add(str[1]);				
			}
		}
		else
		{
			throw new Error("File has been disrupted");
		}
		// Get word frequency map	
		words = String.join(" ", wordList);
		String[] wordArray = words.split(" ");
		for(String word : wordArray)
		{
			Integer freq=wordFreqMap.get(word);
			if(freq==null)
			{
				wordFreqMap.put(word,1);
			}
			else
			{
				wordFreqMap.put(word,freq+1);
			}
		}
	}
	public void ReadSepKB(String titlePath,String namedEntityPath,String descriptionPath,String linkPath) throws IOException
	{
		Path path1 = Paths.get(titlePath);
		Path path2 = Paths.get(namedEntityPath);
		Path path3 = Paths.get(descriptionPath);
		Path path4 = Paths.get(linkPath);
		ReadSepKB(path1, path2, path3, path4);
		
	}
	
	public static void main(String args[]) throws IOException
	{
		@SuppressWarnings("unused")
		LoadKB KB = new LoadKB();
	}
}

