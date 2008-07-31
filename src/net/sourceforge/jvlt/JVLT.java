package net.sourceforge.jvlt;

import java.io.*;
import java.util.*;
import javax.xml.xpath.*;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class JVLT {
	private JVLTModel _model;
	
	private static PropertyMap _runtime_properties;
	private static JVLT _instance = null;
	private static Config _config = null;
	private static Locale[] _locales = {
		Locale.US, Locale.GERMANY, new Locale("cs", "CZ") };
	private static String _version = null;
	private static String _data_version = null;

	public static JVLT getInstance() { return _instance; }
	
	public static Config getConfig() { return _config; }
	
	public static void saveConfig() throws IOException {
		String prop_file_name =
			System.getProperty("user.home")+"/"+".jvltrc";
		FileOutputStream fos = new FileOutputStream(prop_file_name);
		_config.store(fos, "jVLT property file");
	}
	
	public static Locale[] getSupportedLocales() { return _locales; }

	/**
	 * Returns a map that contains a set of properties.
	 * Unlike the properties stored in the Config object returned by method
	 * getConfig(), this map is not stored in a config file. */
	public static PropertyMap getRuntimeProperties() {
		return _runtime_properties; }
	
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
		// Read settings.
		//----------		
		_config = new Config();
		try	{
			String prop_file_name =
				System.getProperty("user.home")+"/"+".jvltrc";
			FileInputStream fis=new FileInputStream(prop_file_name);
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

