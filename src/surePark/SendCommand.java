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
	
	public void sendRebootAllCommand() {
		try {
			byte[] rebootArr = CoordinatorUtils.constructRebootAllCommand(frameId);
			this.out.write(rebootArr);
			this.out.flush();
			
			System.out.println(newLine+"Writing to Coordinator - reboot All.......................\n"+toHexString(rebootArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - reboot All.......................\n"+ toHexString(rebootArr));}
			
			Thread.sleep(1000);
			}
		catch (IOException | InterruptedException e) {
			System.out.println("Exception while sending Reboot ALL"+e.getMessage());
			LOG.error("Exception while sending Reboot ALL"+e.getMessage());}
		}
	
	public void sendRebootCoordinatorCommand() {
		try {
			byte[] rebootArr = CoordinatorUtils.constructRebootCoordinatorCommand(frameId);
			this.out.write(rebootArr);
			this.out.flush();
			
			System.out.println(newLine+"Writing to Coordinator - reboot Coordinator.......................\n"+toHexString(rebootArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - reboot Coordinator.......................\n"+ toHexString(rebootArr));}
			
			Thread.sleep(1000);
			}
		catch (IOException | InterruptedException e) {
			System.out.println("Exception while sending Reboot Coordinator"+e.getMessage());
			LOG.error("Exception while sending Reboot Coordinator"+e.getMessage());}
		}
	
	public void sendRebootOneNodeCommand(String unicast_str) {
		try {
			
		    byte unicast[]=Hex.decodeHex(unicast_str.toCharArray());
			byte[] rebootArr = CoordinatorUtils.constructRebootOneNodeCommand(frameId, unicast[0], unicast[1]);
			this.out.write(rebootArr);
			this.out.flush();
			
			System.out.println(newLine+"Writing to Coordinator - reboot one.......................\n"+ toHexString(rebootArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - reboot one.......................\n"+ toHexString(rebootArr));}
			
			Thread.sleep(1000);
			}
		catch (IOException | InterruptedException e) {
			System.out.println("Exception while sending Reboot one"+e.getMessage());
			LOG.error("Exception while sending Reboot one"+e.getMessage());}
		catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

	
	public void SendTopologyCommand() {
		try {
			byte[] topoArr = CoordinatorUtils.constructTOPOCommand(frameId);
			this.out.write(topoArr);
			this.out.flush();
			
			System.out.println(newLine+"Writing to Coordinator - Send Topology.......................\n"+ toHexString(topoArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - Send Topology.......................\n"+ toHexString(topoArr));}
			
			Thread.sleep(1000);
			}
		catch (IOException | InterruptedException e) {
			System.out.println("Exception while Send Topology"+e.getMessage());
			LOG.error("Exception while Send Topology"+e.getMessage());}
		}
	
	public void SendTopologyCoordinatorCommand() {
		try {
			byte[] topoArr = CoordinatorUtils.constructTOPOCoordinatorCommand(frameId);
			this.out.write(topoArr);
			this.out.flush();
			
			System.out.println(newLine+"Writing to Coordinator - Send Topology Coordinator.......................\n"+ toHexString(topoArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - Send Topology Coordinator.......................\n"+ toHexString(topoArr));}
			
			Thread.sleep(1000);
			}
		catch (IOException | InterruptedException e) {
			System.out.println("Exception while Send Topology Coordinator"+e.getMessage());
			LOG.error("Exception while Send Topology Coordinator"+e.getMessage());}
		}
	
	
	public void SendTopologyOneNodeCommand(String unicast_str) {
		try {
		    byte unicast[]=Hex.decodeHex(unicast_str.toCharArray());
			byte[] topoArr = CoordinatorUtils.constructTOPOOneNodeCommand(frameId, unicast[0], unicast[1]);
			this.out.write(topoArr);
			this.out.flush();
			
			System.out.println(newLine+"Writing to Coordinator - Send Topology one node.......................\n"+ toHexString(topoArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - Send Topology one node.......................\n"+ toHexString(topoArr));}
			
			Thread.sleep(1000);
			}
		catch (IOException | InterruptedException e) {
			System.out.println("Exception while Send Topology one node"+e.getMessage());
			LOG.error("Exception while Send Topology one node"+e.getMessage());}
		catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}
	
	public void goToAPIMode()
	{
		try {
			byte[] topoArr = {0x61, 0x74, 0x61, 0x70 ,0x0D, 0x0A};
			this.out.write(topoArr);
			this.out.flush();
			
			System.out.println(newLine+"Writing to Coordinator - Enter API mode.......................\n"+ toHexString(topoArr));
			if (LOG.isInfoEnabled()) {
				LOG.info("Writing to Coordinator - Enter API mode.......................\n"+ (topoArr).toString());}
			
			Thread.sleep(1000);
			}
		catch (IOException | InterruptedException e) {
			System.out.println("Exception while Enter API mode"+e.getMessage());
			LOG.error("Exception while Enter API mode"+e.getMessage());}
		
	}
	
    public static String toHexString(byte[] ba) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < ba.length; i++)
            str.append(" "+String.format("%x", ba[i]));
        return str.toString();
    }
	}