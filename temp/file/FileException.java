package pml.file;

public class FileException extends RuntimeException
{
	/**
	 * the cause of the exception
	 */
	Throwable cause = null;
	
	/**
	 * 
	 * @param message
	 */
	public FileException(String message) 
	{
		super(message);
		this.cause = new Throwable(message);
	}
	
	public FileException(Throwable cause)
	{
		super(cause.getMessage());
		this.cause = cause;
	}
	
	public Throwable GetCause()
	{
		return cause;
	}

}
