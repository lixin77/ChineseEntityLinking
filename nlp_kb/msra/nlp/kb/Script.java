package msra.nlp.kb;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import msra.nlp.edl.Query;
import msra.nlp.edl.QueryByStanford;
import msra.nlp.ner.Ner;
import msra.nlp.seg.Seg;
import pml.file.FileModel;
import pml.file.reader.FileReader;
import pml.file.reader.LargeFileReader;
import pml.file.util.Util;
import pml.file.writer.*;
import pml.file.writer.LargeFileWriter;
import pml.string.util.Format;
import pml.string.util.ZHConverter;

public class Script 
{
	public static Seg seg = null;
	public static Ner ner = null;
	
	public static Map<String, String> id2name = null; 
	//public static Map<String, List<String>> id2names = null;
	public static Map<String, String> id2type = null; 
	public static Map<String, String> id2text = null; 
	public static Map<String, List<String>> id2links = null; 
	
	/**
	 * extract chinese entity base from triple languages knoledge base
	 */
	public static void ExtractChineseBase()
	{	
		// directory in which freebase stores
		String sourceDir = "D:/Codes/Data/EDL_Data/LDC2015E42_TAC_KBP_2015_Tri-lingual_Entity_Discovery_and_Linking_Knowledge_Base/data/";
		String des = "../freebase/chinese_freebase/";
		
		if(Util.IsDir(sourceDir))
		{
			// construct a file-name filter to extract defined files
			FilenameFilter filter =new FilenameFilter() 
			{
				@Override
				public boolean accept(File dir, String name)
				{
					if((name.startsWith("name")|| name.startsWith("description")) && name.endsWith(".nt"))
					{
						return true;
					}
					return false;
				}
			};
			File src = new File(sourceDir);
			File[] files = src.listFiles(filter);
			FileReader reader = new LargeFileReader();
			FileWriter writer = new LargeFileWriter();
			String line;
			for(File file :files)
			{
				reader.Open(file.toString());
				writer.Open(des+"/"+Util.Path2Name(file.toString()),FileModel.OpenOrCreate);
				while((line = reader.ReadLine())!=null)
				{
					//TODO: decide this line include chinese entity description
					if(line.indexOf("/m.") != -1 &&(line.indexOf("@zh-Hant")!=-1 || line.indexOf("@zh")!=-1))
					{
						writer.Writeln(line);
					}
				}
			}
			reader.Close();
			writer.Close();
		}
		else
		{
			throw new KnowledgeBaseException(sourceDir.toLowerCase()+" does not exist!");
		}
	}
	
	//			Extract name by id
	public static void ExtractNameById()
	{
		String source = "../freebase/chinese_freebase/";
		String des = "../freebase/id2name.txt";
		
		File src = new File(source);
		if(!src.exists())
		{
			ExtractChineseBase();
		}
		else
		{
			FilenameFilter filter =new FilenameFilter() 
			{
				@Override
				public boolean accept(File dir, String name)
				{
					if(name.startsWith("name") && name.endsWith(".nt"))
					{
						return true;
					}
					return false;
				}
			};
			File[] files = src.listFiles();
			FileReader reader = new LargeFileReader();
			FileWriter writer = new LargeFileWriter(des,FileModel.OpenOrCreate);
			String line;
			String name;
			String id;
			int beginIndex = 0;
			int endIndex = 0;
			int watch = 0;
			id2name = new HashMap<>(400000);
			List<String> names;
			for(File file: files)
			{
				watch=0;
				reader.Open(file.toString());
				while((line = reader.ReadLine())!=null)
				{
					try
					{
						beginIndex = line.indexOf("/m.");
						endIndex = line.indexOf(">",beginIndex);
						id = line.substring(beginIndex+1,endIndex);
						beginIndex =line.indexOf("\"",endIndex);
						endIndex = line.indexOf("\"",beginIndex+1);
						name = line.substring(beginIndex+1, endIndex);
						name = Format.UnicodeEscapedSerial2Str(name);
						if(line.indexOf("@zh-",endIndex)!=-1)
						{
							name = Format.Zhf2Zhj(name);
						}
						id2name.put(id, name);
						/*if((names=id2names.get(id))!=null)
						{
							names.add(name);
							id2names.put(id,names);
						}
						else
						{
							names = new ArrayList<String>();
							names.add(name);
							id2names.put(id,names);
						}*/
					}
					catch(Exception e)
					{
						System.out.println("Something wrong happend in "+watch+" line(skipped) of "+source);
					}
					watch++;
				}
			}
			reader.Close();
			for(String key: id2name.keySet())
			{
				writer.Writeln(key+"\t"+id2name.get(key));
			}
			writer.Close();
		}
	}
	
	//			Extract type by id
	public static void ExtractTypeById()
	{
		LoadId2Name();
		LoadId2Name();
		String dir = "D:/Codes/Data/EDL_Data/LDC2015E42_TAC_KBP_2015_Tri-lingual_Entity_Discovery_and_Linking_Knowledge_Base/data/";
		String des = "../freebase/id2type.txt";
		File src = new File(dir);

		// construct a file name filter to extract xml files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.startsWith("a-m-") && lowercaseName.endsWith(".nt")) {
					return true;
				} else {
					return false;
				}
			}
		};
		File[] files = src.listFiles(filter);
		FileReader reader;
		FileWriter writer = new LargeFileWriter(des,FileModel.OpenOrCreate);
		String line;

		for(File file : files)
		{
			reader = new LargeFileReader(file.toString());
			while((line=reader.ReadLine())!=null)
			{
				if(line.indexOf("/m.")==-1)
				{
					continue;
				}
				String type = ExtractEntityType(line);
				String entity_id = ExtractEntityID(line);
				if(IsChineseEntity(entity_id))
				{
					if(IsPER(type))
					{
						writer.Writeln(entity_id+"\t"+"PER");
					}
					else if(IsORG(type))
					{
						writer.Writeln(entity_id+"\t"+"ORG");
					}
					else if(IsGPE(type))
					{
						writer.Writeln(entity_id+"\t"+"GPE");
					}
				}
			}
			reader.Close();
		}
		writer.Close();

	}

	private static boolean IsChineseEntity(String id)
	{
		return id2name.containsKey(id);
	}
	private static boolean IsPER(String type)
	{
		String[] keys = new String[]{"person","people","politician","actor","lawyer","author","athlete","monarch","academic","inventor"};
		for(String key :keys)
		{
			if(type.contains(key))
			{
				return true;
			}
		}
		return false;
	}
	private static boolean IsORG(String type)
	{
		String[] keys = new String[]{"organization","university","school","operation","company","government"};
		for(String key :keys)
		{
			if(type.contains(key))
			{
				return true;
			}
		}
		return false;
	}
	private static boolean IsGPE(String type)
	{
		String[] keys = new String[]{"country","province","city","location","continent"};
		for(String key :keys)
		{
			if(type.contains(key))
			{
				return true;
			}
		}
		return false;
	}
	private static boolean IsLOC(String type)
	{
		return true;
	}
	
	private static String ExtractEntityID(String line)
	{
		String entityID = line.substring(line.indexOf("/m.")+1,line.indexOf(">"));
		return entityID;
	}

	private static String ExtractEntityType(String line)
	{
		String type = line.substring(line.lastIndexOf("/")+1,line.lastIndexOf(">"));
		return type;
	}

	//			Extract text by id
	//TODO: decide the file or directory path and filter format
	public static void ExtractTextById()
	{
		LoadId2Name();
		String dir = "../freebase/chinese_freebase";
		File src = new File(dir);
		String des = "../freebase/id2text.txt";

		// construct a file name filter to extract files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.startsWith("description") && lowercaseName.endsWith(".nt")) {
					return true;
				} else {
					return false;
				}
			}
		};
		File[] files = src.listFiles(filter);
		FileReader reader = new LargeFileReader();
		FileWriter writer = new LargeFileWriter(des,FileModel.OpenOrCreate);

		String line;
		String text;
		String id;
		int beginIndex = 0;
		int endIndex = 0;
		int watch = 0;

		for(File file : files)
		{
			reader.Open(file.toString());
			watch=0;
			while((line = reader.ReadLine())!=null)
			{
				if(line.indexOf("/m.") != -1 &&(line.indexOf("@zh")!=-1))
				{
					try
					{
						beginIndex = line.indexOf("/m.");
						endIndex = line.indexOf(">",beginIndex);
						id = line.substring(beginIndex+1,endIndex);
						if(!id2name.containsKey(id))
						{
							continue;
						}
						beginIndex =line.indexOf("\"", endIndex);
						endIndex = line.lastIndexOf("\"");
						text = line.substring(beginIndex+1,endIndex);
						text = Format.UnicodeEscapedSerial2Str(text);
						if(line.indexOf("@zh-",endIndex)!=-1)
						{
							text = Format.Zhf2Zhj(text);
						}
						writer.Write(id+"\t");
						writer.Writeln(text);
					}
					catch(Exception e)
					{
						System.out.println("Something wrong happend in "+watch+" line(skipped) of "+file.toString());
					}
					watch++;
				}
			}
		}
	}
	
	//			Extract external id by id
	//TODO: decide the file or directory path and filter format	
	public static void ExtractLinkById()
	{
		LoadId2Name();
		String dir = "D:/Codes/Data/EDL_Data/LDC2015E42_TAC_KBP_2015_Tri-lingual_Entity_Discovery_and_Linking_Knowledge_Base/data/";
		File src = new File(dir);
		String des = "../freebase/id2link.txt";

		// construct a file name filter to extract files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.startsWith("link") && lowercaseName.endsWith(".nt")) {
					return true;
				} else {
					return false;
				}
			}
		};
		File[] files = src.listFiles(filter);
		FileReader reader= new LargeFileReader();
		FileWriter writer = new LargeFileWriter(des,FileModel.OpenOrCreate);

		String line;
		String id;
		String link;
		int beginIndex = 0;
		int endIndex = 0;
		int watch = 0;

		for(File file : files)
		{
			watch=0;
			reader.Open(file.toString());
			while((line = reader.ReadLine())!=null)
			{
				if(line.lastIndexOf("/m.")!=-1 && line.indexOf("/g.")==-1)
				{
					try
					{
						//TODO: Decide regular to extract external id
						beginIndex = line.indexOf("/m.");
						endIndex = line.indexOf(">",beginIndex);
						id = line.substring(beginIndex+1,endIndex);
						if(!IsChineseEntity(id))
						{
							continue;
						}
						endIndex = line.lastIndexOf(">");
						beginIndex =line.lastIndexOf("/m.",endIndex-1);
						link= line.substring(beginIndex+1, endIndex);
						if(!IsChineseEntity(link))
						{
							continue;
						}
						writer.Write(id+"\t");
						writer.Writeln(link);
					}
					catch(Exception e)
					{
						System.out.println("Something wrong happend in "+watch+" line(skipped) of "+file.toString());
					}
					watch++;
				}
			}
		}
		reader.Close();
		writer.Close();
	}

	//			Merge entity information: id name type text(description) link
	public static void Merge()
	{
		LoadId2Name();
		LoadId2Type();
		LoadId2Text();
		LoadId2Link();
		String source = "../freebase/entity.txt";
		FileWriter writer = new LargeFileWriter(source,FileModel.OpenOrCreate);
		for(String id:id2name.keySet())
		{
			writer.Writeln(id+"\t"+id2name.get(id)+"\t"+id2type.get(id)+"\t"+id2text.get(id)+"\t"+String.join(" ", id2links.get(id)));
		}
		writer.Close();
	}
	
	
	private static void LoadId2Name()
	{
		if(id2name==null)
		{
			id2name = new HashMap<>(400000);
			String source = "../freebase/id2name.txt";

			File src = new File(source);
			if(!src.exists())
			{
				ExtractNameById();
			}
			else
			{
				FileReader reader = new LargeFileReader(source);
				String line;
				String id;
				String name;
				String[] str;
				int watch = 0;
				while((line=reader.ReadLine())!=null)
				{
					try 
					{
						str = line.split("\t");
						id = str[0];
						name = str[1];
						id2name.put(id, name);
					} 
					catch (Exception e) {
						System.out.println("Something wrong happend in "+watch+" line(skipped) of "+source);
					}
					watch++;
				}
				reader.Close();
			}
		}
	}

	private static void LoadId2Type()
	{
		if(id2type==null)
		{
			id2type = new HashMap<>(400000);
			String source = "../freebase/id2type.txt";

			File src = new File(source);
			if(!src.exists())
			{
				ExtractTypeById();
			}
			else
			{
				FileReader reader = new LargeFileReader(source);
				String line;
				String id;
				String name;
				String[] str;
				int watch = 0;
				while((line=reader.ReadLine())!=null)
				{
					try 
					{
						str = line.split("\t");
						id = str[0];
						name = str[1];
						id2type.put(id, name);
					} 
					catch (Exception e) {
						System.out.println("Something wrong happend in "+watch+" line(skipped) of "+source);
					}
					watch++;
				}
				reader.Close();
			}
		}
	}

	private static void LoadId2Text()
	{
		if(id2text==null)
		{
			id2text = new HashMap<String, String>(400000);
			String source = "../freebase/id2text.txt";

			File src = new File(source);
			if(!src.exists())
			{
				ExtractTextById();
			}
			else
			{
				FileReader reader = new LargeFileReader(source);
				String line;
				String id;
				String name;
				String[] str;
				int watch = 0;
				while((line=reader.ReadLine())!=null)
				{
					try 
					{
						str = line.split("\t");
						id = str[0];
						name = str[1];
						id2text.put(id, name);
					} 
					catch (Exception e) {
						System.out.println("Something wrong happend in "+watch+" line(skipped) of "+source);
					}
					watch++;
				}
				reader.Close();
			}
		}
	}

	private static void LoadId2Link()
	{
		if(id2links==null)
		{
			id2links = new HashMap<>(400000);
			String source = "../freebase/id2link.txt";

			File src = new File(source);
			if(!src.exists())
			{
				ExtractLinkById();
			}
			else
			{
				FileReader reader = new LargeFileReader(source);
				String line;
				String id;
				String name;
				List<String>list; 
				int watch = 0;
				while((line=reader.ReadLine())!=null)
				{
					try 
					{
						list = Arrays.asList(line.split("\t"));
						id = list.get(0);
						id2links.put(id, list.subList(1, list.size()));
					} 
					catch (Exception e) {
						System.out.println("Something wrong happend in "+watch+" line(skipped) of "+source);
					}
					watch++;
				}
				reader.Close();
			}
		}
	}

	//			Extract ordered and seperated entity information:
	
	//TODO: decide the file path
	public static void ExtractInfoByOrder()
	{
		String source = "../freebase/entity.txt";
		String des0 = "../freebase/id.txt";
		String des1 = "../freebase/name.txt";
		String des2 = "../freebase/type.txt";
		String des3 = "../freebase/text.txt";
		String des4 = "../freebase/link.txt";
		
		File src = new File(source);
		if(!src.exists())
		{
			Merge();
		}
		else
		{
			FileReader reader = new LargeFileReader(source);
			FileWriter writer0 = new LargeFileWriter(des0,FileModel.OpenOrCreate);
			FileWriter writer1 = new LargeFileWriter(des1,FileModel.OpenOrCreate);
			FileWriter writer2 = new LargeFileWriter(des2,FileModel.OpenOrCreate);
			FileWriter writer3 = new LargeFileWriter(des3,FileModel.OpenOrCreate);
			FileWriter writer4 = new LargeFileWriter(des4,FileModel.OpenOrCreate);
			String line;
			String name;
			int beginIndex = 0;
			int endIndex = 0;
			int watch = 0;
			String[] str;
			while((line = reader.ReadLine())!=null)
			{
				try
				{
					str = line.split("\t");
					writer0.Writeln(str[0]);	// id
					writer1.Writeln(str[1]);	// name
					writer2.Writeln(str[2]);	// type
					writer3.Writeln(str[3]);	// text
					writer4.Writeln(str[4]);	// link
					
				}
				catch(Exception e)
				{
					System.out.println("Something wrong happend in "+watch+" line(skipped)");	
				}
			}
			reader.Close();
			writer0.Close();
			writer1.Close();
			writer2.Close();
			writer3.Close();
			writer4.Close();
		}
	}

	//			Seg and Ner knoledge base and constructed infomation

	private static List<String> ids = null; 
	

	public static void ExtractMentionById()
	{

		Query query = new QueryByStanford();
		
		String source = "../freebase/id2text.txt";
		String des = "../freebase/namedEntity.txt";
		String des2 = "../freebase/entityNerInfo.xml";
		
		File src = new File(source);
		if(!src.exists())
		{
			ExtractInfoByOrder();
		}
		else
		{
			
			FileReader reader = new LargeFileReader(source);
			FileWriter writer = new LargeFileWriter(des);
			FileWriter writer2 = new LargeFileWriter(des2);
			String line;
			int index = 0;
			String[] str;
			while((line=reader.ReadLine())!=null)
			{
				str = line.split("\t");
				List<Map<String, String>> queries = (List<Map<String, String>>) query.QueryText(str[1], "");
				writer.Write(str[0]);
				for(Map<String, String> map : queries)
				{
					writer.Write(map.get("names")+" ");
					writer2.Writeln("<mention>");
					writer2.Writeln("\t<entity_id>"+str[0]+"</entity_id>");
					writer2.Writeln("\t<name>"+map.get("name")+"<name>");
					writer2.Writeln("\t<type>"+map.get("type")+"<type>");
					writer2.Writeln("\t<begin>"+map.get("begin")+"</begin>");
					writer2.Writeln("\t<end>"+map.get("end")+"</end>");
					writer2.Writeln("</mention>");
				}
				writer.Write("\r");
			}
			reader.Close();
			writer.Close();
			writer2.Close();
		}

	}
	
	public static void ExtractMentionByOrder()
	{
		Query query = new QueryByStanford();
		
		String source = "text.txt";
		String des = "namedEntity.txt";
		String des2 = "entityNerInfo.xml";
		
		File src = new File(source);
		if(!src.exists())
		{
			ExtractInfoByOrder();
		}
		else
		{
			
			FileReader reader = new LargeFileReader(source);
			FileWriter writer = new LargeFileWriter(des);
			FileWriter writer2 = new LargeFileWriter(des2);
			String line;
			int index = 0;
			while((line=reader.ReadLine())!=null)
			{
				List<Map<String, String>> queries = (List<Map<String, String>>) query.QueryText(line, "");
				for(Map<String, String> map : queries)
				{
					writer.Write(map.get("names")+"\t");
					writer2.Writeln("<mention>");
					writer2.Writeln("\t<id>"+ids.get(index++)+"</id>");
					writer2.Writeln("\t<name>"+map.get("name")+"<name>");
					writer2.Writeln("\t<type>"+map.get("type")+"<type>");
					writer2.Writeln("\t<begin>"+map.get("begin")+"</begin>");
					writer2.Writeln("\t<end>"+map.get("end")+"</end>");
					writer2.Writeln("</mention>");
				}
				writer.Write("\r");
			}
			reader.Close();
			writer.Close();
			writer2.Close();
		}
	}

	private static void LoadId()
	{
		String source = "id.txt"; 
		FileReader reader = new LargeFileReader(source);
		String id;
		while((id = reader.ReadLine())!=null)
		{
			Script.ids.add(id);
		}
		reader.Close();
	}
	

	public static void main(String args[])
	{

//		String string= "楓葉";
//		System.out.println(ZHConverter.convert(string,1));
//		String string = "\0x57CE\0x821\0x8C37";
//		System.out.println(Unicode2UTF8(string));
//		ExtractChineseBase();
//		ExtractNameById();
//		ExtractTypeById();
//		ExtractTextById();
		ExtractLinkById();
//		Merge();
//		ExtractInfoByOrder();
	}
}
