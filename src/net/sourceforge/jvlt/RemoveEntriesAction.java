package net.sourceforge.jvlt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class RemoveEntriesAction extends DictAction {
	private Dict _dict;
	private Collection<Entry> _entries;
	private TreeSet<Example> _removed_examples;
	private ArrayList<EditDictObjectAction> _example_actions;
	
	public RemoveEntriesAction (Dict dict, Collection<Entry> entries) {
		_dict = dict;
		_entries = entries;
		_removed_examples = new TreeSet<Example>();
		_example_actions = new ArrayList<EditDictObjectAction>();
		
		TreeSet<Entry> removed_entries = new TreeSet<Entry>();
		removed_entries.addAll(entries);
		
		TreeMap<Example, TreeSet<Entry>> example_map
									= new TreeMap<Example, TreeSet<Entry>>();
		for (Iterator<Entry> it=entries.iterator(); it.hasNext(); ) {
			Entry entry = it.next();
			Collection<Example> examples = _dict.getExamples(entry);
			for (Iterator<Example> eit=examples.iterator(); eit.hasNext(); ) {
				Example example = eit.next();
				if (! example_map.containsKey(example)) {
					// Add all linked entries except for the current entry.
					TreeSet<Entry> entry_set = new TreeSet<Entry>();
					Sense[] senses = example.getSenses();
					for (int k=0; k<senses.length; k++)
						entry_set.add(senses[k].getParent());
					entry_set.remove(entry);
					example_map.put(example, entry_set);
				}
				else {
					TreeSet<Entry> entry_set = example_map.get(example);
					entry_set.remove(entry);
				}
			}
		}
		
		for (Iterator<Example> it=example_map.keySet().iterator();
			it.hasNext(); ) {
			Example example = (Example) it.next();
			TreeSet<Entry> entry_set = example_map.get(example);
			if (entry_set.size() == 0)
				_removed_examples.add(example);
			else {
				Example new_example = (Example) example.clone();
				Example.TextFragment[] fragments
					= new_example.getTextFragments();
				for (int i=0; i<fragments.length; i++) {
					Example.TextFragment f = fragments[i];
					if (f.getSense() != null &&
						removed_entries.contains(f.getSense().getParent()))
						f.setSense(null);
				}
				_example_actions.add(new EditDictObjectAction(
					example, new_example));
			}
		}
	}

	public Collection<Entry> getRemovedEntries() { return _entries; }
	
	public Collection<Example> getRemovedExamples() {
		return  _removed_examples;
	}
		
	public Collection<Example> getModifiedExamples() {
		TreeSet<Example> examples = new TreeSet<Example>();
		for (Iterator<EditDictObjectAction> it=_example_actions.iterator();
			it.hasNext(); )
			examples.add((Example) it.next().getObject());
			
		return examples;
	}
	
	public void executeAction() throws DictException {
		for (Iterator<EditDictObjectAction> it=_example_actions.iterator();
			it.hasNext(); )
			it.next().executeAction();
		
		for (Iterator<Example> it=_removed_examples.iterator(); it.hasNext(); )
			_dict.removeExample(it.next());
		
		for (Iterator<Entry> it=_entries.iterator(); it.hasNext(); )
			_dict.removeEntry(it.next());
	}
	
	public void undoAction() throws DictException {
		for (Iterator<Entry> it=_entries.iterator(); it.hasNext(); )
			_dict.addEntry(it.next());
		
		for (Iterator<EditDictObjectAction> it=_example_actions.iterator();
			it.hasNext(); )
			it.next().undoAction();
		
		for (Iterator<Example> it=_removed_examples.iterator();
			it.hasNext(); )
			_dict.addExample(it.next());
	}
}
