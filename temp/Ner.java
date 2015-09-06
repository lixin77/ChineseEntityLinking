package msra.nlp.ner;

public interface Ner 
{
	public Object NerText(String segedText) throws NerException;
	
	public Object NerFile(String filePath) throws NerException;
	
}
