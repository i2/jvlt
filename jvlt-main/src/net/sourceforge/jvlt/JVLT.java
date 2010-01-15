package net.sourceforge.jvlt;

import java.io.*;
import java.util.*;
import javax.xml.xpath.*;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class JVLT {
	public static final String CONFIG_DIR;
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().startsWith("windows")) {
			CONFIG_DIR = System.getenv("APPDATA") + File.separator + "jvlt";
		} else if (os.toLowerCase().startsWith("mac os x")) {
			CONFIG_DIR = System.getProperty("user.home") + File.separator
					+ "Library" + File.separator + "Application Support"
					+ File.separator + "jvlt";
		} else {
			String dir = System.getenv("XDG_CONFIG_HOME");
			if (dir == null || dir.equals(""))
				dir = System.getProperty("user.home") + File.separator
						+ ".config";
			
			CONFIG_DIR = dir + File.separator + "jvlt";
		}
	}
	
	private static final String OLD_CONFIG_DIR =
		System.getProperty("user.home") + File.separator + ".jvlt"; // TODO: Remove

	private static final Locale[] _locales = {
		Locale.US, 
		Locale.FRANCE, 
		Locale.GERMANY, 
		new Locale("cs", "CZ"),
		new Locale("pl", "PL")
	};
	
	private JVLTModel _model;
	private static PropertyMap _runtime_properties;
	private static JVLT _instance = null;
	private static Config _config = null;

	private static String _version = null;
	private static String _data_version = null;

	public static JVLT getInstance() { return _instance; }
	
	public static Config getConfig() { return _config; }
	
	public static void saveConfig() throws IOException {
		String prop_file_name = CONFIG_DIR + File.separator + "config";
		FileOutputStream fos = new FileOutputStream(prop_file_name);
		_config.store(fos);
	}
	
	public static Locale[] getSupportedLocales() { return _locales; }

	/**
	 * Returns a map that contains a set of properties.
	 * Unlike the properties stored in the Config object returned by method
	 * getConfig(), this map is not stored in a config file. */
	public static PropertyMap getRuntimeProperties() {
		return _runtime_properties;
	}
	
	public static String getVersion() { return _version; }
	
	public static String getDataVersion() { return _data_version; }
	
	public JVLT() {
		_model = null;
		_config = null;
		_instance = this;
		_runtime_properties = new PropertyMap();
	}

	public JVLTModel getModel() { return _model; }
	
	public void init() {
		//----------
		// Read version info
		//----------
		InputStream xml_stream = JVLT.class.getResourceAsStream("/xml/info.xml");
		InputSource src = new InputSource(xml_stream);
		XPathFactory fac = XPathFactory.newInstance();
		XPath path = fac.newXPath();
		try {
			Node root = (Node) path.evaluate("/info", src, XPathConstants.NODE);
			_version = path.evaluate("version", root);
			_data_version = path.evaluate("data-version", root);
		}
		catch (XPathExpressionException ex) { ex.printStackTrace(); }

		//----------
		// Create .jvlt directory if necessary
		//----------
		File dir = new File(CONFIG_DIR);
		File old_dir = new File(OLD_CONFIG_DIR);
		if (dir.exists()) {
			if (! dir.isDirectory())
				System.out.println(dir.getPath() +
						" already exists but it is not a directory");
		} else {
			if (old_dir.exists()) {
				old_dir.renameTo(dir);
				System.out.println(old_dir.getPath()
						+ " migrated to " + dir.getPath());
			} else {
				if (! dir.mkdir())
					System.out.println(dir.getPath()
							+ " could not be created.");
			}
		}
		
		//----------
		// Read settings.
		//----------		
		_config = new Config();
		try	{
			String prop_file_name = dir.getPath() + File.separator + "config";
			FileInputStream fis = new FileInputStream(prop_file_name);
			_config.load(fis);
			fis.close();
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		_model = new JVLTModel();
		
		//----------
		// Set locale.
		//----------
		Locale loc = Locale.getDefault();
		Locale.setDefault(_config.getLocaleProperty("locale", loc));		
	}
}

