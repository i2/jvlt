package net.sourceforge.jvlt.hibernate;

public class HibernateException extends Exception {
	private static final long serialVersionUID = 1;
	
	public HibernateException() {}

	public HibernateException(String message) { super(message); }

	public HibernateException(Throwable cause) { super(cause); }

	public HibernateException(String message, Throwable cause) {
		super(message, cause);
	}
}
