package surePark;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class sureParkServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(sureParkServlet.class);

	private static final long serialVersionUID = -7404786595814093489L;

	public void init() {
		try {
			super.init();
			/*
			 * Start all Threads here
			 */
		}

		catch (Exception e) {
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

}
