package net.sourceforge.jvlt;

public interface ChoiceAttribute extends Attribute {
	public void addValues(Object[] values);
	
	public void setValues(Object[] values);
	
	public Object[] getValues();
}

