package net.sourceforge.jvlt;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class CollatingEntryComparator implements Comparator<Entry> {
	Collator _collator;
	
	public CollatingEntryComparator() {
		_collator = Collator.getInstance(Locale.US);
		_collator.setStrength(Collator.PRIMARY);
	}
	
	public int compare(Entry e1, Entry e2) {
		return _collator.compare(e1.getOrthography(), e2.getOrthography());
	}
}
