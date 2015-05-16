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

import org.apache.log4j.Logger;

public class sureParkServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(sureParkServlet.class);

	private static final long serialVersionUID = -7404786595814093489L;

	private static final String TOMCAT_PROP_FILE = "tomcat.properties";

	private static final String CONFIG_PROP_FILE = "config.properties";

	public static InputStream INPUT_STREAM;

	public static OutputStream OUTPUT_STREAM;
	
	public static SerialPort SERIAL_PORT;

	public void init() {
		try {
			super.init();
			
			// Sleep for 5 seconds to allow tomcat to start up
			Thread.currentThread().sleep(5000);
			
			// 1. Increment the value of tomcat-start up counter
			String newLine = System.getProperty("line.separator");
			int counter = Integer.parseInt(PropertyUtils.getProperty("count",TOMCAT_PROP_FILE));
	
			// Logging
			// System.out.println("Tomcat prev start up count = " + counter + newLine + "Incrementing the value by 1");
			if (LOG.isInfoEnabled())LOG.info("Tomcat prev start up count = " + counter + newLine + "Incrementing the value by 1");
			
			PropertyUtils.setProperty("count", Integer.toString(counter + 1),"tomcat.properties");

			// 2. Connect to the Device
			String portId = PropertyUtils.getProperty("port", CONFIG_PROP_FILE);
			
			// Logging
			// System.out.println("Attempting to connect to device: " + portId);
			if (LOG.isInfoEnabled()) LOG.info("Attempting to connect to device: " + portId);

			SERIAL_PORT = new ConnectToCoordinator().connect(portId);
			if (SERIAL_PORT != null) {
				INPUT_STREAM = SERIAL_PORT.getInputStream();
				OUTPUT_STREAM = SERIAL_PORT.getOutputStream();
			} else {
				throw new Exception("Serial port is null!");
			}
			
			// 3. Start Listening
			new Thread(new DataListener(INPUT_STREAM)).start();
			
			// 4. sleep for 3 seconds and then send ATAP commnand
			Thread.currentThread().sleep(3000);
			new SendCommand(OUTPUT_STREAM).goToAPIMode();
			
			
			// 4. sleep for 3 seconds and then send Topology coordinator command
			Thread.currentThread().sleep(3000);
			String res= new SendCommand(OUTPUT_STREAM, (byte) 0x01).SendTopologyCoordinatorCommand();
			if(res.compareTo("SUCCEESS")==0){
				// Logging	
				// System.out.println("Got coordinator MAC successfully");
				if (LOG.isInfoEnabled()) LOG.info("Got coordinator MAC successfully");
			}

			// 5. If tomcat is started for first time, then send Reboot all command
			if(counter==1){				
			Thread.currentThread().sleep(3000);
			new SendCommand(OUTPUT_STREAM, (byte)0x00).sendRebootAllCommand();
			}
			
			// 6. Start Kinesis client
			AWSUtil awsutil = AWSUtil.getInstance();
			if(awsutil!=null){
				// Logging	
				// System.out.println("Kinesis client started successfully");
				if (LOG.isInfoEnabled()) LOG.info("Kinesis client started successfully");
			}
			
			// 7. Server IP and send it to Kinesis and schedule this task for every 24 hours.
			ServerConnectionScheduler.getInstance(awsutil).reconnectionScheduler(24*60*60);
			
			// Logging	
			// System.out.println("Sure Park servlet started successfully, ready to roll!!");
			if (LOG.isInfoEnabled()) LOG.info("Sure Park servlet started successfully, ready to roll!!");

			

		} catch (NoSuchPortException e) {
			// Logging
			// System.out.println("Coordinator is not connected or port number is wrong in config.properties. "+ e);
			LOG.error("Coordinator is not connected or port number is wrong in config.properties. "+ e);
		} catch (Exception e) {
			LOG.fatal("Sure Park servlet failed to start successfully"+e.getMessage(),e);
		}
	}

	public void destroy() {
		try {
			// Logging
			// System.out.print("Stopping SurePark servlet");
			if (LOG.isInfoEnabled()) LOG.info("Stopping SurePark servlet");
			SERIAL_PORT.close();
			INPUT_STREAM.close();
			OUTPUT_STREAM.close();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	public sureParkServlet() {
	
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().println("<h1>Sure Park is active!</h1>");
		String paramName = "do";
		String paramValue = request.getParameter(paramName);
        if(paramValue.equalsIgnoreCase("rebootall")){
			new SendCommand(OUTPUT_STREAM, (byte)0x00).sendRebootAllCommand();
        }
        if(paramValue.equalsIgnoreCase("topoall")){
        	new SendCommand(OUTPUT_STREAM, (byte) 0x00).SendTopologyCommand();
        }       
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		StringBuffer requestXml = null;
		String reqName = null;
		String uniCastId = null;
		ReturnStatus returnStatus = null;
		String resultStatus = null;
		String reqId = null;
		String responseXml = null;
		try {
			// 1. read Request XML
			requestXml = XMLUtils.readInputStream(request.getInputStream());
			if (LOG.isInfoEnabled()) {
				LOG.info("Request XML is: " + requestXml);
			}
			// 2. get request-name
			reqName = XMLUtils.getRequestName(requestXml.toString());
			if (LOG.isInfoEnabled()) {
				LOG.info("Request Name is: " + reqName);
			}
			// 3. get unicast-id
			uniCastId = XMLUtils.getUnicastID(requestXml.toString());
			if (LOG.isInfoEnabled()) {
				LOG.info("Unicast Id is: " + uniCastId);
			}
			
			// 4. Process the request
			if(reqName.compareTo("GetResultStatus")==0){
				reqId = XMLUtils.getRequestID(requestXml.toString());
				if (LOG.isInfoEnabled()) {
					LOG.info("Request id is"+reqId);
				}
				resultStatus= RequestHandler.getInstance().getRequestStatus(reqId);
			}
			else{
	            returnStatus = RequestHandler.getInstance().handleRequest(reqName, OUTPUT_STREAM, uniCastId);
			}
			
		} catch (Exception e) {
			LOG.error("Error in parsing the XML. Please verify the request!"+e.getMessage(),e);

		} finally {
			// Send the response
			response.setContentType("text/x-xml; charset=UTF-8");
			if(reqName.compareTo("GetResultStatus")==0){
				responseXml = XMLUtils.constructResponse(resultStatus);
			}
			else{
				 responseXml = XMLUtils.constructResponse(reqName, returnStatus);
			}
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing Response: "+responseXml);
			}
			response.getWriter().print(responseXml);
		}

	}

}
