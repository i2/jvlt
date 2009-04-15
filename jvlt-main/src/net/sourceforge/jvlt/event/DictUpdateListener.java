package net.sourceforge.jvlt.event;

import java.util.Collection;

import net.sourceforge.jvlt.Dict;
import net.sourceforge.jvlt.Entry;
import net.sourceforge.jvlt.Example;

public interface DictUpdateListener {
	public static abstract class DictUpdateEvent {
		protected int _type;
		
		public DictUpdateEvent(int type) { _type = type; }
		
		public int getType() { return _type; }
	}
	
	public static class EntryDictUpdateEvent extends DictUpdateEvent {
		public static final int ENTRIES_ADDED = 1;
		public static final int ENTRIES_CHANGED = 2;
		public static final int ENTRIES_REMOVED = 3;
		
		private Collection<Entry> _examples;
		
		public EntryDictUpdateEvent(int type, Collection<Entry> entries) {
			super(type);
			_examples = entries;
		}
		
		public Collection<Entry> getEntries() { return _examples; }
	}
	
	public static class ExampleDictUpdateEvent extends DictUpdateEvent {
		public static final int EXAMPLES_ADDED = 1;
		public static final int EXAMPLES_CHANGED = 2;
		public static final int EXAMPLES_REMOVED = 3;
		
		private Collection<Example> _examples;
		
		public ExampleDictUpdateEvent(int type, Collection<Example> examples) {
			super(type);
			_examples = examples;
		}
		
		public Collection<Example> getExamples() { return _examples; }
	}
	
	public static class NewDictDictUpdateEvent extends DictUpdateEvent {
		private Dict _dict;
		
		public NewDictDictUpdateEvent(Dict dict) {
			super(0);
			_dict = dict;
		}
		
		public Dict getDict() { return _dict; }
	}
	
	public static class LanguageDictUpdateEvent extends DictUpdateEvent {
		private String _new_language;
		
		public LanguageDictUpdateEvent(String new_language) {
			super(0);
			_new_language = new_language;
		}
		
		public String getLanguage() { return _new_language; }
	}
	
	public void dictUpdated(DictUpdateEvent event);
}

