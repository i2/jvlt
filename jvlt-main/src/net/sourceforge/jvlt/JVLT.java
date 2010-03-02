package net.sourceforge.jvlt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sourceforge.jvlt.model.JVLTModel;
import net.sourceforge.jvlt.utils.Config;
import net.sourceforge.jvlt.utils.PropertyMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class JVLT {
	private static final Logger logger = Logger.getLogger(JVLT.class);

	/** where to find/store configuration information */
	private final String configDir;

	/** from old versions, used to move old data */
	private static final String OLD_CONFIG_DIR = System
			.getProperty("user.home")
			+ File.separator + ".jvlt";

	private static final Locale[] _locales = { Locale.US, Locale.FRANCE,
			Locale.GERMANY, new Locale("cs", "CZ"), new Locale("pl", "PL") };

	private JVLTModel _model;
	private PropertyMap _runtime_properties;
	private Config _config = null;

	private String _version = null;
	private String _data_version = null;

	private static JVLT _instance;
	static {
		_instance = new JVLT();
		_instance.init();
	}
	
	public static JVLT getInstance() {
		return _instance;
	}

	public static Config getConfig() {
		return _instance._config;
	}

	public void saveConfig() throws IOException {
		String prop_file_name = configDir + File.separator + "config";
		FileOutputStream fos = new FileOutputStream(prop_file_name);
		_instance._config.store(fos);
	}

	public static Locale[] getSupportedLocales() {
		return _locales;
	}

	/**
	 * Returns a map that contains a set of properties. Unlike the properties
	 * stored in the Config object returned by method getConfig(), this map is
	 * not stored in a config file.
	 */
	public static PropertyMap getRuntimeProperties() {
		return _instance._runtime_properties;
	}

	public static String getVersion() {
		return _instance._version;
	}

	public static String getDataVersion() {
		return _instance._data_version;
	}

	public JVLT() {
		_model = null;
		_config = null;
		_runtime_properties = new PropertyMap();
		configDir = getConfigPath();
	}

	public String getConfigDir() {
		return configDir;
	}

	public JVLTModel getModel() {
		return _model;
	}

	/**
	 * Returns the folder where to store JVLT config information, creating it if
	 * necessary. If no configuration exists but there is a configuration
	 * defined for an older version, the old configuration will be moved to the
	 * new folder.
	 * 
	 * @param config where to read/store the configuration information
	 * @param oldConfig configuration directory from previous versions
	 * @return the folder to use for reading/storing config information
	 */
	static File getOrBuildConfigDirectory(File config, File oldConfig) {
		// TODO what do we do if we can't write the config for whatever reason?
		if (config.exists()) {
			if (!config.isDirectory()) {
				logger.info(config.getPath()
						+ " already exists but it is not a directory");
			}
			return config;
		}

		if (oldConfig.exists() && oldConfig.renameTo(config)) {
			logger.info(oldConfig.getPath() + " migrated to "
					+ config.getPath());
			return config;
		}

		if (!config.mkdirs()) {
			logger.info(config.getPath() + " could not be created.");
		}
		return config;
	}

	/**
	 * Determines the folder where to read and store config information. This
	 * method should only be called during initialization, refer to the
	 * {@link #configDir} field afterwards.
	 * 
	 * @return the folder where to read and store config information
	 */
	static String getConfigPath() {
		String pathSuffix = "jvlt";

		String configOverride = System.getProperty("config");
		if (configOverride != null && new File(configOverride).canWrite()) {
			// a writable config folder was specified
			String configPath = configOverride + File.separator + pathSuffix;
			logger.info("Using config folder: " + configPath);
			return configPath;
		}

		// set path based on OS
		String os = System.getProperty("os.name");
		if (os.toLowerCase(Locale.getDefault()).startsWith("windows")) {
			return System.getenv("APPDATA") + File.separator + pathSuffix;
		}

		if (os.toLowerCase(Locale.getDefault()).startsWith("mac os x")) {
			return System.getProperty("user.home") + File.separator + "Library"
					+ File.separator + "Application Support" + File.separator
					+ pathSuffix;
		}
		String dir = System.getenv("XDG_CONFIG_HOME");
		if (dir == null || dir.equals("")) {
			dir = System.getProperty("user.home") + File.separator + ".config";
		}

		return dir + File.separator + pathSuffix;
	}
	
	private void init() {
		// ----------
		// Read version info
		// ----------
		InputStream xml_stream = JVLT.class
				.getResourceAsStream("/xml/info.xml");
		InputSource src = new InputSource(xml_stream);
		XPathFactory fac = XPathFactory.newInstance();
		XPath path = fac.newXPath();
		try {
			Node root = (Node) path.evaluate("/info", src, XPathConstants.NODE);
			_version = path.evaluate("version", root);
			_data_version = path.evaluate("data-version", root);
		} catch (XPathExpressionException ex) {
			logger.error(ex);
		}

		File dir = getOrBuildConfigDirectory(new File(configDir), new File(
				OLD_CONFIG_DIR));

		// ----------
		// Read settings.
		// ----------
		_config = new Config();
		try {
			String prop_file_name = dir.getPath() + File.separator + "config";
			FileInputStream fis = new FileInputStream(prop_file_name);
			_config.load(fis);
			fis.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		_model = new JVLTModel();

		// ----------
		// Set locale.
		// ----------
		Locale loc = Locale.getDefault();
		Locale.setDefault(_config.getLocaleProperty("locale", loc));
	}
}
