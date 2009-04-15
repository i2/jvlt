package net.sourceforge.jvlt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class StatsUpdateAction extends QueryAction {
	private Entry[] _known_entries;
	private Entry[] _unknown_entries;
	private GregorianCalendar _now;
	private ArrayList<EditDictObjectAction> _entry_actions;
	
	public StatsUpdateAction(Entry[] known_entries, Entry[] unknown_entries) {
		super();
		
		_known_entries = known_entries;
		_unknown_entries = unknown_entries;
		_now = new GregorianCalendar();
		_now.set(Calendar.SECOND, 0);
		_entry_actions = new ArrayList<EditDictObjectAction>();
		for (int i=0; i<_known_entries.length; i++)
			_entry_actions.add(new EditDictObjectAction(_known_entries[i],
				getUpdatedEntry(_known_entries[i], true)));
		for (int i=0; i<_unknown_entries.length; i++)
			_entry_actions.add(new EditDictObjectAction(_unknown_entries[i],
				getUpdatedEntry(_unknown_entries[i], false)));
		
	}
	
	public Entry[] getKnownEntries() { return _known_entries; }
	
	public Entry[] getUnknownEntries() { return _unknown_entries; }
	
	public void executeAction() {
		Iterator<EditDictObjectAction> it = _entry_actions.iterator();
		while (it.hasNext()) {
			try { it.next().executeAction(); }
			catch (DictException ex) {} // Should not happen
		}
	}
	
	public void undoAction() {
		Iterator<EditDictObjectAction> it = _entry_actions.iterator();
		while (it.hasNext()) {
			try { it.next().undoAction(); }
			catch (DictException ex) {} // Should not happen
		}
	}
	
	private Entry getUpdatedEntry(Entry entry, boolean known) {
		Entry new_entry = (Entry) entry.clone();
		new_entry.setNumQueried(new_entry.getNumQueried()+1);
		new_entry.setLastQueried(_now);
		if (! known)
			new_entry.setNumMistakes(new_entry.getNumMistakes()+1);
		new_entry.setLastQueryResult(known);
		
		return new_entry;
	}
}

