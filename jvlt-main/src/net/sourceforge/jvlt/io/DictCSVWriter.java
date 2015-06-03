package net.sourceforge.jvlt.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import net.sourceforge.jvlt.core.ArraySchemaAttribute;
import net.sourceforge.jvlt.core.Dict;
import net.sourceforge.jvlt.core.Entry;
import net.sourceforge.jvlt.core.EntryClass;
import net.sourceforge.jvlt.core.SchemaAttribute;
import net.sourceforge.jvlt.core.Sense;
import net.sourceforge.jvlt.utils.AttributeResources;
import net.sourceforge.jvlt.utils.I18nService;

public class DictCSVWriter extends DictWriter {
	private class DictInfo {
		public int num_senses = 0;
		public int num_categories = 0;
		public int num_mmfiles = 0;
		TreeMap<SchemaAttribute, Integer> num_attr_columns = new TreeMap<SchemaAttribute, Integer>();
	}

	private char _text_delimiter;
	private char _field_delimiter;
	private String _charset;

	public DictCSVWriter(Dict dict, OutputStream stream) {
		super(dict, stream);
		_text_delimiter = '"';
		_field_delimiter = ',';
		_charset = "UTF-8";
	}

	public void setTextDelimiter(char value) {
		_text_delimiter = value;
	}

	public void setFieldDelimiter(char value) {
		_field_delimiter = value;
	}

	public void setCharset(String value) {
		_charset = value;
	}

	@Override
	public void write() throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(_stream, _charset);
		Collection<Entry> entries = _dict.getEntries();
		DictInfo info = new DictInfo();

		for (Entry entry : entries) {
			info.num_senses = Math.max(entry.getSenses().length,
					info.num_senses);
			info.num_categories = Math.max(entry.getCategories().length,
					info.num_categories);
			info.num_mmfiles = Math.max(entry.getMultimediaFiles().length,
					info.num_mmfiles);

			EntryClass ec = entry.getEntryClass();
			if (ec != null) {
				SchemaAttribute[] attrs = ec.getAttributes();
				for (int i = 0; i < attrs.length; i++) {
					if (attrs[i] instanceof ArraySchemaAttribute) {
						ArraySchemaAttribute asa = (ArraySchemaAttribute) attrs[i];
						Object[] values = (Object[]) asa.getValue();
						if (!info.num_attr_columns.containsKey(attrs[i])) {
							info.num_attr_columns.put(attrs[i],
									values == null ? 1 : values.length);
						} else {
							info.num_attr_columns.put(attrs[i], Math.max(
									values == null ? 1 : values.length,
									info.num_attr_columns.get(attrs[i])));
						}
					} else {
						info.num_attr_columns.put(attrs[i], 1);
					}
				}
			}
		}

		// Write column headers
		StringBuilder builder = new StringBuilder();
		builder.append(getField(I18nService.getString("Labels", "original")));
		builder.append(_field_delimiter);
		builder.append(getField(I18nService.getString("Labels", "pronunciation")));
		builder.append(_field_delimiter);
		for (int i = 0; i < info.num_senses; i++) {
			builder.append(I18nService.getString("Labels", "translation") + " #"
					+ (i + 1));
			builder.append(_field_delimiter);
			builder.append(I18nService.getString("Labels", "definition") + " #"
					+ (i + 1));
			builder.append(_field_delimiter);
		}
		builder.append(getField(I18nService.getString("Labels", "lesson")));
		builder.append(_field_delimiter);
		for (int i = 0; i < info.num_categories; i++) {
			builder.append(I18nService.getString("Labels", "category") + " #"
					+ (i + 1));
			builder.append(_field_delimiter);
		}
		for (int i = 0; i < info.num_mmfiles; i++) {
			builder.append(I18nService.getString("Labels", "multimedia_file") + " #"
					+ (i + 1));
			builder.append(_field_delimiter);
		}
		builder.append(getField(I18nService.getString("Labels", "class")));
		builder.append(_field_delimiter);
		AttributeResources resources = new AttributeResources();
		for (SchemaAttribute attr : info.num_attr_columns.keySet()) {
			if (attr instanceof ArraySchemaAttribute) {
				int num_cols = info.num_attr_columns.get(attr);
				for (int i = 0; i < num_cols; i++) {
					builder.append(getField(resources.getString(attr.getName()))
							+ " #" + (i + 1));
					builder.append(_field_delimiter);
				}
			} else {
				builder.append(getField(resources.getString(attr.getName())));
				builder.append(_field_delimiter);
			}
		}
		
		// Remove last field delimiter
		builder.deleteCharAt(builder.length() - 1);

		builder.append(System.getProperty("line.separator"));
		
		writer.write(builder.toString());

		// Write entries
		for (Entry entry : entries) {
			writeEntry(writer, info, entry);
		}

		writer.close();
	}

	private void writeEntry(Writer writer, DictInfo info, Entry entry)
			throws IOException {
		StringBuilder builder = new StringBuilder();
		
		// Write orthography
		builder.append(getField(entry.getOrthography()));
		builder.append(_field_delimiter);

		// Write first pronunciation
		String[] pronunciations = entry.getPronunciations();
		if (pronunciations.length > 0) {
			builder.append(getField(pronunciations[0]));
		} else {
			builder.append(getField(""));
		}
		builder.append(_field_delimiter);

		// Write senses
		Sense[] senses = entry.getSenses();
		for (int i = 0; i < info.num_senses; i++) {
			builder.append(i < senses.length ? getField(senses[i]
					.getTranslation()) : getField(""));
			builder.append(_field_delimiter);
			builder.append(i < senses.length ? getField(senses[i]
							.getDefinition()) : getField(""));
			builder.append(_field_delimiter);
		}

		// Write lesson
		builder.append(getField(entry.getLesson()));
		builder.append(_field_delimiter);

		// Write categories
		String[] categories = entry.getCategories();
		for (int i = 0; i < info.num_categories; i++) {
			builder.append(i < categories.length ? getField(categories[i])
					: getField(""));
			builder.append(_field_delimiter);
		}

		// Write multimedia files
		String[] mmfiles = entry.getMultimediaFiles();
		for (int i = 0; i < info.num_mmfiles; i++) {
			builder.append(i < mmfiles.length ? getField(mmfiles[i])
					: getField(""));
			builder.append(_field_delimiter);
		}

		// TODO: Write examples

		// Write entry class
		EntryClass ec = entry.getEntryClass();
		builder.append(ec != null ? ec.getName() : getField(""));
		builder.append(_field_delimiter);

		// Write language-specific attributes
		if (ec != null) {
			Iterator<SchemaAttribute> it = info.num_attr_columns.keySet()
					.iterator();
			while (it.hasNext()) {
				SchemaAttribute attr = ec.getAttribute(it.next().getName());
				if (attr == null) {
					builder.append(_field_delimiter);
					continue;
				}

				if (attr instanceof ArraySchemaAttribute) {
					ArraySchemaAttribute asa = (ArraySchemaAttribute) attr;
					Object[] vals = (Object[]) asa.getValue();
					int num_cols = info.num_attr_columns.get(attr);
					for (int i = 0; i < num_cols; i++) {
						builder.append(getField(vals != null && i < vals.length
								? vals[i].toString() : ""));
						builder.append(_field_delimiter);
					}
				} else {
					Object val = attr.getValue();
					builder.append(getField(val != null ? val.toString() : ""));
					builder.append(_field_delimiter);
				}
			}
		}

		// Remove last field delimiter
		builder.deleteCharAt(builder.length() - 1);

		builder.append(System.getProperty("line.separator"));

		writer.write(builder.toString());
	}

	private String getField(String value) {
		StringBuffer buf = new StringBuffer();
		buf.append(_text_delimiter);
		buf.append(value);
		buf.append(_text_delimiter);
		return buf.toString();
	}
}
