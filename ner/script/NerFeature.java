package script;


import java.io.*;
import java.nio.file.*;

import pml.file.*;

import msra.nlp.ner.ChineseNER;
import msra.nlp.ner.FormatNer;

public class NerFeature {

	public static void main(String[] args) throws Exception {
		// TODO 保留 type 信息
		Path featurePath = Paths.get("D:/Codes/Project/NLP/nlp_entitylink/Chinese_entity_linking_java/ChineseEntityLinking/data/featureSeged.seg");
		Path outputPath = Paths.get("D:/Codes/Project/NLP/nlp_entitylink/Chinese_entity_linking_java/ChineseEntityLinking/data/featureNered.ner");		
		MyFile reader= new MyFile(featurePath.toString(),FileModel.Open,FileAccess.Read);
		MyFile writer = new MyFile(outputPath.toString(),FileModel.Open,FileAccess.Write);
		String text;
		FormatNer formatNer = new FormatNer();
		
		while((text=reader.ReadLine())!=null)
		{
			writer.Write(formatNer.NerContext(text));
		}
	}

}
