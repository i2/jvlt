package net.sourceforge.jvlt;

import java.util.*;

public class EntryFilter {
	protected ObjectQuery _query;
	
	public EntryFilter(ObjectQuery query) { _query = query;	}
	
	public EntryFilter() { this(new ObjectQuery(Entry.class)); }
	
	public void setQuery(ObjectQuery query) { _query = query; }
	
	public List<Entry> getMatchingEntries(Collection<Entry> entries) {
		ArrayList<Entry> entry_list = new ArrayList<Entry>();
		for (Iterator<Entry> it=entries.iterator(); it.hasNext(); ) {
			Entry entry = it.next();
			if (entryMatches(entry))
				entry_list.add(entry);
		}
		
		return entry_list;
	}
	
	public boolean entryMatches(Entry entry) {
		return _query.objectMatches(entry);
	}
}

