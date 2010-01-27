package net.sourceforge.jvlt.core;

public interface Reinitializable extends Cloneable {
	public void reinit(Reinitializable object);

	public Object clone();
}
