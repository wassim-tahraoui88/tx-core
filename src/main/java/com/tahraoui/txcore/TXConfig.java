package com.tahraoui.txcore;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;

public class TXConfig {

	private static final Logger LOGGER = LogManager.getLogger(TXConfig.class);
	private static final String LOGGER_PACKAGE = "com.tahraoui.txcore";

	private static TXConfig instance;
	public static TXConfig getInstance() {
		if (instance == null) instance = new TXConfig();
		return instance;
	}

	//region Properties String Caches
	private static final String CONFIG_PATH = "application.properties";
	private static final String KEY_APP_NAME = "app.name", KEY_APP_VERSION = "app.version", KEY_APP_AUTHOR = "app.author",
			KEY_DEBUG_ENABLED = "tx-core.debug.enabled", KEY_DEBUG_LEVEL = "tx-core.debug.level",
			// TODO: Add a property to set the debug file path
			KEY_DEBUG_FILE = "tx-core.debug.file";
	//endregion

	private final Preferences prefs;
	private final ClassLoader classLoader;
	private final Properties properties = new Properties();

	private TXConfig() {
		classLoader = Thread.currentThread().getContextClassLoader();
		loadProperties();
		prefs = Preferences.userRoot().node("com.tahraoui:%s".formatted(getAppProjectName()));
		initLogger();
	}
	private void initLogger() {
		var isDebugEnabled = loadBooleanProperty(KEY_DEBUG_ENABLED,true);
		var debugLevel = Level.getLevel(loadStringProperty(KEY_DEBUG_LEVEL,"DEBUG"));
		setLogger(LOGGER_PACKAGE, isDebugEnabled, debugLevel);
	}

	public ClassLoader getClassLoader() { return classLoader; }
	public Preferences getPrefs() { return prefs; }

	//region Aux Methods
	public void setLogger(String loggerPackage, boolean isDebugEnabled, Level debugLevel) {
		Configurator.setLevel(loggerPackage, isDebugEnabled ? debugLevel : Level.OFF);
	}
	private void loadProperties() {
		try (var input = classLoader.getResourceAsStream(CONFIG_PATH)) {
			if (input == null) throw new FileNotFoundException("Resource not found: %s, if you have one and you're using modules, open the config folder in module-info.java.".formatted(CONFIG_PATH));
			properties.load(input);
		}
		catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
		}
		catch (IOException e) {
			LOGGER.error("Failed to load configuration properties.", e);
		}
	}

	public String loadStringProperty(String propertyName) { return loadStringProperty(propertyName,null); }
	public String loadStringProperty(String propertyName, String defaultValue) {
		var property = properties.getProperty(propertyName);
		if (property == null) {
			if (defaultValue == null) rejectProperty(propertyName);
			return defaultValue;
		}
		return property;
	}

	public int loadIntProperty(String propertyName) { return loadIntProperty(propertyName,null); }
	public int loadIntProperty(String propertyName, Integer defaultValue) {
		var property = properties.getProperty(propertyName);
		if (property == null) {
			if (defaultValue == null) rejectProperty(propertyName);
			return defaultValue;
		}
		try {
			return Integer.parseInt(property);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to parse `%s`".formatted(propertyName), e);
		}
	}

	public float loadFloatProperty(String propertyName) { return loadFloatProperty(propertyName,null); }
	public float loadFloatProperty(String propertyName, Float defaultValue) {
		var property = properties.getProperty(propertyName);
		if (property == null) {
			if (defaultValue == null) rejectProperty(propertyName);
			return defaultValue;
		}
		try {
			return Float.parseFloat(property);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to parse `%s`".formatted(propertyName), e);
		}
	}

	public boolean loadBooleanProperty(String propertyName) { return loadBooleanProperty(propertyName,null); }
	public boolean loadBooleanProperty(String propertyName, Boolean defaultValue) {
		var property = properties.getProperty(propertyName);
		if (property == null) {
			if (defaultValue == null) rejectProperty(propertyName);
			return defaultValue;
		}
		return Boolean.parseBoolean(property);
	}

	private void rejectProperty(String propertyName) {
		throw new RuntimeException("You need a `%s` property in your %s to get this value.".formatted(propertyName, CONFIG_PATH));
	}
	//endregion

	//region App Properties
	/**
	 * Returns the value of the `app.name` property from the application properties file.
	 * If the property is not found, it throws a RuntimeException.
	 * This property is used to identify the application name.
	 *
	 * @return The application name.
	 */
	public String getAppName() { return loadStringProperty(KEY_APP_NAME); }
	/**
	 * Returns a lowercase hyphenated format of the application name (`app-project-name`).
	 *
	 * @return a lowercase hyphenated format of the application name.
	 */
	public String getAppProjectName() { return getAppName().toLowerCase().replace(" ", "-"); }
	/**
	 * Returns the value of the `app.version` property from the application properties file.
	 * If the property is not found, it throws a RuntimeException.
	 * This property is used to identify the application version.
	 *
	 * @return The application version.
	 */
	public String getAppVersion() { return loadStringProperty(KEY_APP_VERSION); }
	/**
	 * Returns the value of the `app.author` property from the application properties file.
	 * If the property is not found, it throws a RuntimeException.
	 * This property is used to identify the application author.
	 * If you don't have an author, you can set it to "Unknown".
	 *
	 * @return The application author.
	 */
	public String getAppAuthor() { return loadStringProperty(KEY_APP_AUTHOR); }
	//endregion

}

