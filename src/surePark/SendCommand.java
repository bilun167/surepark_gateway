package surePark;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

public class SendCommand {

	private static final Logger LOG = Logger.getLogger(SendCommand.class);
	private OutputStream out;
	private byte frameId;
	String newLine = System.getProperty("line.separator");

	public SendCommand(OutputStream out, byte frameId) {
		this.out = out;
		this.frameId = frameId;
	}

	public SendCommand(OutputStream out) {
		this.out = out;
	}

	public String sendRebootAllCommand() {
		try {
			byte[] rebootArr = CoordinatorUtils
					.constructRebootAllCommand(frameId);
			this.out.write(rebootArr);
			this.out.flush();

			System.out
					.println(newLine
							+ "Writing to Coordinator - reboot All.......................\n"
							+ toHexString(rebootArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - reboot All.......................\n"
						+ toHexString(rebootArr));
			}
			return "SUCCESS";

		} catch (IOException e) {
			System.out.println("Exception while sending Reboot ALL"
					+ e.getMessage());
			LOG.error("Exception while sending Reboot ALL" + e.getMessage());
			return e.getMessage();
		}
	}

	public String sendRebootCoordinatorCommand() {
		try {
			byte[] rebootArr = CoordinatorUtils
					.constructRebootCoordinatorCommand(frameId);
			this.out.write(rebootArr);
			this.out.flush();

			System.out
					.println(newLine
							+ "Writing to Coordinator - reboot Coordinator.......................\n"
							+ toHexString(rebootArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - reboot Coordinator.......................\n"
						+ toHexString(rebootArr));
			}
			return "SUCCESS";
		} catch (IOException e) {
			System.out.println("Exception while sending Reboot Coordinator"
					+ e.getMessage());
			LOG.error("Exception while sending Reboot Coordinator"
					+ e.getMessage());
			return e.getMessage();
		}
	}

	public String sendRebootOneNodeCommand(String unicast_str) {
		try {

			byte unicast[] = Hex.decodeHex(unicast_str.toCharArray());
			byte[] rebootArr = CoordinatorUtils.constructRebootOneNodeCommand(
					frameId, unicast[0], unicast[1]);
			this.out.write(rebootArr);
			this.out.flush();

			System.out
					.println(newLine
							+ "Writing to Coordinator - reboot one.......................\n"
							+ toHexString(rebootArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - reboot one.......................\n"
						+ toHexString(rebootArr));
			}
			return "SUCCESS";
		} catch (IOException e) {
			System.out.println("Exception while sending Reboot one"
					+ e.getMessage());
			LOG.error("Exception while sending Reboot one" + e.getMessage());
			return e.getMessage();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String SendTopologyCommand() {
		try {
			byte[] topoArr = CoordinatorUtils.constructTOPOCommand(frameId);
			this.out.write(topoArr);
			this.out.flush();

			System.out
					.println(newLine
							+ "Writing to Coordinator - Send Topology.......................\n"
							+ toHexString(topoArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - Send Topology.......................\n"
						+ toHexString(topoArr));
			}
			return "SUCCESS";

		} catch (IOException e) {
			System.out
					.println("Exception while Send Topology" + e.getMessage());
			LOG.error("Exception while Send Topology" + e.getMessage());
			return e.getMessage();
		}
	}

	public String SendTopologyCoordinatorCommand() {
		try {
			byte[] topoArr = CoordinatorUtils
					.constructTOPOCoordinatorCommand(frameId);
			this.out.write(topoArr);
			this.out.flush();

			System.out
					.println(newLine
							+ "Writing to Coordinator - Send Topology Coordinator.......................\n"
							+ toHexString(topoArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - Send Topology Coordinator.......................\n"
						+ toHexString(topoArr));
			}
			return "SUCCESS";

		} catch (IOException e) {
			System.out.println("Exception while Send Topology Coordinator"
					+ e.getMessage());
			LOG.error("Exception while Send Topology Coordinator"
					+ e.getMessage());
			return e.getMessage();
		}
	}

	public String SendTopologyOneNodeCommand(String unicast_str) {
		try {
			byte unicast[] = Hex.decodeHex(unicast_str.toCharArray());
			byte[] topoArr = CoordinatorUtils.constructTOPOOneNodeCommand(
					frameId, unicast[0], unicast[1]);
			this.out.write(topoArr);
			this.out.flush();

			System.out
					.println(newLine
							+ "Writing to Coordinator - Send Topology one node.......................\n"
							+ toHexString(topoArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - Send Topology one node.......................\n"
						+ toHexString(topoArr));
			}
			return "SUCCESS";

		} catch (IOException e) {
			System.out.println("Exception while Send Topology one node"
					+ e.getMessage());
			LOG.error("Exception while Send Topology one node" + e.getMessage());
			return e.getMessage();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}

	}

	public String goToAPIMode() {
		try {
			byte[] topoArr = { 0x61, 0x74, 0x61, 0x70, 0x0D, 0x0A };
			this.out.write(topoArr);
			this.out.flush();

			System.out
					.println(newLine
							+ "Writing to Coordinator - Enter API mode.......................\n"
							+ toHexString(topoArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - Enter API mode.......................\n"
						+ (topoArr).toString());
			}
			return "SUCCESS";

		} catch (IOException e) {
			System.out.println("Exception while Enter API mode"
					+ e.getMessage());
			LOG.error("Exception while Enter API mode" + e.getMessage());
			return e.getMessage();
		}

	}

	public static String toHexString(byte[] ba) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < ba.length; i++)
			str.append(" " + String.format("%x", ba[i]));
		return str.toString();
	}
}