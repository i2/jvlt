package net.sourceforge.jvlt;

import java.util.*;

public class QuizDict {
	private JVLTModel _model;
	private ArrayList<QueryResult> _results;
	private QuizInfo _info = null;
	private EntryFilter[] _filters = null;
	private boolean _ignore_batches;
	private ArrayList<Entry> _available_entries;
	private ArrayList<Entry> _current_entries;

	public QuizDict(JVLTModel model) {
		_model = model;
		_filters = null;
		_info = null;
		_results = new ArrayList<QueryResult>();
		_available_entries = new ArrayList<Entry>();
		_current_entries = new ArrayList<Entry>();
	}
	
	public boolean isIgnoreBatches() { return _ignore_batches; }
	
	public void setIgnoreBatches(boolean ignore) { _ignore_batches = ignore; }
	
	public int getResultCount() { return _results.size(); }
	
	public QueryResult getResult(int pos) {
		if (pos >= _results.size())
			return null;
		else
			return _results.get(pos);
	}
	
	public void setResult(int index, QueryResult result) {
		if (index >= _results.size())
			_results.add(result);
		else
			_results.set(index, result);
	}
	
	public QuizInfo getQuizInfo() { return _info; }
	
	public int getAvailableEntryCount() { return _available_entries.size(); }
	
	public int getCurrentEntryCount() { return _current_entries.size(); }
	
	/**
	 * Get entry of current quiz
	 */
	public Entry getCurrentEntry(int pos) {
		if (pos < 0 || pos >= _current_entries.size())
			return null;
		else
			return _current_entries.get(pos);
	}

	public Entry[] getKnownEntries() {
		ArrayList<Entry> list = new ArrayList<Entry>();
		Iterator<QueryResult> it = _results.iterator();
		while (it.hasNext()) {
			QueryResult result = it.next();
			if (result.isKnown())
				list.add(result.getEntry());
		}
		
		return list.toArray(new Entry[0]);
	}
	
	public Entry[] getNotKnownEntries() {
		ArrayList<Entry> list = new ArrayList<Entry>();
		Iterator<QueryResult> it = _results.iterator();
		while (it.hasNext()) {
			QueryResult result = it.next();
			if (! result.isKnown())
				list.add(result.getEntry());
		}
		
		return list.toArray(new Entry[0]);
	}
	
	/**
	 * Starts a new quiz
	 */
	public void start() {
		_results.clear();
		_current_entries.clear();
		_current_entries.addAll(_available_entries);
	}
	
	/**
	 * Reset the dictionary.
	 * Clear the result list and reinitialize the entry list using the not
	 * known entries.
	 */
	public void reset() {
		Entry[] not_known = getNotKnownEntries();
		_results.clear();
		_current_entries.clear();
		_current_entries.addAll(Arrays.asList(not_known));
		Collections.shuffle(_current_entries, new Random(new Date().getTime()));
	}
	
	/**
	 * Update the quiz dictionary using a new set of filters and/or a different
	 * quiz type.
	 */
	public void update(EntryFilter[] filters, QuizInfo info) {
		_filters = filters;
		_info = info;
		
		_available_entries.clear();
		_available_entries.addAll(
				getEntryList(_model.getDict().getEntries(), filters));
	}
	
	/**
	 * Update the quiz dictionary when entries have been added, changed or
	 * removed.
	 */
	public void update(Collection<Entry> new_entries,
			Collection<Entry> changed_entries,
			Collection<Entry> removed_entries) {
		if (new_entries != null) {
			_available_entries.addAll(
					getEntryList(new_entries, _filters));
			// Do not add entries to _current_entries, as _current_entries
			// might contain only the not known entries
		}
		
		if (changed_entries != null) {
			_current_entries.removeAll(changed_entries);
			_current_entries.addAll(
					getEntryList(changed_entries, _filters));
			_available_entries.removeAll(changed_entries);
			_available_entries.addAll(
					getEntryList(changed_entries, _filters));
			updateResultList(changed_entries);
		}
		
		if (removed_entries != null) {
			_available_entries.removeAll(removed_entries);
			_current_entries.removeAll(removed_entries);
			updateResultList(removed_entries);
		}
	}
	
	private List<Entry> getEntryList(Collection<Entry> entries,
			EntryFilter[] filters) {
		ArrayList<Entry> return_list = null;
		
		if (_info == null)
			return Collections.emptyList();
		
		//-----
		// Only add the entries where the quizzed attribute is set and that
		// do not have a flag set that disables them. If the batches
		// are not ignored, only not expired entries are added.
		//-----
		String attr_str = _info.getQuizzedAttribute(); 
		Attribute attr = _model.getDictModel().getMetaData(
					Entry.class).getAttribute(attr_str);
		GregorianCalendar now = new GregorianCalendar();
		ArrayList<Entry> entry_array = new ArrayList<Entry>();
		for (Iterator<Entry> it=entries.iterator(); it.hasNext(); ) {
			Entry entry = it.next();
			Calendar expiry_date = entry.getExpireDate();
			
			/* Check whether quizzed attribute is set */
			if (attr.getValue(entry) == null)
				continue;
			
			/* Check for flags */
			int flags = entry.getUserFlags();
			if ((flags & Entry.Stats.UserFlag.INACTIVE.getValue()) != 0
					|| (flags & Entry.Stats.UserFlag.KNOWN.getValue()) != 0)
				continue;
			
			/* Check for batch and expiry date */
			if (!_ignore_batches && entry.getBatch() != 0
					&& expiry_date != null && !expiry_date.before(now))
				continue;
			
			entry_array.add(entry);
		}

		//-----
		// Add the entries that match the filters: First, all entries that
		// match the first filter are added, after that all entries that match
		// the second filter etc.
		//-----
		if (filters.length > 0) {
			TreeSet<Entry> entry_set = new TreeSet<Entry>();
			ArrayList<Entry> entry_list = new ArrayList<Entry>();
			for (int i=0; i<filters.length; i++) {
				entry_list.clear();
				for (Iterator<Entry> it = entry_array.iterator();
					it.hasNext(); ) {
					Entry entry = it.next();
					if (! entry_set.contains(entry)
						&& filters[i].entryMatches(entry)) {
						entry_set.add(entry);
						entry_list.add(entry);
					}
				}
					
				Collections.shuffle(
					entry_list, new Random(new Date().getTime()));
				return_list = entry_list;
			}
		}
		//-----
		// If there is no filter, all entries that have been expired are added.
		//-----
		else {
			Collections.shuffle(entry_array, new Random(new Date().getTime()));
			return_list = entry_array;
		}
		
		return return_list;
	}
	
	/**
	 * Remove all entries from the result list that are not contained in
	 * the entry list.
	 */
	private void updateResultList(Collection<Entry> entries) {
		Iterator<QueryResult> it = _results.iterator();
		while (it.hasNext()) {
			QueryResult result = it.next();
			if (entries.contains(result.getEntry())
					&& ! _current_entries.contains(result.getEntry()))
				it.remove();
		}
	}
}

