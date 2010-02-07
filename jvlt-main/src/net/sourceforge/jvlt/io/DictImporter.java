package net.sourceforge.jvlt.io;

import java.util.Collection;
import java.util.TreeSet;

import net.sourceforge.jvlt.actions.ImportAction;
import net.sourceforge.jvlt.core.Dict;
import net.sourceforge.jvlt.core.DictException;
import net.sourceforge.jvlt.core.Entry;
import net.sourceforge.jvlt.core.Example;
import net.sourceforge.jvlt.core.Sense;
import net.sourceforge.jvlt.core.Example.TextFragment;
import net.sourceforge.jvlt.model.JVLTModel;
import net.sourceforge.jvlt.ui.utils.GUIUtils;
import net.sourceforge.jvlt.utils.AttributeResources;

public class DictImporter {
	private final JVLTModel _model;
	private boolean _clear_stats = false;

	public DictImporter(JVLTModel model) {
		_model = model;
	}

	public void setClearStats(boolean clear) {
		_clear_stats = clear;
	}

	/**
	 * Determine the entries that can be imported. Only entries that do not
	 * already exist in the dictionary will be imported.
	 *
	 * @param dict The dictionary that will be imported.
	 */
	public Collection<Entry> getImportedEntries(Dict dict) {
		TreeSet<Entry> new_entries = new TreeSet<Entry>(new Entry.Comparator());
		Collection<Entry> entries = dict.getEntries();
		for (Entry entry : entries) {
			if (_model.getDict().getEntry(entry) == null) {
				new_entries.add(entry);
			}
		}

		return new_entries;
	}

	/**
	 * Determine the examples that can be added to the dictionary. Examples that
	 * already exist in the dictionary cannot be imported. The same is true for
	 * examples whose linked entries cannot be imported.
	 *
	 * @param dict The dictionary that will be imported.
	 * @param entries The entries that will be imported. They can be obtained by
	 *            the method {@link #getImportedEntries(Dict)}.
	 */
	public Collection<Example> getImportedExamples(Dict dict,
			Collection<Entry> entries) {
		TreeSet<Entry> entry_set = new TreeSet<Entry>();
		entry_set.addAll(entries);
		TreeSet<Example> new_examples = new TreeSet<Example>(
				new Example.Comparator());
		Collection<Example> examples = dict.getExamples();
		for (Example example : examples) {
			// Only import examples that do not already exist in the dictionary.
			if (_model.getDict().getExample(example) == null) {
				Sense[] senses = example.getSenses();
				boolean add_example = true;
				// Only add example if all senses are available
				for (int j = 0; j < senses.length; j++) {
					if (!entry_set.contains(senses[j].getParent())) {
						Entry entry = _model.getDict().getEntry(
								senses[j].getParent());
						if (entry == null || entry.getSense(senses[j]) == null) {
							add_example = false;
							break;
						}
					}
				}
				if (add_example) {
					new_examples.add(example);
				}
			}
		}

		return new_examples;
	}

	public void importDict(Dict dict) throws DictException {
		String old_lang = _model.getDict().getLanguage();
		String new_lang = dict.getLanguage();
		if (new_lang == null) {
			new_lang = old_lang;
		} else if (old_lang != null && !new_lang.equals(old_lang)) {
			AttributeResources resources = new AttributeResources();
			throw new DictException("Invalid language: "
					+ resources.getString(new_lang));
		}

		Collection<Entry> entries = getImportedEntries(dict);
		TreeSet<Entry> entry_set = new TreeSet<Entry>();
		entry_set.addAll(entries);
		Collection<Example> examples = getImportedExamples(dict, entries);

		// -----
		// Prepare the entries
		// -----
		if (_clear_stats) {
			for (Entry entry : entries) {
				entry.resetStats();
			}
		}

		// -----
		// Prepare the examples
		// -----
		for (Example example : examples) {
			Example.TextFragment[] fragments = example.getTextFragments();
			for (TextFragment fragment : fragments) {
				Example.TextFragment tf = fragment;
				if (tf.getSense() == null) {
					continue;
				}

				if (!entry_set.contains(tf.getSense().getParent())) {
					// Replace links to senses in the imported dictionary with
					// links to senses in the original dictionary.
					Entry entry = _model.getDict().getEntry(
							tf.getSense().getParent());
					tf.setSense(entry.getSense(tf.getSense()));
				}
			}
		}

		ImportAction ia = new ImportAction(old_lang, new_lang, entries,
				examples);
		ia.setMessage(GUIUtils.getString("Actions", "import_dict"));
		_model.getDictModel().executeAction(ia);
	}
}
