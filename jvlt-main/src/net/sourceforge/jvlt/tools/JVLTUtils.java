package net.sourceforge.jvlt.tools;

import java.io.*;
import java.util.*;

import net.sourceforge.jvlt.Dict;
import net.sourceforge.jvlt.DictReaderException;
import net.sourceforge.jvlt.Entry;
import net.sourceforge.jvlt.Example;
import net.sourceforge.jvlt.JVLT;
import net.sourceforge.jvlt.JVLTModel;
import net.sourceforge.jvlt.MetaData;
import net.sourceforge.jvlt.Sense;

public class JVLTUtils {
	private JVLTModel _model;
	private String _current_file;
	private Dict _dict;
	
	public JVLTUtils() {
		JVLT jvlt = new JVLT();
		jvlt.init();
		_model = jvlt.getModel();
		_current_file = "";
		_dict = null;
	}

	public void print(String file_name, String field) {
		try {
			open(file_name);
			HashMap<String, TreeSet<Entry>> entry_map =
				new HashMap<String, TreeSet<Entry>>();
			TreeSet<String> value_set = new TreeSet<String>();
			MetaData data = _model.getDictModel().getMetaData(Entry.class);
			for (Iterator<Entry> it=_dict.getEntries().iterator();
					it.hasNext(); ) {
				Entry entry = it.next();
				String value=data.getAttribute(field).getFormattedValue(entry);
				if (value_set.add(value))
					entry_map.put(value, new TreeSet<Entry>());
				
				Set<Entry> set = entry_map.get(value);
				set.add(entry);
			}

			Writer writer = new BufferedWriter(
				new OutputStreamWriter(System.out, "UTF-8"));
			writer.write("<html>\n");
			Iterator<String> it = value_set.iterator();
			int i=0;
			char last_first_char = 0;
			while (it.hasNext()) {
				String value = (String) it.next();
				char first_char = value.length() == 0 ? 0 : value.charAt(0);
				Set<Entry> set = entry_map.get(value);
				Iterator<Entry> it2 = set.iterator();
				while (it2.hasNext()) {
					if (i>0)
						writer.write(", ");
					
					if (first_char != 0 && first_char != last_first_char) {
						writer.write("<b>" + Character.toUpperCase(first_char)
							+ "</b> ");
						last_first_char = first_char;
					}
					
					Entry entry = (Entry) it2.next();
					writer.write(entry.getOrthography());
					i++;
				}
			}
			writer.write("</html>\n");
			writer.flush();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void findDuplicates(String file_name) {
		try {
			open(file_name);
			Writer writer = new BufferedWriter(
				new OutputStreamWriter(System.out, "UTF-8"));
			Set<String> set = new TreeSet<String>();
			for (Iterator<Entry> it=_dict.getEntries().iterator();
					it.hasNext(); ) {
				String orth = it.next().getOrthography();
				if (! set.add(orth))
					writer.write("Duplicate: " + orth + "\n");
			}

			writer.flush();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void exampleLinkCount(String file_name) {
		try {
			open(file_name);
			Writer writer = new BufferedWriter(
				new OutputStreamWriter(System.out, "UTF-8"));
			for (Iterator<Example> it=_dict.getExamples().iterator();
					it.hasNext(); ) {
				Example ex = it.next();
				Sense[] senses = ex.getSenses();
				writer.write(ex.getText() + ": " + senses.length + " link(s)");

				Example.TextFragment[] fragments = ex.getTextFragments();
				TreeSet<Sense> set = new TreeSet<Sense>();
				for (int j=0; j<fragments.length; j++) {
					Sense s = fragments[j].getSense(); 
					if (s != null)
						if (! set.add(s))
							writer.write(", duplicate link: " + s.getParent());
				}
				
				writer.write("\n");
			}

			writer.flush();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void importStats(String target, String source) {
		try {
			open(target);
			JVLTModel statsModel = new JVLTModel();
			Dict statsDict = open(source, statsModel);
			for (Entry e: _dict.getEntries()) {
				Entry statsEntry = statsDict.getEntry(e);
				if (statsEntry != null)
					e.setStats(statsEntry.getStats());
			}
			_model.save(_current_file);
		} catch (DictReaderException e) {
			System.err.println(e.getShortMessage()
					+ "(" + e.getLongMessage() + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void open(String file_name)
		throws DictReaderException, IOException {
		if (file_name.equals(_current_file))
			return;
		
		_dict = open(file_name, _model);
		_current_file = file_name;
	}
	
	private Dict open(String file_name, JVLTModel model)
			throws DictReaderException, IOException {
		model.load(file_name);
		return model.getDict();
	}

	public static void main (String[] args) {
		if (args.length < 2) {
			printHelp();
			return;
		}

		String file = args[args.length-1];
		List<String> arg_set = new ArrayList<String>();
		for (int i=0; i<args.length; i++)
			arg_set.add(args[i]);
		
		JVLTUtils utils = new JVLTUtils();
		if (arg_set.contains("--find-duplicates")) {
			utils.findDuplicates(file);
		} else if (arg_set.contains("--example-link-count")) {
			utils.exampleLinkCount(file);
		} else if (arg_set.contains("--print-words")) {
			Iterator<String> it = arg_set.iterator();
			String field = "Orthography";
			while (it.hasNext()) {
				String arg = (String) it.next();
				if (arg.startsWith("--sort-by="))
					if (arg.endsWith("Pronunciations")) {
						field = "Pronunciations";
						break;
					}
			}
			utils.print(file, field);
		} else if (arg_set.contains("--import-stats")) {
			int index = arg_set.indexOf("--import-stats");
			if (index >= 0)
				utils.importStats(file, arg_set.get(index + 1));
		} else {
			printHelp();
		}
	}
	
	private static void printHelp() {
		System.out.println("Usage: JVLTUtils <options> <file>");
		System.out.println("Possible options:");
		System.out.println("--print-words");
		System.out.println("--find-duplicates");
		System.out.println("--example-link-count");
		System.out.println("--sort-by=<field>"
			+ "  Can be used together with --print-words. <field> is "
			+ "  either \"Orthography\" or \"Pronunciations\"");
		System.out.println("--import-stats <file>");
	}
}
