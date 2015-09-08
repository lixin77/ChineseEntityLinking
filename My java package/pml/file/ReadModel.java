package pml.file;

public enum ReadModel
{
	Beg(0), Cur(1);
	
	Integer model = null;
	
	private ReadModel(Integer model) 
	{
		this.model = model;
	}

	public String toString()
	{
		return null;
	}
	
	public ReadModel toReadModel(Integer model)
	{
		if(model == 0)
		{
			return Beg;
		}
		else if(model ==1)
		{
			return Cur;
		}
		else {
			throw new FileException("No such read model!");
		}
	}

}
