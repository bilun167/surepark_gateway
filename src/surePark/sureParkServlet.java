package surePark;

import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import surePark.TalkWithCoordinator.SerialReader;

public class sureParkServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(sureParkServlet.class);

	private static final long serialVersionUID = -7404786595814093489L;

	private static final String TOMCAT_PROP_FILE = "tomcat.properties";

	private static final String CONFIG_PROP_FILE = "config.properties";

	public static InputStream INPUT_STREAM;

	public static OutputStream OUTPUT_STREAM;

	public void init() {
		try {
			super.init();

			// 1. Increment the value of tomcat-start up counter
			String newLine = System.getProperty("line.separator");
			int counter = Integer.parseInt(PropertyUtils.getProperty("count",TOMCAT_PROP_FILE));
	
			// Logging
			System.out.println("Tomcat prev start up count = " + counter + newLine + "Incrementing the value by 1");
			if (LOG.isInfoEnabled())LOG.info("Tomcat prev start up count = " + counter + newLine + "Incrementing the value by 1");
			
			PropertyUtils.setProperty("count", Integer.toString(counter + 1),"tomcat.properties");

			// 2. Connect to the Device
			String portId = PropertyUtils.getProperty("port", CONFIG_PROP_FILE);
			
			//Logging
			System.out.println("Attempting to connect to device: " + portId);
			if (LOG.isInfoEnabled()) LOG.info("Attempting to connect to device: " + portId);

			SerialPort serialPort = new ConnectToCoordinator().connect(portId);
			if (serialPort != null) {
				INPUT_STREAM = serialPort.getInputStream();
				OUTPUT_STREAM = serialPort.getOutputStream();
			} else {
				throw new Exception("Serial port is null!");
			}
			
			// 3. Start Listening
			new Thread(new DataListener(INPUT_STREAM)).start();
			Thread.currentThread().sleep(3000);
			new SendCommand(OUTPUT_STREAM).goToAPIMode();
						
			//Thread.currentThread().sleep(3000);
			//new SendCommand(OUTPUT_STREAM, (byte)0x01).SendTopologyCommand();
			
			Thread.currentThread().sleep(3000);
			new SendCommand(OUTPUT_STREAM, (byte)0x02).sendRebootAllCommand();
			

		} catch (NoSuchPortException e) {
			System.out.println("Coordinator is not connected or port number is wrong in config.properties. "+ e);
			LOG.error("Coordinator is not connected or port number is wrong in config.properties. "+ e);
		} catch (Exception e) {
			System.out.println(e);
			LOG.fatal(e.getMessage());
		}
	}

	public void destroy() {
		try {
			/*
			 * kill threads, session etc
			 */

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	public sureParkServlet() {
		/*
		 * Initialize all helper classes, runnable etc
		 */
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().println("Sure Park is active!");
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		StringBuffer requestXml = null;
		String reqName = null;
		try {
			// 1. read Request XML
			requestXml = XMLUtils.readInputStream(request.getInputStream());
			if (LOG.isInfoEnabled()) {
				LOG.info("Request XML is" + requestXml);
			}
			// 2. get request-name
			reqName = XMLUtils.getRequestName(requestXml.toString());
			if (LOG.isInfoEnabled()) {
				LOG.info("Request Name is" + reqName);
			}
			// 3. Process the request

		} catch (Exception e) {

		} finally {
			// Send the response
			response.setContentType("text/x-xml; charset=UTF-8");
			String responseXml = requestXml.toString();
			response.getWriter().print(responseXml);
		}

	}

	public static void main(String args[]) {
		//new sureParkServlet().init();
		String s="ab10";
		try {
			byte arr[]=Hex.decodeHex(s.toCharArray());
			System.out.println(arr[0]);
			System.out.println(arr[1]);
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
