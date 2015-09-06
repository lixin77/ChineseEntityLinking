package msra.nlp.edl;

public class EdlException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	Throwable cause = null;
	
	public EdlException()
	{
		
	}
	
	public EdlException(String message)
	{
		super(message);
		this.cause = new Throwable(message);
	}
	
	public EdlException(Throwable cause)
	{
		super(cause.getMessage());
		this.cause = cause;
	}
	
	public Throwable GetCause()
	{
		return this.cause;
	}



}
