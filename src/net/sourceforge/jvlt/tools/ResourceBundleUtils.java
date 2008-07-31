package net.sourceforge.jvlt.tools;

import java.io.*;
import java.util.*;
import java.util.regex.*;

class ResourceBundleUtils
{
	private String[] _files;
	private String[] _languages;
	
	public ResourceBundleUtils(String[] files, String[] languages) {
		_files = files;
		_languages = languages;
	}
	
	public void sync() {
		for (int i=0; i<_files.length; i++) {
			PropertiesFile file = readFile(_files[i]+".properties");
			for (int j=0; j<_languages.length; j++) {
				String file_name = _files[i]+"_"+_languages[j]+".properties";
				PropertiesFile tfile = readFile(file_name);
				try {
					FileOutputStream fos = new FileOutputStream(file_name);
					OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF8");
					BufferedWriter writer = new BufferedWriter(osw);
					Line[] lines = file.getLines();
					int k=0;
					while (k<lines.length) {
						Line line = lines[k];
						String key = file.getKey(line);
						if (key == null) {
							k++;
							writer.write(line.content+"\n");
						} else {
							Line[] file_lines = file.getLines(key);
							k = file_lines[file_lines.length-1].index+1;
							if (tfile.containsKey(key)) {
								Line[] tfile_lines = tfile.getLines(key);
								for (int l=0; l<tfile_lines.length; l++)
									writer.write(tfile_lines[l].content+"\n");
							} else {
								for (int l=0; l<file_lines.length; l++)
									writer.write("# "+file_lines[l].content
										+"\n");
							}
						}
					}

					writer.close();
				} catch (Exception ex) { ex.printStackTrace(); }
			}
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("No path specified");
			System.exit(1);
		}
		
		ArrayList<String> files = new ArrayList<String>();
		files.add(args[0] + "/Actions");
		files.add(args[0] + "/Attributes");
		files.add(args[0] + "/Labels");
		files.add(args[0] + "/Messages");
		String[] languages = new String[]{ "de_DE", "cs_CZ" };
		ResourceBundleUtils utils = new ResourceBundleUtils(
				files.toArray(new String[0]), languages);
		utils.sync();
	}
	
	private PropertiesFile readFile(String filename) {
		PropertiesFile pf = new PropertiesFile();
		Pattern assign_pattern=Pattern.compile("([a-zA-Z0-9_-]+)\\s*=.*");
		Pattern empty_pattern=Pattern.compile("\\s*|#.*");
		File file = new File(filename);
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, "UTF8");
				BufferedReader reader = new BufferedReader(isr);
				String current_key = null;
				boolean new_key = true;
				for (int i=0; reader.ready(); i++) {
					String line = reader.readLine();
					if (new_key) {
						Matcher matcher = assign_pattern.matcher(line);
						if (! matcher.matches()) {
							if (empty_pattern.matcher(line).matches()) {
								pf.addLine(new Line(i, line), null);
								continue;
							} else
								throw new Exception("Invalid line: "+line);
						}
						current_key = matcher.group(1);
						pf.addLine(new Line(i, line), current_key);
						if (line.endsWith("\\"))
							new_key = false;
					} else {
						pf.addLine(new Line(i, line), current_key);
						if (! line.endsWith("\\")) {
							new_key = true;
							current_key = null;
						}
					}
				}
				reader.close();
			} catch (Exception ex) { ex.printStackTrace(); }
		}

		return pf;
	}
}

class Line implements Comparable<Line> {
	public int index;
	public String content;

	public Line(int i, String c) {
		this.index = i;
		this.content = c;
	}

	public int compareTo(Line line) {
		Line l = (Line) line;
		if (index != l.index)
			return index-l.index;
		else
			return content.compareTo(l.content);
	}
}

class PropertiesFile {
	private TreeMap<Line, String> _line_map;
	private TreeMap<String, Vector<Line>> _key_map;

	public PropertiesFile() {
		_line_map = new TreeMap<Line, String>();
		_key_map = new TreeMap<String, Vector<Line>>();
	}

	public void addLine(Line l, String k) {
		_line_map.put(l, k);
		if (k != null) {
			if (! _key_map.containsKey(k))
				_key_map.put(k, new Vector<Line>());

			Vector<Line> v = _key_map.get(k);
			v.add(l);
		}
	}

	public Line[] getLines() {
		return _line_map.keySet().toArray(new Line[0]);
	}

	public Line[] getLines(String key) {
		return _key_map.get(key).toArray(new Line[0]);
	}

	public boolean containsKey(String key) {
		return _key_map.containsKey(key);
	}

	public String getKey(Line line) {
		return _line_map.get(line);
	}
}

