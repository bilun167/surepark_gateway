package surePark;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyUtils {

	public static String getProperty(String property,String propertyFile) {
		Properties prop = new Properties();
		InputStream input = null;
		String propertyValue = null;
		try {
			input = new FileInputStream(propertyFile);
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			propertyValue = prop.getProperty(property);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return propertyValue;
	}

	public static boolean setProperty(String property, String value, String propertyFile) {
		Properties prop = new Properties();
		OutputStream output = null;
		boolean status = false;
		try {

			output = new FileOutputStream(propertyFile);

			// set the properties value
			prop.setProperty(property,value);

			// save properties to project root folder
			prop.store(output, null);
			status = true;
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return status;
	}

}
