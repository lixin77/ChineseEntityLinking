package msra.nlp.edl;


public interface Query 
{
	public String name = "name";
	public String type = "type";
	public String docid = "docid";
	public String begin = "begin";
	public String end = "end";
	
	public Object QueryText(String text, String docID) throws EdlException;
	
	public void QueryText(String text, String docID, String desPath) throws EdlException;
	
	public Object QueryFile(String sourcePath) throws EdlException;
	
	public Object QueryFile(String sourcePath, String docID) throws EdlException;
	
	public void QueryFile(String text, String docID, String desPath) throws EdlException;
}
