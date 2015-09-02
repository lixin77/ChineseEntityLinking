package msra.nlp.seg;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;


// import user model
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import pml.file.*;


/**
 * @author Minlong Peng
 *
 */
public class Segmenter implements Seg 
{
	
	/**
	 * For the use of segmenter you should invoke  msra.nlp.seg.segmenter.initClass() first
	 * This function initial the propertities of the segmenter like the path of chinese dictionary and classifier.
	 * Then segment method is availabe. This function take a string type input passage which contain the passage wanna to
	 * be splitted and return a string that is the splitted passage.
	 *   
	 */
	// Set directory of data
	private static final String basedir = "D:/Codes/Project/NLP/nlp_segmenter/stanford-segmenter/data";
	static Properties props = new Properties();
	static CRFClassifier<CoreLabel> seg;
	
	public Segmenter() {
		// TODO Auto-generated constructor stub
		if(props.isEmpty())
		{
			initClass();
		}
	}
	
	public static void initClass()
	{
		props.setProperty("sighanCorporaDict", basedir);
		props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
		props.setProperty("inputEncoding", "UTF-8");
		props.setProperty("sighanPostProcessing", "true");
		seg = new CRFClassifier<CoreLabel>(props);
		seg.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
	}
		
	public String segText(String passage)
	{	    
	    String segmentedSentences = "";
		
	    List<String> segmented = seg.segmentString(passage);
	    segmentedSentences = String.join(" ", segmented);
	    return segmentedSentences;
	}
	
	public void segFile(Path sourceFilePath,Path destFilePath) throws IOException
	{
		FileOutputStream outputFileStream;
		byte[] byteTypeFile;
		String passage;
		String segmentedPassage;
		
		// Create destFile if it not exist
		File outputFile = destFilePath.toFile();
		if(!outputFile.exists())
		{
			outputFile.createNewFile();
		}
		// Create output stream		
		try {
			outputFileStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e){			
			throw new FileNotFoundException();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputFileStream));
		// Read source file
		byteTypeFile = Files.readAllBytes(sourceFilePath);
		passage = new String(byteTypeFile, StandardCharsets.UTF_8);
		segmentedPassage = segText(passage);
		bufferedWriter.write(segmentedPassage);
		bufferedWriter.close();
		outputFileStream.close();		
	}

	public void segDir(Path sourceDir,Path destDir) throws IOException,NotDirectoryException
	{
		// Get all file under sourceDir
		File dirObj = sourceDir.toFile();
		
		File file;
		Path sourceFilePath;
		Path desFilePath;
		
		
		// If desDir does not exist, Create it!
		file = destDir.toFile();
		if(! file.exists())
		{
			file.mkdirs();
		}
		if(dirObj.isDirectory())
		{
			String[] sourceFileNames = dirObj.list();
			for(String fileName : sourceFileNames)
			{	
				sourceFilePath = Paths.get(sourceDir.toString(),fileName);
				desFilePath = Paths.get(destDir.toString(),fileName+".seg");
				file = sourceFilePath.toFile();
				if(file.isFile())
				{					
					segFile(sourceFilePath, desFilePath);
				}
			}
		}
		else
		{
			throw new NotDirectoryException(sourceDir.toRealPath(null).toString());
		}
	}
	
	public void segFileList(Path fileList, Path destDir) throws IOException
	{
		File file;
		String fileName;
		Path filePath;
		Path destFilePath;
		FileInputStream fileInputStream;
		BufferedReader fileReader;
		
		// If desDir does not exist, Create it!
		file = destDir.toFile();
		if(! file.exists())
		{
			file.mkdirs();
		}
		file = fileList.toFile();
		if(!file.exists())
		{
			throw new FileNotFoundException();
		}
		else
		{
			fileInputStream = new FileInputStream(file);
			fileReader = new BufferedReader(new InputStreamReader(fileInputStream));
			while((fileName = fileReader.readLine())!=null)
			{
				// Get file name
				filePath = Paths.get(fileName);
				destFilePath = Paths.get(destDir.toString(),fileName,".seg");
				segFile(filePath,destFilePath);				
			}
		}
				
	}

	
	@Override
	public String SegText(String text) 
	{	
		    String segmentedSentences = "";
			
		    List<String> segmented = seg.segmentString(text);
		    segmentedSentences = String.join(" ", segmented);
		    return segmentedSentences;
	}

	
	@Override
	public String SegFile(String filePath) throws Exception
	{
		MyFile myFile;
		try
		{
			myFile = new MyFile(filePath,FileModel.Open,FileAccess.Read);
			String text = myFile.ReadAll();	
			myFile.Close();
			return SegText(text);
		}
		catch(Exception e)
		{
			
		}
		return null;
	}


	@Override
	public void SegFile(String sourceFile, String desFile) 
	{
		
		MyFile source;
		MyFile des;
		try
		{
			String segedText = SegText(sourceFile);
			try
			{
				des = new MyFile(desFile,FileModel.OpenOrCreate,FileAccess.Write);
				des.Write(segedText);
			}
			catch(Exception e)
			{
				
			}
		}
		catch(Exception e)
		{
			
		}
	}

	@Override
	public void SegDir(String sourceDir, String desDir)
	{
		File src = new File(sourceDir);
		
		
	}

	@Override
	public void SegFileList(String source, String desDir) {
		// TODO Auto-generated method stub
		
	}
	
}
