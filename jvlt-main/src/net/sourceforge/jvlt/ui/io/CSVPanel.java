package net.sourceforge.jvlt.ui.io;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.SortedMap;

import javax.swing.JPanel;

import net.sourceforge.jvlt.ui.components.LabeledComboBox;
import net.sourceforge.jvlt.ui.utils.GUIUtils;

public abstract class CSVPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private enum FieldDelimiter {
		COMMA(',', ","),
		SEMICOLON(';', ";"),
		COLON(':', ":"),
		SPACE(' ', GUIUtils.getString("Labels", "space")),
		TAB('\t', GUIUtils.getString("Labels", "tab"));
		
		private char 	_character;
		private String 	_description;
		
		private FieldDelimiter(char character, String description) {
			_character = character;
			_description = description;
		}
		
		public char getCharacter() { return _character; }
		
		@Override
		public String toString() { return _description; }
	}

	protected LabeledComboBox _text_delim_box;
	protected LabeledComboBox _field_delim_box;
	protected LabeledComboBox _charset_box;

	public CSVPanel() {
		initComponents();
		initLayout();
	}

	public char getTextDelimiter() {
		return _text_delim_box.getSelectedItem().toString().charAt(0);
	}

	public char getFieldDelimiter() {
		return ((FieldDelimiter) _field_delim_box.getSelectedItem())
				.getCharacter();
	}

	public String getCharset() {
		return _charset_box.getSelectedItem().toString();
	}

	protected void initComponents() {
		_text_delim_box = new LabeledComboBox();
		_text_delim_box.setLabel("text_delimiter");
		_text_delim_box.addItem("\"");
		_text_delim_box.addItem("'");

		_field_delim_box = new LabeledComboBox();
		_field_delim_box.setLabel("field_delimiter");
		for (FieldDelimiter delim: FieldDelimiter.values())
			_field_delim_box.addItem(delim);

		_charset_box = new LabeledComboBox();
		_charset_box.setLabel("charset");
		SortedMap<String, Charset> charsets = java.nio.charset.Charset
				.availableCharsets();
		for (Iterator<String> it = charsets.keySet().iterator(); it.hasNext();) {
			_charset_box.addItem(it.next());
		}
		if (charsets.containsKey("UTF-8"))
			_charset_box.setSelectedItem("UTF-8");
	}

	protected abstract void initLayout();
}
