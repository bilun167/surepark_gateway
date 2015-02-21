package surePark;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class DataListener implements Runnable {

	private static final Logger LOG = Logger.getLogger(DataListener.class);

	InputStream in;
	static int i = 1;
	String newLine = System.getProperty("line.separator");
	private final static int len_index = 0;
	private final static int api_index = 1;
	private final static int session_index = 2;
	private final static int cmd_index = 3;
	private final static int status_index = 4;
	private final static int role_index = 5;
	private final static int firmware_index = 7;
	private final static int unicast_index = 9;
	private final static int netid_index = 11;
	private final static int mac_index = 13;
	private final static byte isTopo = (byte) 0x54;
	private final static byte isReboot = (byte) 0x30;
	private final static byte isCarData = (byte) 0xEC;
	private final static byte isLocal = (byte) 0x88;
	private final static byte isRemote = (byte) 0x97;
	private final String[] statusResult = { "OK", "Error", "Invalid Command",
			"Invalid Parameter" };
	private static String MAC_COORDINATOR;
	
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
						i++;
						if (firstSignal) {
							// first byte must be length
							firstSignal = false;
							if (buffer[len_index] == str.length() - 3) {
								
								// LOGGING
								// System.out.println("Message is complete, start processing");
								if (LOG.isInfoEnabled()) LOG.info("Message is complete, start processing");
								
								// start processing
								processRequest(buffer);
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
								processRequest(processor);
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
						i++;
					}
				}

			}
			// LOGGING
			//System.out.println("Stopped listening");
			if (LOG.isInfoEnabled()) LOG.info("Stopped Listening");
		} catch (IOException e) {
			LOG.error("Exception in Data Listener",e);
		}
	}

	public void processRequest(byte[] bArray) {
		int crc_pos = bArray[len_index]+2;
		int crc = bArray[crc_pos];
		int calc_crc = getCRC(bArray);
		if (crc != calc_crc) {
			// LOGGING
			// System.out.println("Incorrect CRC, rejecting the Response!!");
			LOG.error("Incorrect CRC, rejecting the Response!!");
			/*
			 * TODO check if command can be sent again
			 */

		} else {
			switch (bArray[cmd_index]) {
			case isTopo: {
				// Topology data
				// System.out.println("Topology data!");
				if (LOG.isInfoEnabled()) LOG.info("Topology data!");
				ProcessTopoData(bArray);
				break;
			}
			case isReboot: {
				// Reboot data
				// System.out.println("Reboot data!");
				if (LOG.isInfoEnabled()) LOG.info("Reboot data!");
				ProcessRebootData(bArray);
				break;
			}
			default: {
				if (bArray[2] == isCarData) {
					// car data
					// System.out.println("Car data!");
					if (LOG.isInfoEnabled()) LOG.info("Car data!");
					ProcessCarData(bArray);
				} else {
					// Corrupt data
					// System.out.println("Unknown data fromat!");
					if (LOG.isInfoEnabled()) LOG.info("Unknown data fromat!");

				}
				break;
			}
			}

		}
	}

	public String buildHex(byte[] ba, int index, int lim) {
		StringBuilder str = new StringBuilder();
		for (int i = index; i < index + lim; i++)
			str.append(String.format("%02x", ba[i]));
		return str.toString();
	}

	public String buildMac(byte[] ba, int index, int lim) {
		StringBuilder str = new StringBuilder();
		for (int i = index + 4; i < index + lim; i++)
			str.append(String.format("%02x", ba[i]));
		for (int i = index; i < index + 4; i++)
			str.append(String.format("%02x", ba[i]));
		return str.toString();
	}

	public byte getCRC(byte[] crcArr) {
		byte calc_crc = 0;
		int msg_len = crcArr[0]+2;
		System.out.println("message length is"+msg_len);
		for (int i = 2; i < crcArr[0]+2; i++) {
			calc_crc += crcArr[i];
		}
		return calc_crc;
	}

	public String toHexString(byte[] ba, int size) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < size; i++)
			str.append(" " + String.format("%02x", ba[i]));
		return str.toString();
	}

	public void ProcessTopoData(byte[] topoArray) {

		String sessionId = String.format("%02x", topoArray[session_index]);
		int statusId = topoArray[status_index];
		String statusStr = statusResult[statusId];
		if (statusId != 0) {
			// LOGGING
			// System.out.println("Command failed with error : " + statusStr);
			LOG.error("Command failed with error : " + statusStr);
		} else {
			int roleId = topoArray[role_index];
			byte apiId = topoArray[api_index];

			String macStr = buildMac(topoArray, mac_index, 8);
			String firmwareStr = buildHex(topoArray, firmware_index, 2);
			String unicastStr = buildHex(topoArray, unicast_index, 2);
			String netIdStr = buildHex(topoArray, netid_index, 2);
			
			// LOGGING
			//System.out.println("firmware " + firmwareStr);
			//System.out.println("unicast id " + unicastStr);
			//System.out.println("net id " + netIdStr);
			//System.out.println("mac id " + macStr);
			if (LOG.isInfoEnabled()) LOG.info("firmware " + firmwareStr);
			if (LOG.isInfoEnabled()) LOG.info("unicast id " + unicastStr);
			if (LOG.isInfoEnabled()) LOG.info("net id " + netIdStr);
			if (LOG.isInfoEnabled()) LOG.info("mac id " + macStr);
			
			if (apiId == isRemote && roleId == 2) {
				// data from end node
				// System.out.println("Data from End node");
				if (LOG.isInfoEnabled()) LOG.info("Data from End node");
				
				// Insert topology data into DB
				int db_counter = Integer.parseInt(PropertyUtils.getInstance().getRefreshProperty("count","db_count.properties"));
                if(sessionId=="FF")
                db_counter--;
                
                String reqId=String.valueOf(db_counter)+sessionId;
				TopologyMO topo = new TopologyMO(reqId, macStr, firmwareStr, unicastStr, netIdStr);
				try {
					if (LOG.isInfoEnabled()) LOG.info("Insert data into DB.");
					new DBUtils().insertTopoRecords(topo);
				} catch (SQLException e) {
					LOG.error("Exception in processing topology data" , e);
				}
			} else if (apiId == isRemote && roleId == 1) {
				// data from repeater
				// System.out.println("Data from Repeater");
				if (LOG.isInfoEnabled()) LOG.info("Data from Repeater");
			} else if (apiId == isLocal) {
				// data from coordinator
				// System.out.println("Data from Coordinator");
				if (LOG.isInfoEnabled()) LOG.info("Data from Coordinator");
				MAC_COORDINATOR=macStr;
			} else {
				// Unknown data format
				// System.out.println("Unknown data fromat!");
				if (LOG.isInfoEnabled()) LOG.info("Unknown data fromat!");
			}
		}

	}

	public void ProcessRebootData(byte[] topoArray) {

		String sessionId = String.format("%02x", topoArray[session_index]);
		int statusId = topoArray[status_index];
		String statusStr = statusResult[statusId];
		if (statusId != 0) {
			// LOGGING
			// System.out.println("Command failed with error : " + statusStr);
			LOG.error("Command failed with error : " + statusStr);
		} else {
			byte apiId = topoArray[api_index];
			String macStr = buildMac(topoArray, 5, 8);
			
			// LOGGING
			// System.out.println("mac id " + macStr);
			if (LOG.isInfoEnabled()) LOG.info("mac id " + macStr);
			if (apiId == isRemote) {
				// data from end node
				// System.out.println("Data from End node");
				if (LOG.isInfoEnabled()) LOG.info("Data from End node");
			} else if (apiId == isLocal) {
				// data from coordinator
				// System.out.println("Data from Coordinator");
				if (LOG.isInfoEnabled()) LOG.info("Data from Coordinator");
			} else {
				// System.out.println("Unknown data format!");
				if (LOG.isInfoEnabled()) LOG.info("Unknown data fromat!");
			}
		}
	}

	public void ProcessCarData(byte[] topoArray) {
		int statusId = topoArray[3];
		String statusStr = statusResult[statusId];
		if (statusId != 0) {
			// LOGGING
			// System.out.println("Command failed with error : " + statusStr);
			LOG.error("Command failed with error : " + statusStr);
		}
		else{
		// Extract info from Car data	
		String macStr = buildMac(topoArray, 9, 8);
		String radio_channel = String.format("%02x", topoArray[4]);
		int occupancy = topoArray[5];
		String dataTosend = "Pkdt,"+MAC_COORDINATOR+","+macStr +","+occupancy;
		// Send to kinesis		
		AWSUtil.getInstance().sendToKinesis(dataTosend);				
		}
	}
}