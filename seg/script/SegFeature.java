package script;



import java.io.IOException;

import msra.nlp.seg.*;
import pml.file.FileException;
import pml.file.reader.FileReader;
import pml.file.reader.LargeFileReader;
import pml.file.writer.LargeFileWriter;

/**
 * This script read the feature file in which a line store an
 * entity's information: name namedEntities description link,
 * which seperated by tab, and segment each entity's description and store the segmented description into destinate file by line.
 * @author wangwenye
 *
 */
public class SegFeature {
	
	public static void main(String[] args) throws SegException
	{
		Batch();
	}
	
	public static void Batch()
	{
		Seg seg = new Segmenter();
		String inputPath = "./data/feature.txt";
		String desPath = "./output/featureSeged.seg";
		FileReader reader = new LargeFileReader(inputPath);
		pml.file.writer.FileWriter writer = new LargeFileWriter(desPath);
		
		String line;
		int i=0;
		while((line=reader.ReadLine())!=null)
		{
			String[] array = line.split("\t");
			String segedText = seg.SegText(array[2]);
			try {
				writer.Writeln(segedText);
			} catch (FileException e) {
				System.out.println(e.getMessage());
			}
			i++;
			if(i%1000==0)
			{
				System.out.println(String.format("Seg %d passage",i));
			}
		}
		reader.Close();
		writer.Close();
	}
}
