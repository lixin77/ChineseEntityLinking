package msra.nlp.seg;

import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.Properties;

import pml.file.FileException;

public interface Seg
{
	/**
	 * segment string into words.
	 * @param text
	 * 					String type of raw text file.
	 * @return
	 * 					String type of segmented text fiel. 
	 */
	public String SegText(String text) throws SegException;
	
	/**
	 * this function should read content firstly from appointed file then segment the file content
	 * and return a string type of segmented file.
	 * @param filePath
	 * 						The path of the file which to be segmented
	 * @return
	 * @throws Exception 
	 */
	public String SegFile(String filePath) throws SegException;
	/**
	 * should read content firstly from appointed file then segment the file content
	 * and store the segmented file into destinate file.
	 * @param sourceFile
	 * 					The path of the file which to be segmented
	 * @param desFile
	 * 					The path of the file in which the segmented file stored
	 * @return
	 */
	public void SegFile(String sourceFile, String desFile)throws SegException;
	
	public void SegDir(String sourceDir, String desDir) throws SegException;
	/**
	 * this function is designed to segment all the files with a folder and stored the segmented files
	 * within a desinate folder with original file name.
	 * @param sourceDir
	 * @param desDir
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 */
	public void  SegDir(String sourceDir, String desDir, FilenameFilter filter) throws SegException;
	
	/**
	 * this function is designed to segment files whoes pathes and names are listed in a source file.
	 * The segmented files will be store within the destinate folder with the original file names.
	 * @param source
	 * @param desDir
	 */
	public void SegFileList(String source , String desDir)throws SegException;
	
	/**
	 * set the properties of segmenter. 
	 * not all the implementers will implement this menthod.
	 * @param props
	 */
	public default void SetProperty(Properties props)
	{
		
	}
	
	public default void SetProperty(String key, String value)
	{
		
	}
	
	public static void main(String args[])
	{
		Seg seg = new Segmenter();
	}
}
