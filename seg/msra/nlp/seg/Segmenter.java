package msra.nlp.seg;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;

// import user model
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import pml.file.*;
import pml.file.reader.FileReader;
import pml.file.reader.LargeFileReader;
import pml.file.reader.SmallFileReader;
import pml.file.util.Util;


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
	private String basedir = "D:/Codes/Project/NLP/nlp_segmenter/stanford-segmenter/data";
	private Properties props = new Properties();
	static CRFClassifier<CoreLabel> seg = null;
	
	/**
	 * construct an segmenter with default properties and default data path
	 */
	public Segmenter() 
	{
		if(props.isEmpty())
		{
			SetDefaultProps();
		}
		InitialSeg();
	}
	
	/**
	 * construct an segmenter with default properties and given data path
	 * @param basedir
	 * 			Path of the directory including the necessary packages of stanford tool
	 */	
	public Segmenter(String basedir)
	{
		this.basedir = basedir;
		if(props.isEmpty())
		{
			SetDefaultProps();
		}
		InitialSeg();
	}
	
	/**
	 * construct an segmenter with given properties
	 * @param properties
	 * 			Properties for the stanford segmenter
	 */
	public Segmenter(Properties properties)
	{
		this.props = properties;
		InitialSeg();
	}

	/**
	 * set default properties of the segmenter
	 */
	private void SetDefaultProps()
	{
		props.setProperty("sighanCorporaDict", basedir);
		props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
		props.setProperty("inputEncoding", "UTF-8");
		props.setProperty("sighanPostProcessing", "true");
	}
	
	/**
	 * change the properties of the segmenter and remake a segmenter.
	 * @param props
	 * 			Property pairs
	 */
	public void SetProperty(Map<String,String> props)
	{
		for(String key: props.keySet())
		{
			this.props.setProperty(key, props.get(key));
		}
		InitialSeg();
	}
	
	public void SetProperty(String key,String value)
	{
		this.props.setProperty(key, value);
		InitialSeg();
	}
	
	/**
	 * load stanford segmenter
	 */
	private void InitialSeg()
	{
		this.seg = new CRFClassifier<CoreLabel>(this.props);
		this.seg.loadClassifierNoExceptions(this.basedir + "/ctb.gz", this.props);
	}
	
	 // 	The following four functions are going to be deplicated
	 
	public String segText(String passage)
	{	    
	    String segmentedSentences = "";
		
	    List<String> segmented = seg.segmentString(passage);
	    segmentedSentences = String.join(" ", segmented);
	    return segmentedSentences;
	}
	
	public void segFile(Path sourceFilePath,Path destFilePath) throws SegException
	{
		FileOutputStream outputFileStream;
		byte[] byteTypeFile;
		String passage;
		String segmentedPassage;
		
		// Create destFile if it not exist
		File outputFile = destFilePath.toFile();
		if(!outputFile.exists())
		{
			try
			{
				outputFile.createNewFile();
			}
			catch(IOException e)
			{
				throw new SegException(e.getCause());
			}
		}
		// Create output stream		
		try {
			outputFileStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e){			
			throw new SegException(e.getCause());
		}
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputFileStream));
		// Read source file
		try {
			byteTypeFile = Files.readAllBytes(sourceFilePath);
		} catch (IOException e) {
			throw new SegException(e.getCause());
		}
		passage = new String(byteTypeFile, StandardCharsets.UTF_8);
		segmentedPassage = segText(passage);
		try {
			bufferedWriter.write(segmentedPassage);
			bufferedWriter.close();
			outputFileStream.close();	
		} catch (IOException e) {
			throw new SegException(e.getCause());
		}
	}

	public void segDir(Path sourceDir,Path destDir) throws SegException
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
			try {
				throw new SegException(sourceDir.toRealPath(null).toString());
			} catch (IOException e) {
				throw new SegException(e.getCause());
			}
		}
	}
	
	public void segFileList(Path fileList, Path destDir) throws SegException
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
			throw new SegException(file.toString()+" does not exist!");
		}
		else
		{
			try 
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
			catch (FileNotFoundException e) 
			{
				throw new SegException(e.getCause());
			}
			catch (IOException e) {
				throw new SegException(e.getCause());
			}

		}
				
	}
	
	/**
	 * segment string into words.
	 * @param text
	 * 					String type of raw text file.
	 * @return
	 * 					String type of segmented text fiel. 
	 */
	@Override
	public String SegText(String text) 
	{	
		    String segmentedSentences = "";
			
		    List<String> segmented = seg.segmentString(text);
		    segmentedSentences = String.join(" ", segmented);
		    return segmentedSentences;
	}
	
	/**
	 * this function should read content firstly from appointed file then segment the file content
	 * and return a string type of segmented file.
	 * @param filePath
	 * 						The path of the file which to be segmented
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String SegFile(String filePath)
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
			throw new SegException(e.getCause());
		}
	}

	@Override
	public void SegFile(String sourceFile, String desFile) 
	{
		MyFile des;
		try
		{
			String segedText = SegText(sourceFile);
			try
			{
				des = new MyFile(desFile,FileModel.OpenOrCreate,FileAccess.Write);
				des.Write(segedText);
				if(des!=null)
				{
					des.Close();
				}
			}
			catch(Exception e)
			{
		
				throw new SegException(e.getCause());
			}
		}
		catch(Exception e)
		{
			throw new FileException(e.getCause());
		}
	}

	/**
	 * this function is designed to segment all the files with a folder and stored the segmented files
	 * within a desinate folder with original file name.
	 * @param sourceDir
	 * @param desDir
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 */
	@Override
	public void SegDir(String sourceDir, String desDir)
	{
		File src = new File(sourceDir);
		SegDir(sourceDir, desDir,null);
	}

	@Override
	public void SegDir(String sourceDir, String desDir, FilenameFilter filter) 
	{
		if(!Util.IsDir(sourceDir))
		{
			throw new SegException(sourceDir+" does not exist!");
		}
		if(!Util.IsValidDirPath(desDir))
		{
			throw new SegException(desDir+" is not a valid direcotry path");
		}
		File src = new File(sourceDir);
		File[] files;
		if(filter!=null)
		{
			files = src.listFiles(filter);
		}
		else
		{
			files = src.listFiles();
		}
		for(File file: files)
		{
			try 
			{
				SegFile(file.toString(), desDir+"/"+Util.Path2Name(file.toString()));
			} catch (Exception e)
			{
				throw new SegException(e.getCause());
			}
		}
	}
		
	/**
	 * this function is designed to segment files whoes pathes and names are listed in a source file.
	 * The segmented files will be store within the destinate folder with the original file names.
	 * @param source
	 * @param desDir
	 */
	@Override
	public void SegFileList(String source, String desDir) {
		FileReader reader = new SmallFileReader(source);
		String path;
		try{
		while((path=reader.ReadLine())!=null)
		{
			try 
			{
				SegFile(path, desDir+"/"+Util.Path2Name(path));
			} catch (Exception e)
			{
				throw new SegException(e.getCause());
			}
		}
		}
		catch(FileException e)
		{
			throw new SegException(e.getCause());
		}
		reader.Close();
	}
}
