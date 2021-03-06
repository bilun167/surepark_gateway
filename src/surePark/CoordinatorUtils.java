package surePark;

public class CoordinatorUtils {

	public static byte START_DLM = 0x7e;

	// Request Specific
	public static byte REMT_REQ = 0x17;
	public static byte LOCAL_REQ = 0x08;
	public static byte BROAD_CAST = 0x40;
	public static byte UNI_CAST = 0x00;

	// Commands
	public static byte REBOOT = 0x30;
	public static byte TOPOLOGY = 0x54;

	// Response specific
	public static byte REMT_RESP = (byte) 0x97;
	public static byte LOCAL_RESP = (byte) 0x88;
	public static byte STATUS_OK = 0x00;
	public static byte STATUS_ERROR = 0x01;
	public static byte INVALID_CMD = 0x02;
	public static byte INVALID_PARAM = 0x03;
	public static byte END_NODE_ROLE = 0x01;

	public static byte[] constructRebootAllCommand(byte frameId){
		byte CRC=(byte) (frameId+0x70);
		byte[] rebootArr = {START_DLM,0x0d,REMT_REQ,frameId,BROAD_CAST,REBOOT,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,CRC};
		return rebootArr;		
	}
	
	public static byte[] constructRebootCoordinatorCommand(byte frameId){
		byte CRC=(byte) (frameId+0x30);
		byte[] rebootArr = {START_DLM,0x0a,LOCAL_REQ,frameId,REBOOT,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,CRC};
		return rebootArr;		
	}
	
	public static byte[] constructRebootOneNodeCommand(byte frameId, byte unicast1, byte unicast2){
		byte CRC=(byte) (frameId+0x30+unicast1+unicast2);
		byte[] rebootArr = {START_DLM,0x0d,REMT_REQ,frameId,UNI_CAST,REBOOT,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,unicast1,unicast2,CRC};
		return rebootArr;
		}	
	
	public static byte[] constructTOPOCommand(byte frameId){
		byte CRC=(byte) (frameId+0x94);
		byte[] topoArr = {START_DLM,0x0d,REMT_REQ,frameId,BROAD_CAST,TOPOLOGY,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,CRC};
		return topoArr;		
	}
	
	public static byte[] constructTOPOCoordinatorCommand(byte frameId){
		byte CRC=(byte) (frameId+0x54);
		byte[] topoArr = {START_DLM,0x0a,LOCAL_REQ,frameId,TOPOLOGY,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,CRC};
		return topoArr;		
	}
	
	public static byte[] constructTOPOOneNodeCommand(byte frameId, byte unicast1, byte unicast2){
		byte CRC=(byte) (frameId+0x54+unicast1+unicast2);
		byte[] topoArr = {START_DLM,0x0d,REMT_REQ,frameId,UNI_CAST,TOPOLOGY,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,unicast1,unicast2,CRC};
		return topoArr;		
	}
}
