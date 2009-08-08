package net.sourceforge.jvlt;

import java.util.*;

public class QuizDict {
	private JVLTModel _model;
	private ArrayList<QueryResult> _results;
	private QuizInfo _info = null;
	private EntryFilter[] _filters = null;
	
	private ArrayList<Entry> _entries;

	public QuizDict(JVLTModel model) {
		_model = model;
		_filters = null;
		_info = null;
		_results = new ArrayList<QueryResult>();
		_entries = new ArrayList<Entry>();
	}
	
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
	
	public int getEntryCount() { return _entries.size(); }
	
	public Entry getEntry(int pos) {
		if (pos < 0 || pos >= _entries.size())
			return null;
		else
			return _entries.get(pos);
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
	 * Reset the dictionary.
	 * Clear the result list and reinitialize the entry list using the not
	 * known entries.
	 */
	public void reset() {
		Entry[] not_known = getNotKnownEntries();
		_results.clear();
		_entries.clear();
		_entries.addAll(Arrays.asList(not_known));
		Collections.shuffle(_entries, new Random(new Date().getTime()));
	}
	
	/**
	 * Update the quiz dictionary using a new set of filters and/or a different
	 * quiz type.
	 */
	public void update(EntryFilter[] filters, QuizInfo info) {
		_filters = filters;
		_info = info;
		
		_results.clear();
		_entries.clear();
		_entries.addAll(getEntryList(_model.getDict().getEntries(), filters));
	}
	
	/**
	 * Update the quiz dictionary when entries have been added, changed or
	 * removed.
	 */
	public void update(Collection<Entry> new_entries,
			Collection<Entry> changed_entries,
			Collection<Entry> removed_entries) {
		if (new_entries != null) {
			_entries.addAll(
					getEntryList(new_entries, _filters));
		}
		
		if (changed_entries != null) {
			_entries.removeAll(changed_entries);
			_entries.addAll(
					getEntryList(changed_entries, _filters));
			updateResultList(changed_entries);
		}
		
		if (removed_entries != null) {
			_entries.removeAll(removed_entries);
			_entries.addAll(
					getEntryList(removed_entries, _filters));
			updateResultList(removed_entries);
		}
	}
	
	private List<Entry> getEntryList(Collection<Entry> entries,
			EntryFilter[] filters) {
		ArrayList<Entry> return_list = null;
		
		if (_info == null)
			return Collections.emptyList();
		
		//-----
		// Only add the entries that have been expired and where the quizzed
		// attribute is set.
		//-----
		String attr_str = _info.getQuizzedAttribute(); 
		Attribute attr = _model.getDictModel().getMetaData(
					Entry.class).getAttribute(attr_str);
		GregorianCalendar now = new GregorianCalendar();
		ArrayList<Entry> entry_array = new ArrayList<Entry>();
		for (Iterator<Entry> it=entries.iterator(); it.hasNext(); ) {
			Entry entry = it.next();
			Calendar expiry_date = entry.getExpireDate();
			if (attr.getValue(entry)!=null && (entry.getBatch() == 0
				|| expiry_date == null || expiry_date.before(now)))
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
					&& ! _entries.contains(result.getEntry()))
				it.remove();
		}
	}
}

