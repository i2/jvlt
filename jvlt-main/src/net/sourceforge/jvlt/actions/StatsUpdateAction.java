package net.sourceforge.jvlt.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.jvlt.core.DictException;
import net.sourceforge.jvlt.core.Entry;

import org.apache.log4j.Logger;

public class StatsUpdateAction extends QueryAction {
	private static final Logger logger = Logger
			.getLogger(StatsUpdateAction.class);

	private Entry[] _known_entries;
	private Entry[] _unknown_entries;
	private GregorianCalendar _now;
	private ArrayList<EditDictObjectAction> _entry_actions;
	private boolean _update_batches = true;
	private Map<Entry, Integer> _user_flag_map;

	public StatsUpdateAction(Entry[] known_entries, Entry[] unknown_entries) {
		super();

		_known_entries = known_entries;
		_unknown_entries = unknown_entries;
		_now = new GregorianCalendar();
		_now.set(Calendar.SECOND, 0);
		_entry_actions = new ArrayList<EditDictObjectAction>();
		_user_flag_map = new HashMap<Entry, Integer>();
	}

	public void setUserFlag(Entry entry, int flag) {
		_user_flag_map.put(entry, flag);
	}

	public Entry[] getKnownEntries() {
		return _known_entries;
	}

	public Entry[] getUnknownEntries() {
		return _unknown_entries;
	}

	public boolean isUpdateBatches() {
		return _update_batches;
	}

	public void setUpdateBatches(boolean update) {
		_update_batches = update;
	}

	public void executeAction() {
		if (_entry_actions.size() == 0)
			prepare();

		Iterator<EditDictObjectAction> it = _entry_actions.iterator();
		while (it.hasNext()) {
			try {
				it.next().executeAction();
			} catch (DictException ex) {
				logger.error("Failed to execute action", ex);
			}
		}
	}

	public void undoAction() {
		Iterator<EditDictObjectAction> it = _entry_actions.iterator();
		while (it.hasNext()) {
			try {
				it.next().undoAction();
			} catch (DictException ex) {
				logger.error("Failed to execute action", ex);
			}
		}
	}

	private void prepare() {
		for (int i = 0; i < _known_entries.length; i++)
			_entry_actions.add(new EditDictObjectAction(_known_entries[i],
					getUpdatedEntry(_known_entries[i], true)));
		for (int i = 0; i < _unknown_entries.length; i++)
			_entry_actions.add(new EditDictObjectAction(_unknown_entries[i],
					getUpdatedEntry(_unknown_entries[i], false)));
	}

	private Entry getUpdatedEntry(Entry entry, boolean known) {
		Entry new_entry = (Entry) entry.clone();
		new_entry.setNumQueried(new_entry.getNumQueried() + 1);
		new_entry.setLastQueried(_now);
		if (!known)
			new_entry.setNumMistakes(new_entry.getNumMistakes() + 1);
		new_entry.setLastQuizResult(known);
		if (_update_batches)
			new_entry.updateBatch();

		/* Update flags */
		if (_user_flag_map.containsKey(entry))
			new_entry.setUserFlags(_user_flag_map.get(entry));

		return new_entry;
	}
}
