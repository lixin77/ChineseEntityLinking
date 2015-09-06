package msra.nlp.kb;

public class KnowledgeBaseException extends RuntimeException
{
	Throwable cause = null;
	
	public KnowledgeBaseException()
	{
		
	}
	
	public KnowledgeBaseException(String message)
	{
		super(message);
		this.cause = new Throwable(message);
	}
	
	public KnowledgeBaseException(Throwable cause)
	{
		super(cause.getMessage());
		this.cause = cause;
	}
	
	public Throwable GetCause()
	{
		return this.cause;
	}


}
