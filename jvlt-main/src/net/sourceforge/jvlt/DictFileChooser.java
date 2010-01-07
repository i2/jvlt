package net.sourceforge.jvlt;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;

public class DictFileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;

	public enum FileType {
		JVLT_FILES(new String[] { "jvlt", "jvlt.zip" }, "jvlt_files"),
		CSV_FILES(new String[] { "csv" }, "csv_files"),
		HTML_FILES(new String[] { "html" }, "html_files");
		
		private String[] extensions;
		private String   description;
		
		private FileType(String[] extensions, String description) {
			this.extensions = extensions;
			this.description = description;
		}
		
		public String[] getExtensions() { return this.extensions; }
		
		public String getDescription() { return this.description; }
	}
	
	public DictFileChooser(String file_name) {
		this(file_name, FileType.JVLT_FILES);
	}

	public DictFileChooser(String file_name, FileType type) {
		if (file_name != null && ! file_name.equals("")) {
			File file = new File(file_name);
			File parent = file.getParentFile();
			if (parent != null && file.isFile()) {
				setCurrentDirectory(parent);
			} else {
				setCurrentDirectory(file);
			}
		} else {
			setCurrentDirectory(new File("."));
		}
		
		SimpleFileFilter filter = new SimpleFileFilter(
				GUIUtils.getString("Labels", type.getDescription()));
		filter.setExtensions(type.getExtensions());
		setFileFilter(filter);
	}
	
	public static String selectSaveFile(String file_name, FileType type,
			Component parent) {
		DictFileChooser chooser = new DictFileChooser(file_name, type);
		if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION)
			return null;
		
		String selected_file = chooser.getSelectedFile().getPath();
		
		/* Add extension if necessary */
		boolean has_extension = false;
		for (String ext: type.getExtensions()) {
			if (selected_file.toLowerCase().endsWith("." + ext)) {
				has_extension = true;
				break;
			}
		}
		if (! has_extension)
			selected_file += "." + type.getExtensions()[0];
		
		return selected_file;
	}
}

