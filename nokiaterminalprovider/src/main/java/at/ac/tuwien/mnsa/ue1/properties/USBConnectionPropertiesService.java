/**
 * 
 */
package at.ac.tuwien.mnsa.ue1.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author klaus
 * 
 */
class USBConnectionPropertiesService implements PropertiesService {
	public static final String PROPERTIES_FILE = "USBConnection.properties";

	public static final String PORT_KEY = "com.port";

	private Properties prop;

	// Private constructor prevents instantiation from other classes
	private USBConnectionPropertiesService() {
		prop = null;
	}

	private static class PropertiesServiceHolder {
		public static final PropertiesService INSTANCE = new USBConnectionPropertiesService();
	}

	public static PropertiesService getInstance() {
		return PropertiesServiceHolder.INSTANCE;
	}

	@Override
	public Properties getProperties() throws IOException {
		if (prop == null) {
			prop = loadProperties(PROPERTIES_FILE);
		}

		// Check required properties
		checkKey(prop, PORT_KEY);

		return prop;
	}

	private Properties loadProperties(String fileName) throws IOException {
		InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
		if (is == null)
			throw new IOException(fileName + " not found!");

		Properties prop = new Properties();
		try {
			try {
				prop.load(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new IOException("Couldn't load " + fileName + "!", e);
		}

		return prop;
	}

	private void checkKey(Properties prop, String key) throws IOException {
		if (prop == null)
			throw new IllegalArgumentException("properties is null");
		if (key == null)
			throw new IllegalArgumentException("key is null");

		if (!prop.containsKey(key)) {
			throw new IOException("Properties do not contain the required key "
					+ key + ".");
		}
	}

}
