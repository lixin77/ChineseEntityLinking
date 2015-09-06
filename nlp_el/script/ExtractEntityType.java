package script;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.type.NullType;
import javax.print.attribute.HashAttributeSet;

import msra.nlp.el.EntityLink;
import msra.nlp.ner.ChineseNER;
import pml.file.FileAccess;
import pml.file.FileModel;
import pml.file.MyFile;

public class ExtractEntityType {

	public static Set<String> idSet = new TreeSet<>();
	
	public static void main(String[] args) throws Exception {
		LoadChineseID();
		ExtractType();
		/*ChineseNER ner = new ChineseNER();
		
		MyFile myFile = new MyFile("D:/Codes/Project/NLP/nlp_entitylink/Chinese_entity_linking_java/ChineseEntityLinking/data/title.txt",FileModel.Open,FileAccess.Read);

		MyFile myFile2 = new MyFile("D:/Codes/Project/NLP/nlp_entitylink/Chinese_entity_linking_java/ChineseEntityLinking/data/featureSeged.seg",FileModel.Open,FileAccess.Read);
		
		MyFile myFile3 = new MyFile("D:/Codes/Project/NLP/nlp_entitylink/Chinese_entity_linking_java/ChineseEntityLinking/data/type.txt",FileModel.OpenOrCreate,FileAccess.Write);
		
		String title="";
		String description ="";
		List<Map<String, String>> maps; 
		String type;
		int i=0;
		while((title=myFile.ReadLine())!=null)
		{
			description = myFile2.ReadLine();
			maps = ner.NerText(description);
			if(EntityLink.ScoreWords(maps.get(0).get("Value"),title)>0.5)
			{
				type = maps.get(0).get("Answer");
			}
			else
			{
				type="unknow";
			}
			myFile3.Writeln(type);
		}
		myFile.Close();
		myFile2.Close();
		myFile3.Close();*/
	}

	public static void ExtractType() throws Exception
	{
		

		String dir = "D:/Codes/Data/EDL_Data/LDC2015E42_TAC_KBP_2015_Tri-lingual_Entity_Discovery_and_Linking_Knowledge_Base/data/";
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
		MyFile myFile;
		MyFile myFile2 = new MyFile("D:/Codes/Project/NLP/nlp_entitylink/Chinese_entity_linking_java/ChineseEntityLinking/data/type.txt",FileModel.OpenOrCreate,FileAccess.Write);
		String line;
		
		for(File file : files)
		{
			myFile = new MyFile(file.toString(),FileModel.Open,FileAccess.Read);
			while((line=myFile.ReadLine())!=null)
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
						myFile2.Writeln(entity_id+"\t"+"PER");
					}
					else if(IsORG(type))
					{
						myFile2.Writeln(entity_id+"\t"+"ORG");
					}
					else if(IsGPE(type))
					{
						myFile2.Writeln(entity_id+"\t"+"GPE");
					}
				}
			}
			myFile.Close();
		}
	}
	
	public static void  LoadChineseID() throws Exception
	{
		MyFile myFile = new MyFile("./data/id.txt",FileModel.Open,FileAccess.Read);
		String id;
		while((id=myFile.ReadLine())!=null)
		{
			idSet.add(id);
		}
		myFile.Close();
	}
	
	public static boolean IsChineseEntity(String id)
	{
		return idSet.contains(id);
	}
	
	public static boolean IsPER(String type)
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
	public static boolean IsORG(String type)
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
	public static boolean IsGPE(String type)
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
	
	public static String ExtractEntityID(String line)
	{
		String entityID = line.substring(line.indexOf("/m.")+1,line.indexOf(">"));
		return entityID;
	}
	 
	public static String ExtractEntityType(String line)
	{
		String type = line.substring(line.lastIndexOf("/")+1,line.lastIndexOf(">"));
		return type;
	}

	public static void ExtractChineseEntityID() throws Exception
	{
		MyFile myFile = new MyFile("./data/link.txt",FileModel.Open,FileAccess.Read);
		MyFile myFile2 = new MyFile("./data/id.txt",FileModel.OpenOrCreate,FileAccess.Write);
		String line;
		while((line=myFile.ReadLine())!=null)
		{
			myFile2.Writeln(line.substring(line.lastIndexOf("/m.")+1));
		}
		myFile.Close();
		myFile2.Close();
	}
}
