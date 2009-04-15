package net.sourceforge.jvlt;

public class SimpleEntryFilter extends EntryFilter {
	private StringQueryItem _orth_item;
	private ObjectArrayQueryItem _pron_item;
	private SenseArrayQueryItem _trans_item;
	private SenseArrayQueryItem _def_item;
	
	public SimpleEntryFilter() {
		super();
		_orth_item = new StringQueryItem(
			"Orthography", StringQueryItem.CONTAINS, "");
		_pron_item = new ObjectArrayQueryItem(
			"Pronunciations", ObjectArrayQueryItem.ITEM_CONTAINS, "");
		_trans_item = new SenseArrayQueryItem(
			SenseArrayQueryItem.TRANSLATION_CONTAINS, "");
		_def_item = new SenseArrayQueryItem(
			SenseArrayQueryItem.DEFINITION_CONTAINS, "");
		_query = new ObjectQuery(Entry.class);
		_query.setType(ObjectQuery.MATCH_ONE);
		_query.addItem(_orth_item);
		_query.addItem(_pron_item);
		_query.addItem(_trans_item);
		_query.addItem(_def_item);
	}
	
	public void setFilterString(String value) {
		_orth_item.setValue(value);
		_pron_item.setValue(value);
		_trans_item.setValue(value);
		_def_item.setValue(value);
	}

	public void setMatchCase(boolean match) {
		_orth_item.setMatchCase(match);
		_pron_item.setMatchCase(match);
		_trans_item.setMatchCase(match);
		_def_item.setMatchCase(match);
	}
}

