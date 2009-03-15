package net.sourceforge.jvlt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.jvlt.event.SelectionListener.SelectionEvent;
import net.sourceforge.jvlt.event.SelectionNotifier;

public class ExampleInfoPanel extends InfoPanel implements HyperlinkListener {
	private static final long serialVersionUID = 1L;
	
	private XSLTransformer _transformer;
	private Example _current_example;
	
	public ExampleInfoPanel(JVLTModel model, SelectionNotifier notifier) {
		super(model, notifier);
		
		_current_example = null;		
		_transformer = createTransformer("/example.xsl");
	}
	
	public void setExample(Example example) {
		_current_example = example;
		updateView();
	}
	
	public synchronized void dictUpdated(DictUpdateEvent event) {
		if (event instanceof EntryDictUpdateEvent) {
			updateView();
		} else if (event instanceof ExampleDictUpdateEvent) {
			ExampleDictUpdateEvent eevent = (ExampleDictUpdateEvent) event;
			if (eevent.getType() == ExampleDictUpdateEvent.EXAMPLES_CHANGED)
				updateView();
			else if (eevent.getType()
					== ExampleDictUpdateEvent.EXAMPLES_REMOVED) {
				if (_current_example != null)
					if (eevent.getExamples().contains(_current_example)) {
						_current_example = null;
						updateView();
					}
			}
		} else if (event instanceof LanguageDictUpdateEvent) {
			_current_example = null;
			updateView();
		} else if (event instanceof NewDictDictUpdateEvent) {
			_current_example = null;
			updateView();
			super.dictUpdated(event);
		}
	}
	
	public void hyperlinkUpdate(HyperlinkEvent ev) {
		if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String descr = ev.getDescription();
			String id = descr.substring(0, descr.indexOf('-'));
			Entry entry = _dict.getEntry(id);
			if (entry != null)
				_notifier.fireSelectionEvent(
					new SelectionEvent(entry, this));
		}
	}
	
	private void updateView() {
		if (_current_example == null) {
			setText("");
			return;
		}
		
		MetaData entry_data=_model.getDictModel().getMetaData(Entry.class);
		MetaData example_data=_model.getDictModel().getMetaData(Example.class);
		Document doc = _builder.newDocument();
		Element root = doc.createElement("Dict");
		doc.appendChild(root);
		DictObjectFormatter dof = new DictObjectFormatter(doc);
		root.appendChild(dof.getElementForObject(
			_current_example, example_data.getAttributes()));
		Sense[] senses = _current_example.getSenses();
		for (int i=0; i<senses.length; i++)
			root.appendChild(dof.getElementForObject(
				senses[i].getParent(), entry_data.getAttributes()));
		// XMLWriter writer = new XMLWriter(System.out);
		// try { writer.write(doc); }
		// catch (IOException e) { e.printStackTrace(); }
		
		String html = _transformer.transform(doc);
		// Because of a bug in JEditorPane, the content-type meta tag causes
		// an error. Therefore, all meta tags are removed.
		Pattern p = Pattern.compile("<META[^>]+>");
		Matcher m = p.matcher(html);
		html = m.replaceAll("");
		// System.out.println(html);
		setText(html);
	}
}

