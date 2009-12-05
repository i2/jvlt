package net.sourceforge.jvlt;

import java.util.*;

import net.sourceforge.jvlt.event.DictUpdateListener;
import net.sourceforge.jvlt.event.DictUpdateListener.*;

public class DictModel extends AbstractModel {
	private EntryMetaData _entry_data;
	private ExampleMetaData _example_data;
	private LinkedList<DictUpdateListener> _dict_update_listeners;
	
	public DictModel() {
		_entry_data = new EntryMetaData();
		_example_data = new ExampleMetaData();
		_dict_update_listeners = new LinkedList<DictUpdateListener>();
	}
	
	/**
	 * Behaves like the corresponding method from AbstractModel except
	 * for emitting a DictUpdateEvent.
	 */
	public void setDict(Dict dict) {
		String old_lang = _dict==null ? null : _dict.getLanguage();
		super.setDict(dict);

		// This step is needed to ensure that language-specific settings
		// are being loaded.
		try { setLanguage(old_lang, dict.getLanguage()); }
		catch (DictException e) {} // This should not happen

        fireDictUpdateEvent(new NewDictDictUpdateEvent(_dict));
	}

	public void addDictUpdateListener(DictUpdateListener listener) {
		synchronized (_dict_update_listeners) {
			_dict_update_listeners.add(listener);
		}
	}

	public void removeDictUpdateListener(DictUpdateListener listener) {
		synchronized (_dict_update_listeners) {
			_dict_update_listeners.remove(listener);
		}
	}
	
	public MetaData getMetaData(Class<? extends Object> cl) {
		if (cl.equals(Entry.class))
			return _entry_data;
		else if (cl.equals(Example.class))
			return _example_data;
		else 
			return new MetaData(cl);
	}
	
	protected void execute(UndoableAction a) throws ModelException {
		if (a instanceof AddDictObjectAction) {
			AddDictObjectAction action = (AddDictObjectAction) a;
			Object obj = action.getObject();
			if (obj instanceof Entry) {
				try {
					ArrayList<Entry> entries = new ArrayList<Entry>();
					entries.add((Entry) obj);
					addEntries(entries);
				}
				catch (DictException e) {
					throw new ModelException(e.getMessage());
				}	
			} else if (obj instanceof Example) {
				try {
					ArrayList<Example> examples = new ArrayList<Example>();
					examples.add((Example) obj);
					addExamples(examples);
				}
				catch (DictException e) {
					throw new ModelException(e.getMessage());
				}	
			}
		} else if (a instanceof EditDictObjectAction) {
			EditDictObjectAction action = (EditDictObjectAction) a;
			try {
				action.executeAction();
				Object obj = action.getObject();
				if (obj instanceof Example) {
					Example example = (Example) obj;
					ArrayList<Example> examples = new ArrayList<Example>();
					examples.add(example);
					fireDictUpdateEvent(new ExampleDictUpdateEvent(
						ExampleDictUpdateEvent.EXAMPLES_CHANGED, examples));
				}
			} catch (DictException e) {
				throw new ModelException(e.getMessage());
			}
		} else if (a instanceof EditEntriesAction) {
			EditEntriesAction eea = (EditEntriesAction) a;
			try { eea.executeAction(); }
			catch (DictException e) {
				throw new ModelException(e.getMessage());
			}
			fireDictUpdateEvent(new EntryDictUpdateEvent(
					EntryDictUpdateEvent.ENTRIES_CHANGED, eea.getEntries()));
		} else if (a instanceof RemoveEntriesAction) {
			RemoveEntriesAction rea = (RemoveEntriesAction) a;
			try { rea.executeAction(); }
			catch(DictException e) { throw new ModelException(e.getMessage()); }
			fireDictUpdateEvent(new ExampleDictUpdateEvent(
				ExampleDictUpdateEvent.EXAMPLES_CHANGED,
				rea.getModifiedExamples()));
			fireDictUpdateEvent(new ExampleDictUpdateEvent(
				ExampleDictUpdateEvent.EXAMPLES_REMOVED,
				rea.getRemovedExamples()));
			fireDictUpdateEvent(new EntryDictUpdateEvent(
				EntryDictUpdateEvent.ENTRIES_REMOVED,
				rea.getRemovedEntries()));
		} else if (a instanceof RemoveExamplesAction) {
			RemoveExamplesAction rea = (RemoveExamplesAction) a;
			removeExamples(rea.getExamples());
		} else if (a instanceof ImportAction) {
			try {
				ImportAction ia = (ImportAction) a;
				String newlang = ia.getNewLanguage();
				String oldlang = ia.getOldLanguage();
				setLanguage(oldlang, newlang);
				fireDictUpdateEvent(new LanguageDictUpdateEvent(newlang));
				addEntries(ia.getEntries());
				addExamples(ia.getExamples());
			} catch (DictException e) {
				throw new ModelException(e.getMessage());
			}
		} else if (a instanceof LanguageChangeAction) {
			LanguageChangeAction lca = (LanguageChangeAction) a;
			try {
				setLanguage(lca.getOldLanguage(), lca.getNewLanguage());
				fireDictUpdateEvent(new LanguageDictUpdateEvent(
					lca.getNewLanguage()));
			} catch (DictException e) { e.printStackTrace(); }
		}
		
		_executed_actions++;
	}
	
	protected void undo(UndoableAction a) {
		if (a instanceof AddDictObjectAction) {
			AddDictObjectAction action = (AddDictObjectAction) a;
			Object obj = action.getObject();
			if (obj instanceof Entry) {
				try {
					ArrayList<Entry> entries = new ArrayList<Entry>();
					entries.add((Entry) obj);
					removeEntries(entries);
				} catch (DictException e) {
					throw new ModelException(e.getMessage());
				}
			} else if (obj instanceof Example) {
				ArrayList<Example> examples = new ArrayList<Example>();
				examples.add((Example) obj);
				removeExamples(examples);
			}
		} else if (a instanceof EditDictObjectAction) {
			EditDictObjectAction action = (EditDictObjectAction) a;
			try {
				action.undoAction();
				Object obj = action.getObject();
				if (obj instanceof Example) {
					Example example = (Example) obj;
					ArrayList<Example> examples = new ArrayList<Example>();
					examples.add(example);
					fireDictUpdateEvent (new ExampleDictUpdateEvent(
						ExampleDictUpdateEvent.EXAMPLES_CHANGED, examples));
				}
			} catch (DictException e) {
				throw new ModelException(e.getMessage());
			}
		} else if (a instanceof EditEntriesAction) {
			EditEntriesAction eea = (EditEntriesAction) a;
			try { eea.undoAction(); }
			catch (DictException e) {
				throw new ModelException(e.getMessage());
			}
			fireDictUpdateEvent(new EntryDictUpdateEvent(
					EntryDictUpdateEvent.ENTRIES_CHANGED, eea.getEntries()));
		} else if (a instanceof RemoveEntriesAction) {
			RemoveEntriesAction rea = (RemoveEntriesAction) a;
			try { rea.undoAction(); }
			catch(DictException e) { throw new ModelException(e.getMessage()); }
			fireDictUpdateEvent(new ExampleDictUpdateEvent(
				ExampleDictUpdateEvent.EXAMPLES_ADDED,
				rea.getRemovedExamples()));
			fireDictUpdateEvent(new ExampleDictUpdateEvent(
				ExampleDictUpdateEvent.EXAMPLES_CHANGED,
				rea.getModifiedExamples()));
			fireDictUpdateEvent(new EntryDictUpdateEvent(
				EntryDictUpdateEvent.ENTRIES_ADDED,
				rea.getRemovedEntries()));
		} else if (a instanceof RemoveExamplesAction) {
			RemoveExamplesAction rea = (RemoveExamplesAction) a;
			try { addExamples(rea.getExamples()); }
			catch(DictException e) { throw new ModelException(e.getMessage()); }
		} else if (a instanceof ImportAction) {
			ImportAction ia = (ImportAction) a;
			removeExamples(ia.getExamples());
			try {
				removeExamples(ia.getExamples());
				removeEntries(ia.getEntries());
				String newlang = ia.getNewLanguage();
				String oldlang = ia.getOldLanguage();
				setLanguage(newlang, oldlang);
				fireDictUpdateEvent(new LanguageDictUpdateEvent(oldlang));
			} catch (DictException e) {
				throw new ModelException(e.getMessage());
			}
		}
		else if (a instanceof LanguageChangeAction) {
			LanguageChangeAction lca = (LanguageChangeAction) a;
			try {
				setLanguage(lca.getNewLanguage(), lca.getOldLanguage());
				fireDictUpdateEvent(new LanguageDictUpdateEvent(
					lca.getOldLanguage()));
			} catch (DictException e) { e.printStackTrace(); }
		}
		
		_executed_actions--;
	}
	
	private void addEntries(Collection<Entry> entries) throws DictException {
		for (Iterator<Entry> it=entries.iterator(); it.hasNext(); ) {
			Entry entry = it.next();
			// Set the DateAdded attribute if it has not been set yet.
			if (entry.getDateAdded() == null)
				entry.setDateAdded(new GregorianCalendar());
			_dict.addEntry(entry);
		}
		
		fireDictUpdateEvent (new EntryDictUpdateEvent(
			EntryDictUpdateEvent.ENTRIES_ADDED, entries));
	}
	
	private void removeEntries(Collection<Entry> entries) throws DictException {
		for (Iterator<Entry> it=entries.iterator(); it.hasNext(); )
			_dict.removeEntry(it.next());
		
		fireDictUpdateEvent (new EntryDictUpdateEvent(
			EntryDictUpdateEvent.ENTRIES_REMOVED, entries));
	}
	
	private void addExamples(Collection<Example> examples) throws DictException {
		for (Iterator<Example> it=examples.iterator(); it.hasNext(); )
			_dict.addExample(it.next());
		
		fireDictUpdateEvent (new ExampleDictUpdateEvent(
			ExampleDictUpdateEvent.EXAMPLES_ADDED, examples));
	}

	private void removeExamples(Collection<Example> examples) {
		for (Iterator<Example> it=examples.iterator(); it.hasNext(); )
			_dict.removeExample(it.next());
		
		fireDictUpdateEvent (new ExampleDictUpdateEvent(
			ExampleDictUpdateEvent.EXAMPLES_REMOVED, examples));
	}

	private void setLanguage(String oldlang, String newlang)
		throws DictException {
		Object[] displayed_attributes = (Object[])
			JVLT.getRuntimeProperties().get("displayed_attributes");
		if (displayed_attributes != null) {
			String key = (oldlang==null||oldlang.equals("")) ?
				"displayed_attributes" : ("displayed_attributes_"+oldlang);
			JVLT.getConfig().setProperty(key, displayed_attributes);
		}

		_dict.setLanguage(newlang);
		_entry_data.setAttributeSchema(_dict.getEntryAttributeSchema());

		String key = (newlang==null||newlang.equals("")) ?
			"displayed_attributes" : ("displayed_attributes_"+newlang);
		String[] displayed_attr_names = JVLT.getConfig().getStringListProperty(
			key, _entry_data.getAttributeNames());
		ArrayList<String> attrs = new ArrayList<String>();
		for (int i=0; i<displayed_attr_names.length; i++)
			if (_entry_data.getAttribute(displayed_attr_names[i])==null)
				System.out.println("Warning: Attribute \""+
					displayed_attr_names[i]+"\" does not exist.");
			else
				attrs.add(displayed_attr_names[i]);
		JVLT.getRuntimeProperties().put("displayed_attributes",
			attrs.toArray(new String[0]));
	}

	private void fireDictUpdateEvent(DictUpdateEvent event) {
		if (event instanceof NewDictDictUpdateEvent) {
			Dict dict = ((NewDictDictUpdateEvent) event).getDict();
			// Update attributes
			updateAttributes(dict.getEntries(), true);
			
			// Update metadata
			_entry_data.setAttributeSchema(_dict.getEntryAttributeSchema());
		} else if (event instanceof EntryDictUpdateEvent) {
			EntryDictUpdateEvent eevent = (EntryDictUpdateEvent) event;
			if (eevent.getType() == EntryDictUpdateEvent.ENTRIES_ADDED
				|| eevent.getType() == EntryDictUpdateEvent.ENTRIES_CHANGED) {
				// Update attributes
				updateAttributes(eevent.getEntries(), false);
			}
		}
		
		synchronized (_dict_update_listeners) {
			Iterator<DictUpdateListener> it = _dict_update_listeners.iterator();
			while (it.hasNext())
				it.next().dictUpdated(event);
		}
	}
	
	private void updateAttributes(
			Collection<Entry> new_entries, boolean reset) {
		ChoiceAttribute categories_attr =
			(ChoiceAttribute) _entry_data.getAttribute("Categories");
		ChoiceAttribute custom_fields_attr =
			(ChoiceAttribute) _entry_data.getAttribute("CustomFields");
		ChoiceAttribute lesson_attr =
			(ChoiceAttribute) _entry_data.getAttribute("Lesson");
		
		// Reset attributes
		if (reset) {
			categories_attr.setValues(new String[0]);
			custom_fields_attr.setValues(new String[0]);
			lesson_attr.setValues(new String[0]);
		}
		
		
		// Add values
		for (Entry e: new_entries) {
			categories_attr.addValues(e.getCategories());
			lesson_attr.addValues(new String[] { e.getLesson() });
			for (StringPair p: e.getCustomFields())
				custom_fields_attr.addValues(new String[] { p.getFirst() });
		}
	}
}

