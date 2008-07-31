package net.sourceforge.jvlt;

import java.util.Iterator;
import java.util.LinkedList;

import net.sourceforge.jvlt.event.DictUpdateListener;
import net.sourceforge.jvlt.event.DictUpdateListener.*;

public class QueryModel extends AbstractModel {
	private LinkedList<DictUpdateListener> _dict_update_listeners;
	
	public QueryModel() {
		_dict_update_listeners = new LinkedList<DictUpdateListener>();
	}
	
	public void addDictUpdateListener(DictUpdateListener listener) {
		_dict_update_listeners.add(listener);
	}

	public void removeDictUpdateListener(DictUpdateListener listener) {
		_dict_update_listeners.remove(listener);
	}
	
	protected void execute(UndoableAction a) {
		if (a instanceof StatsUpdateAction)
			performStatsUpdateAction((StatsUpdateAction) a, false);

		_executed_actions++;
	}
	
	protected void undo(UndoableAction a) {
		if (a instanceof StatsUpdateAction)
			performStatsUpdateAction((StatsUpdateAction) a, true);
		
		_executed_actions--;
	}
	
	private void fireDictUpdateEvent(DictUpdateEvent event) {
		Iterator<DictUpdateListener> it = _dict_update_listeners.iterator();
		while (it.hasNext())
			it.next().dictUpdated(event);
	}

	private void performStatsUpdateAction(StatsUpdateAction sua, boolean undo) {
		if (undo)
			sua.undoAction();
		else
			sua.executeAction();

		LinkedList<Entry> entry_list = new LinkedList<Entry>();
		Entry[] entries = sua.getKnownEntries();
		for (int i=0; i<entries.length; i++)
			entry_list.add(entries[i]);
		entries = sua.getUnknownEntries();
		for (int i=0; i<entries.length; i++)
			entry_list.add(entries[i]);
		fireDictUpdateEvent(new EntryDictUpdateEvent(
			EntryDictUpdateEvent.ENTRIES_CHANGED, entry_list));
	}
}

