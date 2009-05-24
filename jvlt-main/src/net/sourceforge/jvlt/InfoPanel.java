package net.sourceforge.jvlt;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLDocument;
import javax.xml.parsers.*;

import net.sourceforge.jvlt.event.CopyPastePopupListener;
import net.sourceforge.jvlt.event.DictUpdateListener;
import net.sourceforge.jvlt.event.SelectionNotifier;

public class InfoPanel extends JPanel
	implements DictUpdateListener, HyperlinkListener {
	private static final long serialVersionUID = 1L;
	
	protected SelectionNotifier _notifier;
	protected Dict _dict;
	protected JVLTModel _model;
	protected DocumentBuilder _builder;
	
	private JEditorPane _html_pane;
	
	public InfoPanel(JVLTModel model, SelectionNotifier notifier) {
		_notifier = notifier;
		_model = model;
		_dict = null;
		
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		try {
			_builder = fac.newDocumentBuilder();
		}
		catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		}

		model.getDictModel().addDictUpdateListener(this);
		model.getQueryModel().addDictUpdateListener(this);
		
		init();
	}
	
	public synchronized void dictUpdated(DictUpdateEvent ev) {
		if (ev instanceof NewDictDictUpdateEvent)
			_dict = ((NewDictDictUpdateEvent) ev).getDict();
	}
	
	public void hyperlinkUpdate(HyperlinkEvent ev) {}
	
	protected void setText(String text) {
		_html_pane.setText(text);
		_html_pane.setCaretPosition(0);
	}
	
	protected XSLTransformer createTransformer(String file)	{
		InputStream stream = EntryInfoPanel.class.getResourceAsStream(file);
		
		return new XSLTransformer(stream);
	}
	
	private void init() {
		_html_pane = new JEditorPane();
		_html_pane.setEditable(false);
		_html_pane.addHyperlinkListener(this);
		_html_pane.addMouseListener(new CopyPastePopupListener(_html_pane));
		_html_pane.setContentType("text/html");
		
		URL url = InfoPanel.class.getResource("/style.css");
		HTMLDocument doc = (HTMLDocument) _html_pane.getDocument();
		doc.setBase(Utils.getDirectory(url));
		
		JScrollPane spane = new JScrollPane(_html_pane);
		spane.setPreferredSize(new Dimension(400,300));
		setLayout(new GridLayout());
		add(spane);
	}
}
