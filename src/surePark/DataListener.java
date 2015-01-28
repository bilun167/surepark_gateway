package surePark;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class DataListener implements Runnable {

	private static final Logger LOG = Logger.getLogger(DataListener.class);

	InputStream in;
	static int i = 0;
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
	private final static int crc_index = 27;
	private final static byte isTopo = (byte) 0x54;
	private final static byte isReboot = (byte) 0x30;
	private final static byte isCarData = (byte) 0xEC;
	private final static byte isLocal = (byte) 0x88;
	private final static byte isRemote = (byte) 0x97;
	private final String[] statusResult = { "OK", "Error", "Invalid Command",
			"Invalid Parameter" };

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
			System.out.println("Started listening");
			while ((len = this.in.read(buffer)) > -1) {
				if (buffer[0] == 126) {
					// Start of signal , listen to next signal
					firstSignal = true;
					seven_e = true;
				} else {
					if (seven_e == true) {
						String str = new String(buffer, 0, len);
						System.out.println(newLine + "Received a signal: [#"
								+ ++i + "] of length -" + str.length());
						System.out.println("Message :"
								+ toHexString(buffer, str.length()));

						if (firstSignal) {
							// first byte must be length
							firstSignal = false;
							if (buffer[len_index] == str.length() - 3) {
								System.out
										.println("Message is complete, start processing");
								// start processing
								processRequest(buffer);
								seven_e = false;
							} else {
								// first signal is not complete
								System.out.println("Message is incomplete");
								processor = new byte[28];
								total = 0;
								length_msg = buffer[len_index] + 3;
								System.arraycopy(buffer, 0, processor, 0,
										str.length());
								total += str.length();
							}
						} else {
							// getting remaining part of message
							System.out.println("Message is incomplete");
							System.arraycopy(buffer, 0, processor, total,
									str.length());
							total += str.length();
							if (total == length_msg) {
								System.out
										.println("Message is complete now, start processing");
								System.out.println(toHexString(processor, 28));
								// start processing
								processRequest(processor);
								seven_e = false;
							}
						}

					} else {
						String str = new String(buffer, 0, len);
						System.out.println(newLine + "Received a signal: [#"
								+ ++i + "] of length -" + str.length());
						System.out.println("Message :" + str);
					}
				}

			}
			System.out.println("Stopped listening");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processRequest(byte[] bArray) {
		int crc = bArray[crc_index];
		int calc_crc = getCRC(bArray);
		if (crc != calc_crc) {
			System.out.println("Incorrect CRC, rejecting the Response!!");
			LOG.error("Incorrect CRC, rejecting the Response!!");
			/*
			 * TODO check if command can be sent again
			 */

		} else {
			switch (bArray[cmd_index]) {
			case isTopo: {
				// Topology data
				System.out.println("Topology data!");
				ProcessTopoData(bArray);
				break;
			}
			case isReboot: {
				// Reboot data
				System.out.println("Reboot data!");
				ProcessRebootData(bArray);
				break;
			}
			default: {
				if (bArray[2] == isCarData) {
					// car data
					System.out.println("Car data!");
					ProcessCarData(bArray);
				} else {
					System.out.println("Unknown data fromat!");

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
		for (int i = 2; i < 27; i++) {
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
			System.out.println("Command failed with error : " + statusStr);
			LOG.error("Command failed with error : " + statusStr);
		} else {
			int roleId = topoArray[role_index];
			byte apiId = topoArray[api_index];

			String macStr = buildMac(topoArray, mac_index, 8);
			String firmwareStr = buildHex(topoArray, firmware_index, 2);
			String unicastStr = buildHex(topoArray, unicast_index, 2);
			String netIdStr = buildHex(topoArray, netid_index, 2);
			System.out.println("firmware " + firmwareStr);
			System.out.println("unicast id " + unicastStr);
			System.out.println("net id " + netIdStr);
			System.out.println("mac id " + macStr);
			if (apiId == isRemote && roleId == 2) {
				// data from end node
				System.out.println("Data from End node");
			} else if (apiId == isRemote && roleId == 1) {
				// data from repeater
				System.out.println("Data from Repeater");
			} else if (apiId == isLocal) {
				// data from coordinator
				System.out.println("Data from Coordinator");
			} else {
				System.out.println("Unknown data fromat!");
			}
		}

	}

	public void ProcessRebootData(byte[] topoArray) {

		String sessionId = String.format("%02x", topoArray[session_index]);
		int statusId = topoArray[status_index];
		String statusStr = statusResult[statusId];
		if (statusId != 0) {
			System.out.println("Command failed with error : " + statusStr);
			LOG.error("Command failed with error : " + statusStr);
		} else {
			byte apiId = topoArray[api_index];
			String macStr = buildMac(topoArray, 5, 8);
			System.out.println("mac id " + macStr);
			if (apiId == isRemote) {
				// data from end node
				System.out.println("Data from End node");
			} else if (apiId == isLocal) {
				// data from coordinator
				System.out.println("Data from Coordinator");
			} else {
				System.out.println("Unknown data fromat!");
			}
		}
	}

	public void ProcessCarData(byte[] topoArray) {
		int statusId = topoArray[3];
		String statusStr = statusResult[statusId];
		if (statusId != 0) {
			System.out.println("Command failed with error : " + statusStr);
			LOG.error("Command failed with error : " + statusStr);
		}
		else{
		// Extract info from Car data	
		String macStr = buildMac(topoArray, 8, 8);
		String radio_channel = String.format("%02x", topoArray[4]);
		int occupancy = topoArray[5];
		String dataTosend = "Pkdt,"+macStr +","+occupancy;
		// Send to kinesis		
		AWSUtil.getInstance().sendToKinesis(dataTosend);				
		}
	}
}