package surePark;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class ServerUtils {
	private static final Logger LOG = Logger.getLogger(ServerUtils.class);

	private static ServerUtils INSTANCE = null;

	private ServerUtils() {

	}

	/**
	 * ServerUtils getInstance.
	 * 
	 * @return
	 */
	public static ServerUtils getInstance() {
		if (INSTANCE == null) {
			synchronized (ServerUtils.class) {
				if (INSTANCE == null) {
					INSTANCE = new ServerUtils();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * Get IP address of the server on which the current servlet is running.
	 * 
	 * @return
	 */
	public String getIpAddress() {
		InetAddress ip = null;

		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			LOG.error("Error: UnknownHostException.");
		}

		return ip.getHostAddress();
	}

	/**
	 * Get hostname of the server on which the current servlet is running.
	 * 
	 * @return
	 */
	public String getHostName() {
		InetAddress ip = null;

		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			LOG.error("Error: UnknownHostException.");
		}

		return ip.getHostName();
	}
	
	public static void main(String[] args){
		ServerUtils obj = new ServerUtils();
		System.out.println(obj.getIpAddress());
		System.out.println(obj.getHostName());
	}

}
