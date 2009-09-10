package net.sourceforge.jvlt;

import java.util.*;

public class Entry implements Comparable<Entry>, Reinitializable {
	public static class Comparator implements java.util.Comparator<Entry> {
		public int compare(Entry e1, Entry e2) {
			if (! e1._orthography.equals(e2._orthography))
				return e1._orthography.compareTo(e2._orthography);
			else if (! e1._pronunciations.equals(e2._pronunciations)) {
				if (e1._pronunciations.size() != e2._pronunciations.size())
					return e1._pronunciations.size()-e2._pronunciations.size();
				
				Iterator<String> it1 = e1._pronunciations.iterator();
				Iterator<String> it2 = e2._pronunciations.iterator();
				while (it1.hasNext()) {
					String s1 = it1.next();
					String s2 = it2.next();
					if (! s1.equals(s2))
						return s1.compareTo(s2);
				}

				return 0; // Should not happen
			} else {
				if (e1._class == null && e2._class == null)
					return 0;
				else if (e1._class == null)
					return -1;
				else if (e2._class == null)
					return 1;
				else
					return e1._class.compareTo(e2._class);
			}
		}
		
		public boolean equals(Object obj) { return super.equals(obj); }
	}
	
	public static class Stats implements Reinitializable {
		public enum UserFlag {
			NONE(0, "flag_none", "flag_none"),
			KNOWN(1 << 0, "flag_known_long", "flag_known_short"),
			INACTIVE(1 << 1, "flag_inactive_long", "flag_inactive_short");
			
			private int _value;
			private String _long_name;
			private String _short_name;
			
			private UserFlag(int value, String long_name, String short_name) {
				_value = value;
				_long_name = long_name;
				_short_name = short_name;
			}
			
			public int getValue() { return _value; }
			
			public String getLongName() { return _long_name; }
			
			public String getShortName() { return _short_name; }
			
			public String toString() {
				return GUIUtils.getString("Labels", _long_name);
			}
		}
		
		public void reinit(Reinitializable obj) {
			Stats stats = (Stats) obj;
			
			_batch = stats._batch;
			_num_queried = stats._num_queried;
			_num_mistakes = stats._num_mistakes;
			_last_queried = stats._last_queried;
			_date_added = stats._date_added;
			_user_flags = stats._user_flags;
			_last_quiz_result = stats._last_quiz_result;
		}
		
		public Object clone() {
			Stats stats = new Stats();
			stats._batch = _batch;
			stats._num_queried = _num_queried;
			stats._num_mistakes = _num_mistakes;
			stats._last_queried = _last_queried == null ? null :
				(Calendar) _last_queried.clone();
			stats._date_added = _date_added == null ? null :
				(Calendar) _date_added.clone();
			stats._user_flags = _user_flags;
			stats._last_quiz_result = _last_quiz_result == null ? null
					: new Boolean(_last_quiz_result);
			
			return stats;
		}

		public int _num_queried = 0;
		public int _num_mistakes = 0;
		public Calendar _last_queried = null;
		public Calendar _date_added = null;
		public int _batch = 0;
		public int _user_flags = 0;
		public Boolean _last_quiz_result = null;
	}
	
	public static final String UNIT_DAYS  = "days";
	public static final String UNIT_HOURS = "hours";
	
	private static Sense.Comparator _sense_comparator
		= new Sense.Comparator();
	
	private String _id;
	private String _orthography;
	private Vector<Sense> _senses;
	private TreeSet<String> _pronunciations;
	private TreeSet<Sense> _sense_set;
	private TreeSet<String> _categories;
	private String _lesson;
	private TreeSet<String> _mm_files;
	private EntryClass _class;
	private Stats _stats;
	
	public Entry (String id) {
		_id = id;
		_orthography="";
		_senses = new Vector<Sense>();
		_sense_set = new TreeSet<Sense>(new Sense.Comparator());
		_class = null;
		_categories = new TreeSet<String>();
		_lesson = "";
		_mm_files = new TreeSet<String>();
		_pronunciations = new TreeSet<String>();
		_stats = new Stats();
	}
	
	public void reinit(Reinitializable obj)	{
		Entry entry = (Entry) obj;
		_id = entry._id;
		_orthography = entry._orthography;
		// _senses = entry._senses;
		// _sense_set = entry._sense_set;
		_class = entry._class;
		_categories = entry._categories;
		_lesson = entry._lesson;
		_mm_files = entry._mm_files;
		_pronunciations = entry._pronunciations;
		_stats.reinit(entry._stats);
	}
	
	public String getID() { return _id; }
	
	public String getOrthography() { return _orthography; }
	
	public String[] getPronunciations() {
		return _pronunciations.toArray(new String[0]);
	}
	
	public Sense getSense(Sense sense) {
		SortedSet<Sense> tail = _sense_set.tailSet(sense);
		if (tail.size() == 0)
			return null;
		else {
			Sense first = (Sense) tail.iterator().next();
			if (_sense_comparator.compare(first, sense)!=0)
				return null;
			else
				return first;
		}
	}
	
	public Sense[] getSenses() { return _senses.toArray(new Sense[0]); }

	public int getBatch() {	return _stats._batch; }

	public Calendar getExpireDate()	{
		if (_stats._last_queried == null)
			return null;
		
		Calendar expire_date = (Calendar) _stats._last_queried.clone();
		if (_stats._batch == 0)
			return expire_date;
	
		float expiration_factor = JVLT.getConfig().getFloatProperty(
			"expiration_factor", 3.0f);
		String unit = JVLT.getConfig().getProperty(
			"expiration_unit", UNIT_DAYS);
		if (unit.equals(UNIT_DAYS))
			expire_date.add(Calendar.DAY_OF_MONTH,
				(int) Math.pow(expiration_factor, _stats._batch-1));
		else
			expire_date.add(Calendar.HOUR_OF_DAY,
				(int) Math.pow(expiration_factor, _stats._batch-1));
		
		return expire_date;
	}
	
	public int getNumQueried() { return _stats._num_queried; }
	
	public int getNumMistakes() { return _stats._num_mistakes; }
	
	public Calendar getLastQueried() { return _stats._last_queried; }
	
	public Calendar getDateAdded() { return _stats._date_added; }
	
	public String[] getCategories() {
		return (String[]) _categories.toArray(new String[0]);
	}

	public String getLesson() { return _lesson; }
	
	public EntryClass getEntryClass() { return _class; }
	
	public String[] getMultimediaFiles() {
		return (String[]) _mm_files.toArray(new String[0]);
	}
	
	public void setID(String id) { _id = id; }
	
	public void setOrthography(String o) { _orthography=o; }
	
	public void setPronunciations(String[] p) {
		_pronunciations.clear();
		_pronunciations.addAll(Arrays.asList(p));
	}

	public void addPronunciation(String p) { _pronunciations.add(p); }
	
	public void addSense(int index, Sense sense) throws DictException {
		if (_sense_set.contains(sense))
			throw new DictException("Sense \""+sense+"\" already exists.");
		
		_senses.add(index, sense);
		_sense_set.add(sense);
		sense.setParent(this);
	}
	
	public void addSense(Sense sense) throws DictException {
		addSense(_senses.size(), sense);
	}

	public void removeSense (Sense sense) {
		_sense_set.remove(sense);
		_senses.remove(sense);
	}

	public Boolean getLastQuizResult() { return _stats._last_quiz_result; }
	
	public void setLastQuizResult(Boolean known) {
		_stats._last_quiz_result = known;
	}
	
	public void setBatch(int batch) { _stats._batch = batch; }
	
	public void setNumQueried(int val) { _stats._num_queried=val; }

	public void setNumMistakes(int val) { _stats._num_mistakes=val; }
	
	public void setLastQueried(Calendar cal) { _stats._last_queried = cal; }
	
	public void setDateAdded(Calendar cal) { _stats._date_added = cal; }
	
	public void setCategories(String[] categories) {
		_categories.clear();
		_categories.addAll(Arrays.asList(categories));
	}

	public void addCategory(String category) { _categories.add(category); }
	
	public void setLesson(String lesson) { _lesson = lesson; }

	public void setEntryClass(EntryClass cl) { _class = cl; }
	
	public void setMultimediaFiles(String[] files) {
		_mm_files.clear();
		_mm_files.addAll(Arrays.asList(files));
	}
	
	public void addMultimediaFile(String file) { _mm_files.add(file); }
	
	public int getUserFlags() { return _stats._user_flags; }
	
	public void setUserFlags(int flags) { _stats._user_flags = flags; }
	
	public void resetStats() {
		_stats = new Stats();
	}
	
	public void updateBatch() {
		if (! _stats._last_quiz_result)
			setBatch(0);
		else {
			int num_batches = JVLT.getConfig().getIntProperty("num_batches", 7);
			setBatch(Math.min(_stats._batch+1, num_batches));
		}
	}
	
	public int compareTo (Entry e)	{
		return _id.compareTo(e.getID());
	}

	public boolean equals (Object o) {
		if (o == null || ! (o instanceof Entry))
			return false;
		else
			return this.compareTo((Entry) o) == 0;
	}

	public String toString () {
		String s = _orthography;
		if (_pronunciations.size() > 0)
			s += " ("
				+ Utils.arrayToString(_pronunciations.toArray(), ", ")  + ")";
			
		return s;
	}
	
	/**
	 * Creates a clone of depth one. This means that all members are cloned
	 * but not the members of the members (in this case the instances of class
	 * Sense that are contained in the vector <i>_senses</i> are not cloned).
	 */
	public Object clone() {
		Entry entry = new Entry(_id);
		entry._orthography = new String(_orthography);
		entry._senses.addAll(_senses);
		entry._sense_set.addAll(_sense_set);
		entry._categories.addAll(_categories);
		entry._lesson = new String(_lesson);
		if (_class == null)
			entry._class = null;
		else
			entry._class = (EntryClass) _class.clone();
		entry._mm_files.addAll(_mm_files);
		entry._pronunciations.addAll(_pronunciations);
		entry._stats = (Stats) _stats.clone();
		
		return entry;
	}
	
	/**
	 * Creates a copy of the entry like {@link Entry#clone()} does,
	 * but it also clones the senses. 
	 */
	public Entry createDeepCopy()
	{
		Entry entry = (Entry) clone();
		Sense[] senses = entry.getSenses();
		
		entry._senses.clear();
		entry._sense_set.clear();
		for (int i=0; i<senses.length; i++)
			try {
				entry.addSense((Sense) senses[i].clone());
			} catch (DictException e) {} /* No exception should be thrown */
		
		return entry;
	}
}
