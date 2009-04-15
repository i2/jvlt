package net.sourceforge.jvlt;

public interface Reinitializable extends Cloneable {
	public void reinit(Reinitializable object);
	
	public Object clone();
}

