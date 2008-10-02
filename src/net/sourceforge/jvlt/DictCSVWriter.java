package net.sourceforge.jvlt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;

public class DictCSVWriter extends DictWriter {
	private char _text_delimiter;
	private char _field_delimiter;
	private String _charset;
	
	public DictCSVWriter(Dict dict, OutputStream stream) {
		super(dict, stream);
		_text_delimiter = '"';
		_field_delimiter = ',';
		_charset = "UTF-8";
	}
	
	public void setTextDelimiter(char value) { _text_delimiter = value; }
	
	public void setFieldDelimiter(char value) { _field_delimiter = value; }
	
	public void setCharset(String value) { _charset = value; }
	
	public void write() throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(_stream, _charset);
		Collection<Entry> entries = _dict.getEntries();
		for (Iterator<Entry> it=entries.iterator(); it.hasNext(); ) {
			Entry entry = it.next();
			
			writer.write(getField(entry.getOrthography()));
			writer.write(_field_delimiter);
			
			String[] pronunciations = entry.getPronunciations();
			if (pronunciations.length > 0)
				writer.write(getField(pronunciations[0]));
			else
				writer.write(getField(""));
			writer.write(_field_delimiter);
			
			writer.write(getField(entry.getLesson()));
			writer.write(_field_delimiter);
			
			// Only write the first category
			String[] categories = entry.getCategories();
			if (categories.length > 0)
				writer.write(getField(categories[0]));
			else
				writer.write(getField(""));
			
			Sense[] senses = entry.getSenses();
			for (int j=0; j<senses.length; j++) {
				writer.write(_field_delimiter);
				writer.write(getField(senses[j].getTranslation()));
				writer.write(_field_delimiter);
				writer.write(getField(senses[j].getDefinition()));
			}
			writer.write(System.getProperty("line.separator"));
		}
		writer.close();
	}
	
	private String getField(String value) {
		StringBuffer buf = new StringBuffer();
		buf.append(_text_delimiter);
		buf.append(value);
		buf.append(_text_delimiter);
		return buf.toString();
	}
}

