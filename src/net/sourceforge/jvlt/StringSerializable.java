package net.sourceforge.jvlt;

public interface StringSerializable {
	public String convertToString();
	
	public void initFromString(String value) throws DeserializationException;
}

