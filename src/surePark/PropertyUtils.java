package surePark;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

public class PropertyUtils {
	
	
	private static PropertyUtils PROPERTY_UTILS;
	private PropertyUtils(){
		
	}
	public static PropertyUtils getInstance(){
		if(PROPERTY_UTILS==null){
			PROPERTY_UTILS = new PropertyUtils();
		}
		return PROPERTY_UTILS;
	}

	public static String getProperty(String property,String propertyFile) {
		Properties prop = new Properties();
		InputStream input = null;
		String propertyValue = null;
		try {
			prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile));
			//input = new FileInputStream(propertyFile);
			// load a properties file
			// prop.load(input);
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
	
	
	public String getRefreshProperty(String property,String propertyFile) {
		Properties prop = new Properties();
		InputStream input = null;
		String propertyValue = null;
		try {
			//prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile));
			String file=Thread.currentThread().getContextClassLoader().getResource(propertyFile).getFile();
            input = new FileInputStream(file);
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
			//prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile));
			String file=Thread.currentThread().getContextClassLoader().getResource(propertyFile).getFile();
			output = new FileOutputStream(file);
			
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
