package surePark;

import java.io.IOException;
import java.io.OutputStream;

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
	
	public void sendRebootOneCommand() {
		try {
			byte[] rebootArr = CoordinatorUtils.constructRebootOneCommand(frameId);
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