package net.sourceforge.jvlt.metadata;

import net.sourceforge.jvlt.core.Sense;
import net.sourceforge.jvlt.metadata.EntryMetaData.SensesAttribute;

public class QuizzableSensesAttribute extends SensesAttribute implements
		QuizzableAttribute {

	@Override
	public boolean matches(Object o, String input, boolean matchCase) {
		String normalizedInput = input.trim();
		if (matchCase) {
			normalizedInput = normalizedInput.toLowerCase();
		}

		Sense[] senses = (Sense[]) getValue(o);
		for (Sense s : senses) {
			String solution = s.getTranslation().trim();
			if (matchCase) {
				solution = solution.toLowerCase();
			}
			if (solution.equals(normalizedInput)) {
				return true;
			}
		}
		
		return false;
	}
}
