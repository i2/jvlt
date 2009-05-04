package net.sourceforge.jvlt;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.SortedMap;
import javax.swing.JPanel;

public abstract class CSVPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected LabeledComboBox _text_delim_box;
	protected LabeledComboBox _field_delim_box;
	protected LabeledComboBox _charset_box;
	
	public CSVPanel() {
		initComponents();
		initLayout();
	}
	
	public char getTextDelimiter() {
		return _text_delim_box.getSelectedItem().toString().charAt(0); }

	public char getFieldDelimiter() {
		String space_string = GUIUtils.getString("Labels", "space");
		String item = _field_delim_box.getSelectedItem().toString();
		if (item.equals(space_string))
			return ' ';
		else if (item.equals(space_string))
			return '\t';
		else
			return item.charAt(0);
	}
	
	public String getCharset() {
		return _charset_box.getSelectedItem().toString(); }

	protected void initComponents() {
		_text_delim_box = new LabeledComboBox();
		_text_delim_box.setLabel("text_delimiter");
		_text_delim_box.addItem("\"");
		_text_delim_box.addItem("'");

		_field_delim_box = new LabeledComboBox();
		_field_delim_box.setLabel("field_delimiter");
		_field_delim_box.addItem(",");
		_field_delim_box.addItem(";");
		_field_delim_box.addItem(":");
		_field_delim_box.addItem(GUIUtils.getString("Labels", "space"));
		_field_delim_box.addItem(GUIUtils.getString("Labels", "tab"));

		_charset_box = new LabeledComboBox();
		_charset_box.setLabel("charset");
		SortedMap<String, Charset> charsets
			= java.nio.charset.Charset.availableCharsets();
		for (Iterator<String> it=charsets.keySet().iterator(); it.hasNext(); ) {
			_charset_box.addItem(it.next());
		}
		if (charsets.containsKey("UTF-8"))
			_charset_box.setSelectedItem("UTF-8");
	}
	
	protected abstract void initLayout();
}

