package msra.nlp.seg;

public class SegException extends RuntimeException
{
	Throwable cause = null;
	
	public SegException()
	{
		
	}
	
	public SegException(String message)
	{
		super(message);
		this.cause = new Throwable(message);
	}
	
	public SegException(Throwable cause)
	{
		super(cause.getMessage());
		this.cause = cause;
	}
	
	public Throwable GetCause()
	{
		return this.cause;
	}

}
