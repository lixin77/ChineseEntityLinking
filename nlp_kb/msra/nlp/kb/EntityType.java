package msra.nlp.kb;

import java.util.List;

public enum EntityType {
	PER("PER"),GPE("GPE"),ORG("ORG"),LOC("LOC"),UD("UD");
	
	String type = null;
	
	private EntityType(String type)
	{
		this.type = type;
	}
	
	public static EntityType toEntityType(String string)
	{
		if(PER.Equal(string) || string.equals("PERSON"))
		{
			return PER;
		}
		else if(ORG.Equal(string))
		{
			return ORG;
		}
		else if(GPE.Equal(string))
		{
			return GPE;
		}
		else if(LOC.Equal(string))
		{
			return LOC;
		}
		else
		{
			return UD;
		}
	}
	
	
	public boolean Equal(String type)
	{
		return this.type.equals(type);
	}
	
	public String toString()
	{
		return type;
	}
	
}
