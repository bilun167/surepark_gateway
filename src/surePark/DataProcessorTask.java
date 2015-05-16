package surePark;

import java.sql.SQLException;

import org.apache.log4j.Logger;

public class DataProcessorTask implements Runnable {

	private byte[] bArray;
	private static final Logger LOG = Logger.getLogger(DataProcessorTask.class);
	private static String MAC_COORDINATOR;

	public DataProcessorTask(byte[] bArray) {
		this.bArray = bArray;
	}

	public void run() {
		int crc_pos = bArray[DataListener.len_index] + 2;
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
			switch (bArray[DataListener.cmd_index]) {
			case DataListener.isTopo: {
				// Topology data
				// System.out.println("Topology data!");
				if (LOG.isInfoEnabled())
					LOG.info("Topology data!");
				ProcessTopoData(bArray);
				break;
			}
			case DataListener.isReboot: {
				// Reboot data
				// System.out.println("Reboot data!");
				if (LOG.isInfoEnabled())
					LOG.info("Reboot data!");
				ProcessRebootData(bArray);
				break;
			}
			default: {
				if (bArray[2] == DataListener.isCarData
						|| bArray[2] == DataListener.isRebootCarData) {
					// car data
					// System.out.println("Car data!");
					if (LOG.isInfoEnabled())
						LOG.info("Car data!");
					ProcessCarData(bArray);
				} else {
					// Corrupt data
					// System.out.println("Unknown data fromat!");
					if (LOG.isInfoEnabled())
						LOG.info("Unknown data fromat!");

				}
				break;
			}
			}

		}

	}

	public void ProcessTopoData(byte[] topoArray) {

		String sessionId = String.format("%02x",
				topoArray[DataListener.session_index]);
		int statusId = topoArray[DataListener.status_index];
		String statusStr = DataListener.statusResult[statusId];
		if (statusId != 0) {
			// LOGGING
			// System.out.println("Command failed with error : " + statusStr);
			LOG.error("Command failed with error : " + statusStr);
		} else {
			int roleId = topoArray[DataListener.role_index];
			byte apiId = topoArray[DataListener.api_index];

			String macStr = buildMac(topoArray, DataListener.mac_index, 8);
			String firmwareStr = buildHex(topoArray,
					DataListener.firmware_index, 2);
			String unicastStr = buildHex(topoArray, DataListener.unicast_index,
					2);
			String netIdStr = buildHex(topoArray, DataListener.netid_index, 2);

			// LOGGING
			// System.out.println("firmware " + firmwareStr);
			// System.out.println("unicast id " + unicastStr);
			// System.out.println("net id " + netIdStr);
			// System.out.println("mac id " + macStr);
			if (LOG.isInfoEnabled())
				LOG.info("firmware " + firmwareStr);
			if (LOG.isInfoEnabled())
				LOG.info("unicast id " + unicastStr);
			if (LOG.isInfoEnabled())
				LOG.info("net id " + netIdStr);
			if (LOG.isInfoEnabled())
				LOG.info("mac id " + macStr);

			if (apiId == DataListener.isRemote && roleId == 2) {
				// data from end node
				// System.out.println("Data from End node");
				if (LOG.isInfoEnabled())
					LOG.info("Data from End node");

				// Insert topology data into DB
				int db_counter = Integer.parseInt(PropertyUtils.getInstance()
						.getRefreshProperty("count", "db_count.properties"));
				if (sessionId == "FF")
					db_counter--;

				String reqId = String.valueOf(db_counter) + sessionId;
				TopologyMO topo = new TopologyMO(reqId, macStr, firmwareStr,
						unicastStr, netIdStr);
				try {
					if (LOG.isInfoEnabled())
						LOG.info("Insert data into DB.");
					new DBUtils().insertTopoRecords(topo);
				} catch (SQLException e) {
					LOG.error("Exception in processing topology data", e);
				}
			} else if (apiId == DataListener.isRemote && roleId == 1) {
				// data from repeater
				// System.out.println("Data from Repeater");
				if (LOG.isInfoEnabled())
					LOG.info("Data from Repeater");
			} else if (apiId == DataListener.isLocal) {
				// data from coordinator
				// System.out.println("Data from Coordinator");
				if (LOG.isInfoEnabled())
					LOG.info("Data from Coordinator");
				MAC_COORDINATOR = macStr;
			} else {
				// Unknown data format
				// System.out.println("Unknown data fromat!");
				if (LOG.isInfoEnabled())
					LOG.info("Unknown data fromat!");
			}
		}

	}

	public void ProcessRebootData(byte[] topoArray) {

		String sessionId = String.format("%02x",
				topoArray[DataListener.session_index]);
		int statusId = topoArray[DataListener.status_index];
		String statusStr = DataListener.statusResult[statusId];
		if (statusId != 0) {
			// LOGGING
			// System.out.println("Command failed with error : " + statusStr);
			LOG.error("Command failed with error : " + statusStr);
		} else {
			byte apiId = topoArray[DataListener.api_index];
			String macStr = buildMac(topoArray, 5, 8);

			// LOGGING
			// System.out.println("mac id " + macStr);
			if (LOG.isInfoEnabled())
				LOG.info("mac id " + macStr);
			if (apiId == DataListener.isRemote) {
				// data from end node
				// System.out.println("Data from End node");
				if (LOG.isInfoEnabled())
					LOG.info("Data from End node");
			} else if (apiId == DataListener.isLocal) {
				// data from coordinator
				// System.out.println("Data from Coordinator");
				if (LOG.isInfoEnabled())
					LOG.info("Data from Coordinator");
			} else {
				// System.out.println("Unknown data format!");
				if (LOG.isInfoEnabled())
					LOG.info("Unknown data fromat!");
			}
		}
	}

	public void ProcessCarData(byte[] topoArray) {
		int statusId = topoArray[3];
		String statusStr = DataListener.statusResult[statusId];
		if (statusId != 0) {
			// LOGGING
			// System.out.println("Command failed with error : " + statusStr);
			LOG.error("Command failed with error : " + statusStr);
		} else {
			// Extract info from Car data
			String macStr = buildMac(topoArray, 9, 8);
			String radio_channel = String.format("%02x", topoArray[4]);
			int occupancy = topoArray[5];
			String dataTosend = "Pkdt," + MAC_COORDINATOR + "," + macStr + ","
					+ occupancy;
			// Send to kinesis
			AWSUtil.getInstance().sendToKinesis(dataTosend);
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
		int msg_len = crcArr[0] + 2;
		System.out.println("message length is" + msg_len);
		for (int i = 2; i < crcArr[0] + 2; i++) {
			calc_crc += crcArr[i];
		}
		return calc_crc;
	}

	public static String getCoordinatorMAC() {
		return MAC_COORDINATOR;
	}

}
