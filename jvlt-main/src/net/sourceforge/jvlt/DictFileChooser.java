package net.sourceforge.jvlt;

import java.io.File;
import javax.swing.JFileChooser;

public class DictFileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;

	public static final int JVLT_FILES = 0;
	public static final int CSV_FILES = 1;
	public static final int HTML_FILES = 2;
	
	public DictFileChooser(String file_name) {
		this(file_name, JVLT_FILES); }

	public DictFileChooser(String file_name, int type) {
		if (file_name != null && ! file_name.equals("")) {
			File file = new File(file_name);
			File parent = file.getParentFile();
			if (parent != null)
				setCurrentDirectory(parent);
		}
		
		SimpleFileFilter filter = null;
		if (type == JVLT_FILES) {
			filter = new SimpleFileFilter(
				GUIUtils.getString("Labels", "jvlt_files"));
			filter.addExtension("jvlt");
		} else if (type == CSV_FILES) {
			filter = new SimpleFileFilter(
				GUIUtils.getString("Labels", "csv_files"));
			filter.addExtension("csv");
		} else if (type == HTML_FILES) {
			filter = new SimpleFileFilter(
				GUIUtils.getString("Labels", "html_files"));
			filter.addExtension("html");
		}
		
		if (filter != null)
			setFileFilter(filter);
	}
}

