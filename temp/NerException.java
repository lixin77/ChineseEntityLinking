package msra.nlp.ner;

public class NerException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	Throwable cause = null;
	
	public NerException()
	{
		
	}
	
	public NerException(String message)
	{
		super(message);
		this.cause = new Throwable(message);
	}
	
	public NerException(Throwable cause)
	{
		super(cause.getMessage());
		this.cause = cause;
	}
	
	public Throwable GetCause()
	{
		return this.cause;
	}

}
