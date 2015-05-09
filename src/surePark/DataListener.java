package surePark;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class DataListener implements Runnable {

	private static final Logger LOG = Logger.getLogger(DataListener.class);

	 InputStream in;
	 static int i = 1;
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
	 private static int threadPoolQueueSize=20;
	 private static int threadPoolCoreSize=5;
	 private static int maxThreadPoolSize=10;
	 private static int KeepAliveTime=3600;
     private DataProcessorTask dataProcessorTask;
	
	 private static BlockingQueue<Runnable> jobQueue = new ArrayBlockingQueue<Runnable> (threadPoolQueueSize);
	 private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor (threadPoolCoreSize, maxThreadPoolSize, KeepAliveTime , TimeUnit.SECONDS, jobQueue);

	 
	public DataListener(InputStream in) {
		this.in = in;
	}

	public void run() {
		byte[] buffer = new byte[1024];
		int len = -1;
		boolean firstSignal = false;
		byte[] processor = null;
		int total = 0;
		int length_msg = 0;
		boolean seven_e = false;
		try {
			// LOGGING
			// System.out.println("Started listening");
			if (LOG.isInfoEnabled()) LOG.info("Started listening");
			while ((len = this.in.read(buffer)) > -1) {
				try{
					i++;	
				if (LOG.isInfoEnabled()) LOG.info("Got a signal: [#"+ i + "] "+buffer);	
				if (buffer[0] == 126) {
					// Start of signal , listen to next signal
					firstSignal = true;
					seven_e = true;
				} else {
					if (seven_e == true) {
						String str = new String(buffer, 0, len);
						
						// LOGGING
						// System.out.println(newLine + "Received a signal: [#"+ i + "] of length -" + str.length());
						// System.out.println("Message :"+ toHexString(buffer, str.length()));
						if (LOG.isInfoEnabled()) LOG.info("Received a signal: [#"+ i + "] of length -" + str.length());
						if (LOG.isInfoEnabled()) LOG.info("Message :"+ toHexString(buffer, str.length()));
						if (firstSignal) {
							// first byte must be length
							firstSignal = false;
							if (buffer[len_index] == str.length() - 3) {
								
								// LOGGING
								// System.out.println("Message is complete, start processing");
								if (LOG.isInfoEnabled()) LOG.info("Message is complete, start processing");
								
								// start processing
								dataProcessorTask=new DataProcessorTask(buffer);
								threadPoolExecutor.submit(dataProcessorTask);
								seven_e = false;
							} else {
								// first signal is not complete
								processor = new byte[28];
								total = 0;
								length_msg = buffer[len_index] + 3;
								System.arraycopy(buffer, 0, processor, 0,
										str.length());
								total += str.length();
							}
						} else {
							// getting remaining part of message							
							System.arraycopy(buffer, 0, processor, total,str.length());
							total += str.length();
							if (total == length_msg) {
								
								// LOGGING
								// System.out.println("Message construction complete, start processing");
								// System.out.println(toHexString(processor, 28));
								if (LOG.isInfoEnabled()) LOG.info("Message construction complete, start processing");
								if (LOG.isInfoEnabled()) LOG.info(toHexString(processor, 28));
								// start processing
								dataProcessorTask=new DataProcessorTask(processor);
								threadPoolExecutor.submit(dataProcessorTask);
								seven_e = false;
							}
						}

					} else {
						String str = new String(buffer, 0, len);
						// LOGGING
						// System.out.println(newLine + "Received a signal: [#"+ i + "] of length -" + str.length());
						// System.out.println("Message :" + str);
						if (LOG.isInfoEnabled()) LOG.info("Received a signal: [#"+ i + "] of length -" + str.length());
						if (LOG.isInfoEnabled()) LOG.info("Message :" + str);
					}
				}

			}
			catch(Exception e){
				if (LOG.isInfoEnabled()) LOG.info("Exception occured"+e);
			}

			}
			// LOGGING
			//System.out.println("Stopped listening");
			if (LOG.isInfoEnabled()) LOG.info("Stopped Listening");
		} catch (IOException e) {
			LOG.error("Exception in Data Listener",e);
		}
	}

	public String toHexString(byte[] ba, int size) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < size; i++)
			str.append(" " + String.format("%02x", ba[i]));
		return str.toString();
	}
	
}