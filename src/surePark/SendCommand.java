package surePark;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class SendCommand {
	
	private static final Logger LOG = Logger.getLogger(SendCommand.class);
	private OutputStream out;
	private byte frameId;

	public SendCommand(OutputStream out, byte frameId) {
		this.out = out;
		this.frameId = frameId;
	}

	public void sendRebootAllCommand() {
		try {
			byte[] rebootArr = CoordinatorUtils.constructRebootAllCommand(frameId);
			this.out.write(rebootArr);
			this.out.flush();
			
			System.out.println("------writing to Coordinator - reboot All---------\n"+ rebootArr);
			if (LOG.isInfoEnabled()) {
				LOG.info("------writing to Coordinator - reboot All---------\n"+ rebootArr);}
			
			Thread.sleep(1000);
			}
		catch (IOException | InterruptedException e) {
			System.out.println("Exception while sending Reboot ALL"+e.getMessage());
			LOG.info("Exception while sending Reboot ALL"+e.getMessage());}
		}
	
	public void sendRebootOneCommand() {
		try {
			byte[] rebootArr = CoordinatorUtils.constructRebootOneCommand(frameId);
			this.out.write(rebootArr);
			this.out.flush();
			
			System.out.println("------writing to Coordinator - reboot one---------\n"+ rebootArr);
			if (LOG.isInfoEnabled()) {
				LOG.info("------writing to Coordinator - reboot one---------\n"+ rebootArr);}
			
			Thread.sleep(1000);
			}
		catch (IOException | InterruptedException e) {
			System.out.println("Exception while sending Reboot one"+e.getMessage());
			LOG.info("Exception while sending Reboot one"+e.getMessage());}
		}
	
	public void SendTopologyCommand() {
		try {
			byte[] topoArr = CoordinatorUtils.constructTOPOCommand(frameId);
			this.out.write(topoArr);
			this.out.flush();
			
			System.out.println("------writing to Coordinator - Send Topology---------\n"+ topoArr);
			if (LOG.isInfoEnabled()) {
				LOG.info("------writing to Coordinator - Send Topology---------\n"+ topoArr);}
			
			Thread.sleep(1000);
			}
		catch (IOException | InterruptedException e) {
			System.out.println("Exception while Send Topology"+e.getMessage());
			LOG.info("Exception while Send Topology"+e.getMessage());}
		}
	}