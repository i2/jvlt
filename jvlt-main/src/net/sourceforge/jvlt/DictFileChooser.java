package net.sourceforge.jvlt;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;

public class DictFileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;

	public enum FileType {
		JVLT_FILES("jvlt", "jvlt_files"),
		CSV_FILES("csv", "csv_files"),
		HTML_FILES("html", "html_files");
		
		private String extension;
		private String description;
		
		private FileType(String extension, String description) {
			this.extension = extension;
			this.description = description;
		}
		
		public String getExtension() { return this.extension; }
		
		public String getDescription() { return this.description; }
	}
	
	public DictFileChooser(String file_name) {
		this(file_name, FileType.JVLT_FILES);
	}

	public DictFileChooser(String file_name, FileType type) {
		if (file_name != null && ! file_name.equals("")) {
			File file = new File(file_name);
			File parent = file.getParentFile();
			if (parent != null)
				setCurrentDirectory(parent);
		}
		
		SimpleFileFilter filter = new SimpleFileFilter(
				GUIUtils.getString("Labels", type.getDescription()));
		filter.addExtension(type.getExtension());
		setFileFilter(filter);
	}
	
	public static String selectSaveFile(String file_name, FileType type,
			Component parent) {
		DictFileChooser chooser = new DictFileChooser(file_name, type);
		if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION)
			return null;
		
		String selected_file = chooser.getSelectedFile().getPath();
		
		/* Add extension if necessary */
		String extension = type.getExtension();
		if (selected_file.length() < extension.length() + 1
				|| ! selected_file.toLowerCase().endsWith("." + extension))
			selected_file = selected_file + "." + extension;
		
		return selected_file;
	}
}

