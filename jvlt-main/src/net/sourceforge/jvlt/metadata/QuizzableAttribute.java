package net.sourceforge.jvlt.metadata;


/** Attribute with extra method for performing quizzes */
public interface QuizzableAttribute extends Attribute {
	/**
	 * Checks whether the attribute's value matches the user input
	 * @param o The object from which the attribute value is fetched
	 * @param input The user input
	 * @param matchCase If true the case has to match
	 */
	boolean matches(Object o, String input, boolean matchCase);
}
