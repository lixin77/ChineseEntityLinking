package msra.nlp.seg;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.Path;

public interface Seg
{
	/**
	 * segment string into words.
	 * @param text
	 * 					String type of raw text file.
	 * @return
	 * 					String type of segmented text fiel. 
	 */
	public String SegText(String text);
	
	/**
	 * this function should read content firstly from appointed file then segment the file content
	 * and return a string type of segmented file.
	 * @param filePath
	 * 						The path of the file which to be segmented
	 * @return
	 * @throws Exception 
	 */
	public String SegFile(String filePath) throws Exception;
	/**
	 * should read content firstly from appointed file then segment the file content
	 * and store the segmented file into destinate file.
	 * @param sourceFile
	 * 					The path of the file which to be segmented
	 * @param desFile
	 * 					The path of the file in which the segmented file stored
	 * @return
	 */
	public void SegFile(String sourceFile, String desFile);
	
	public void SegDir(String sourceDir, String desDir);
	/**
	 * this function is designed to segment all the files with a folder and stored the segmented files
	 * within a desinate folder with original file name.
	 * @param sourceDir
	 * @param desDir
	 */
	public void  SegDir(String sourceDir, String desDir, FilenameFilter filter);
	
	/**
	 * this function is designed to segment files whoes pathes and names are listed in a source file.
	 * The segmented files will be store within the destinate folder with the original file names.
	 * @param source
	 * @param desDir
	 */
	public void SegFileList(String source , String desDir);
		
}
