package z.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceFetcher {

	public static String getUrl() throws IOException {

		Properties applicationProperties = new Properties();

		// Loads the properties file from the class-path:
		// The complete path from the root must be used, separated by /
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream propertiesInputStream = classLoader
				.getResourceAsStream("resources/application_classpath.properties");

		applicationProperties.load(propertiesInputStream);

		String url = applicationProperties.getProperty("db.url");
		return url;
	}

	public static String getAdminName() throws IOException {

		Properties applicationProperties = new Properties();

		// Loads the properties file from the class-path:
		// The complete path from the root must be used, separated by /
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream propertiesInputStream = classLoader
				.getResourceAsStream("resources/application_classpath.properties");

		applicationProperties.load(propertiesInputStream);

		String url = applicationProperties.getProperty("admin.name");
		return url;
	}

	public static String getaAdminPassword() throws IOException {

		Properties applicationProperties = new Properties();

		// Loads the properties file from the class-path:
		// The complete path from the root must be used, separated by /
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream propertiesInputStream = classLoader
				.getResourceAsStream("resources/application_classpath.properties");

		applicationProperties.load(propertiesInputStream);

		String url = applicationProperties.getProperty("admin.password");
		return url;
	}

}
