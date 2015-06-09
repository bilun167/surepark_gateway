package surePark;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class DataListener implements Runnable {

	private static final Logger LOG = Logger.getLogger(DataListener.class);

	InputStream in;
	static int i = 0;
	String newLine = System.getProperty("line.separator");
	final static int len_index = 0;
	final static int api_index = 1;
	final static int session_index = 2;
	final static int cmd_index = 3;
	final static int status_index = 4;
	final static int role_index = 5;
	final static int firmware_index = 7;
	final static int unicast_index = 9;
	final static int netid_index = 11;
	final static int mac_index = 13;
	final static byte isTopo = (byte) 0x54;
	final static byte isReboot = (byte) 0x30;
	final static byte isCarData = (byte) 0xEC;
	final static byte isRebootCarData = (byte) 0xCE;
	final static byte isLocal = (byte) 0x88;
	final static byte isRemote = (byte) 0x97;
	final static String[] statusResult = { "OK", "Error", "Invalid Command",
			"Invalid Parameter" };
	private static int threadPoolQueueSize = 20;
	private static int threadPoolCoreSize = 5;
	private static int maxThreadPoolSize = 10;
	private static int KeepAliveTime = 3600;
	private DataProcessorTask dataProcessorTask;

	private static BlockingQueue<Runnable> jobQueue = new ArrayBlockingQueue<Runnable>(
			threadPoolQueueSize);
	private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			threadPoolCoreSize, maxThreadPoolSize, KeepAliveTime,
			TimeUnit.SECONDS, jobQueue);

	public DataListener(InputStream in) {
		this.in = in;
	}

	public void run() {
		byte[] buffer = new byte[1024];
		byte[] construct_buffer = new byte[1024];
		byte[] temp_buffer = new byte[1024];

		int len = -1;
		byte[] processor = null;
		int total = 0;
		boolean seven_e = false;
		try {
			// LOGGING
			// System.out.println("Started listening");
			if (LOG.isInfoEnabled())
				LOG.info("Started listening");
			while ((len = this.in.read(buffer)) > -1) {
				try {

					String str = new String(buffer, 0, len);
					// Ignore blank Messages
					if (str.length() == 0 && str.equals("")) {
						continue;
					}
					// if first byte is 7e, start constructing buffer
					if (buffer[0] == 126) {
						seven_e = true;
					}
					
                    i++;
					if (seven_e) {
						if (LOG.isInfoEnabled()) LOG.info("Received a signal: [#"+ i + "] of length -" + str.length()+"--------------------------");
						if (LOG.isInfoEnabled()) LOG.info("Message :"+ toHexString(buffer, str.length()));
						
						// Copy the contents of buffer array into construct buffer
						System.arraycopy(buffer, 0, construct_buffer, total, str.length());						
						total += str.length();

						while (true) {
							String const_str = new String(construct_buffer, 0, total);
							int msg_length = construct_buffer[1];
							if (LOG.isInfoEnabled()) LOG.info("Accumulated Message Buffer is:"+ toHexString(construct_buffer, const_str.length()));
							
							// First byte 7e and no other bytes, then wait
							if (construct_buffer[0] == 126 && msg_length == 0) {
								if (LOG.isInfoEnabled()) LOG.info("case1");
								break;
							}
							
							// First byte 7e and message still incomplete, then wait
							if (construct_buffer[0] == 126 && msg_length != 0 && msg_length > (total - 4)) {
								if (LOG.isInfoEnabled()) LOG.info("case2");
								break;
							}
							
							// first byte 7e and message is complete or has multiple messages in buffer, then process
							if (construct_buffer[0] == 126 && msg_length != 0 && msg_length <= (total - 4)) {
								
								if (LOG.isInfoEnabled())
								LOG.info("Message is complete, start processing");
								
								// start processing
								processor = new byte[msg_length + 3];
								System.arraycopy(construct_buffer, 1,processor, 0, msg_length + 3);
								
								// submit task
								dataProcessorTask = new DataProcessorTask(processor);
								threadPoolExecutor.submit(dataProcessorTask);
								
								// reinitialize construct buffer
								if(msg_length == (total - 4))
								{
									construct_buffer = new byte[1024];
									total=0;
									break;
								}
								else
								{
								temp_buffer = Arrays.copyOf(construct_buffer,total);
								construct_buffer = new byte[1024];
								
								System.arraycopy(temp_buffer, msg_length + 4, construct_buffer, 0, total- (msg_length + 4));
								total = total - (msg_length + 4);
								continue;
								}
								
							}
							else{
								LOG.info("Case 3");
								construct_buffer = new byte[1024];
								total=0;
								break;
							}
						}
					} 
					else {
						if (LOG.isInfoEnabled()) LOG.info("Received a signal: [#"+ i + "] of length -" + str.length());
						if (LOG.isInfoEnabled()) LOG.info("Message :"+ toHexString(buffer, str.length()));
					}

				} catch (Exception e) {
					if (LOG.isInfoEnabled())
						LOG.info("Exception occured" + e);
				}

			}
			// LOGGING
			// System.out.println("Stopped listening");
			if (LOG.isInfoEnabled())
				LOG.info("Stopped Listening");
		} catch (IOException e) {
			LOG.error("Exception in Data Listener", e);
		}
	}

	public String toHexString(byte[] ba, int size) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < size; i++)
			str.append(" " + String.format("%02x", ba[i]));
		return str.toString();
	}

}