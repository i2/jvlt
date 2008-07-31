package net.sourceforge.jvlt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class CSVImportTool {
	private CSVDictReader _reader;
	
	public CSVImportTool(Properties props) {
		String language = props.getProperty("language");
		boolean ignore_first_line = Boolean.valueOf(
			props.getProperty("ignore_first_line", "false")).booleanValue();
		int num_senses = Integer.parseInt(
			props.getProperty("senses", "1"));
		int num_categories = Integer.parseInt(
			props.getProperty("categories", "1"));
		int num_mmfiles = Integer.parseInt(
			props.getProperty("multimedia_files", "0"));
		int num_examples = Integer.parseInt(
			props.getProperty("examples", "0"));
		String[] attrs = Utils.split(props.getProperty("attributes", ""), ",");
		String[] attr_columns = Utils.split(
			props.getProperty("attribute_columns", ""), ",");
		int[] columns = new int[attrs.length];
		for (int i=0; i<columns.length; i++) {
			if (i>=attr_columns.length)
				columns[i] = 1;
			else
				columns[i] = Integer.parseInt(attr_columns[i]);
		}
		
		_reader = new CSVDictReader();
		_reader.setLanguage(language);
		_reader.setIgnoreFirstLine(ignore_first_line);
		_reader.setNumSenses(num_senses);
		_reader.setNumCategories(num_categories);
		_reader.setNumMultimediaFiles(num_mmfiles);
		_reader.setNumExamples(num_examples);
		_reader.setAttributes(attrs);
		_reader.setAttributeColumns(columns);
	}

	public Dict readDict(String filename)
		throws DictReaderException, IOException {
		_reader.read(new File(filename));
		return _reader.getDict();
	}
	
	public void writeDict(Dict dict, String filename) throws IOException {
		DictXMLWriter writer = new DictXMLWriter(
			dict, new FileOutputStream(filename));
		writer.write();
	}
	
	public static void main(String[] args) {
		if (args.length != 3)
			return;
		
		JVLT jvlt = new JVLT();
		jvlt.init();
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(args[0]));
			CSVImportTool tool = new CSVImportTool(props);
			Dict dict = tool.readDict(args[1]);
			tool.writeDict(dict, args[2]);
		}
		catch (IOException e) { e.printStackTrace(); }
		catch (DictReaderException e) {
			System.err.println("Importing failed. Reason:");
			System.err.println(e.getShortMessage());
			System.err.println(e.getLongMessage());
			e.printStackTrace();
		}
	}
}

