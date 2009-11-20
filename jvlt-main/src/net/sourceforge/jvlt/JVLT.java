package net.sourceforge.jvlt;

import java.io.*;
import java.util.*;
import javax.xml.xpath.*;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class JVLT {
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
		String prop_file_name =
			System.getProperty("user.home") + File.separator + ".jvlt" +
			File.separator + "config";
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
		InputStream xml_stream = JVLT.class.getResourceAsStream("/info.xml");
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
		File dir = new File(System.getProperty("user.home") +
				File.separator + ".jvlt");
		if (dir.exists()) {
			if (! dir.isDirectory())
				System.out.println(dir.getPath() +
						" already exists but it is not a directory");
		} else {
			if (! dir.mkdir())
				System.out.println(dir.getPath() + " could not be created.");
		}
		
		//----------
		// Read settings.
		//----------		
		_config = new Config();
		try	{
			String prop_file_name = dir.getPath() + File.separator + "config";
			FileInputStream fis = new FileInputStream(prop_file_name);
			_config.load(new FileInputStream(prop_file_name));
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

