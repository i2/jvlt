package net.sourceforge.jvlt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.jvlt.event.SelectionListener.SelectionEvent;
import net.sourceforge.jvlt.event.SelectionNotifier;

public class EntryInfoPanel extends InfoPanel implements HyperlinkListener {
	private static final long serialVersionUID = 1L;
	
	private XSLTransformer _entry_transformer;
	private Entry _current_entry = null;
	private Vector<Attribute> _entry_attributes = new Vector<Attribute>();
	private Vector<Attribute> _example_attributes = new Vector<Attribute>();
	
	public EntryInfoPanel(JVLTModel model, SelectionNotifier notifier) {
		super(model, notifier);
		
		_entry_transformer = createTransformer("/xml/entry.xsl");
		MetaData entry_data = model.getDictModel().getMetaData(
			Entry.class);
		_entry_attributes.addAll(Arrays.asList(entry_data.getAttributes()));
		MetaData example_data = model.getDictModel().getMetaData(
			Example.class);
		_example_attributes.addAll(Arrays.asList(example_data.getAttributes()));
	}
	
	public Entry getEntry() { return _current_entry; }
	
	public void setEntry(Entry entry) {
		_current_entry = entry;
		updateView();
	}
	
	public void setDisplayedEntryAttributes(String[] attr_names) {
		MetaData entry_data = _model.getDictModel().getMetaData(Entry.class);
		_entry_attributes.clear();
		for (int i=0; i<attr_names.length; i++) {
			Attribute attr = entry_data.getAttribute(attr_names[i]);
			if (attr == null)
				System.out.println("Warning: Attribute \""+attr_names[i]+
					"\" does not exist.");
			else
				_entry_attributes.add(entry_data.getAttribute(attr_names[i]));
		}
		updateView();
	}
	
	public void setDisplayedExampleAttributes(String[] attr_names) {
		MetaData example_data = _model.getDictModel().getMetaData(
			Example.class);
		_example_attributes.clear();
		for (int i=0; i<attr_names.length; i++) {
			Attribute attr = example_data.getAttribute(attr_names[i]);
			if (attr == null)
				System.out.println("Warning: Attribute \""+attr_names[i]+
					"\" does not exist.");
			else
				_example_attributes.add(attr);
		}
		updateView();
	}
	
	public synchronized void dictUpdated(DictUpdateEvent event) {
		if (event instanceof EntryDictUpdateEvent) {
			EntryDictUpdateEvent eevent = (EntryDictUpdateEvent) event;
			if (eevent.getType() == EntryDictUpdateEvent.ENTRIES_CHANGED) {
				updateView();
			} else if (event.getType() == EntryDictUpdateEvent.ENTRIES_REMOVED){
				if (_current_entry != null)
					if (eevent.getEntries().contains(_current_entry)) {
						_current_entry = null;
						updateView();
					}
			}
		} else if (event instanceof ExampleDictUpdateEvent) {
			updateView();
		} else if (event instanceof LanguageDictUpdateEvent) {
			_current_entry = null;
			updateView();
		} else if (event instanceof NewDictDictUpdateEvent) {
			_current_entry = null;
			updateView();
			super.dictUpdated(event);
		}
	}
	
	public void hyperlinkUpdate(HyperlinkEvent ev) {
		if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String descr = ev.getDescription();
			if (descr.length() < 1)
				return;
			
			if (descr.startsWith("e")) {
				String id = descr.substring(0, descr.indexOf('-'));
				Entry entry = _dict.getEntry(id);
				if (entry != null)
					_notifier.fireSelectionEvent(
						new SelectionEvent(entry, this));
			}
			else if (descr.startsWith("x")) {
				Example example = _dict.getExample(descr);
				if (example != null)
					_notifier.fireSelectionEvent(
						new SelectionEvent(example, this));
			}
			else if (descr.startsWith("mm:")) {
				String file_name = descr.substring(3, descr.length());
				MultimediaFile file = MultimediaUtils.getMultimediaFileForName(
					file_name);
				if (file != null)
					_notifier.fireSelectionEvent(
						new SelectionEvent(file, this));
			}
		}
	}
	
	private void updateView() {
		if (_current_entry == null) {
			setText("");
			return;
		}
		
		Document doc = _builder.newDocument();
		Element root = doc.createElement("Dict");
		doc.appendChild(root);
		DictObjectFormatter dof = new DictObjectFormatter(doc);
		root.appendChild(dof.getElementForObject(
			_current_entry, _entry_attributes.toArray(new Attribute[0])));
		Collection<Example> examples = _dict.getExamples(_current_entry);
		for (Iterator<Example> it=examples.iterator(); it.hasNext(); )
			root.appendChild(dof.getElementForObject(
				it.next(), _example_attributes.toArray(new Attribute[0])));
//		XMLWriter writer = new XMLWriter(System.out);
//		try { writer.write(doc); }
//		catch (java.io.IOException e) { e.printStackTrace(); }
		
		String html = _entry_transformer.transform(doc);
		// Because of a bug in JEditorPane, the content-type meta tag causes
		// an error. Therefore, all meta tags are removed.
		Pattern p = Pattern.compile("<META[^>]+>");
		Matcher m = p.matcher(html);
		html = m.replaceAll("");
//		System.out.println(html);
		setText(html);
	}
}

