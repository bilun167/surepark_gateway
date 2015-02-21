package surePark;

import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;


public class RequestHandler {

	// get from db/file later
	private static final String REBOOT_ALL = "RebootAllNodesRequest";
	private static final String REBOOT_ONE = "RebootOneNodeRequest";
	private static final String REBOOT_Coordinator = "RebootCoordinatorRequest";
	private static final String TOPOLOGY_ALL = "GetAllNodesInfoRequest";
	private static final String TOPOLOGY_ONE = "GetOneNodeInfoRequest";
	private static final String TOPOLOGY_COORDINATOR = "GetCoordinatorInfoRequest";
	private static final String REQUEST_STATUS = "GetRequestStatus";

	private static final String FRAME_PROP_FILE = "frame_count.properties";
	private static final String DB_COUNT_PROP_FILE = "db_count.properties";    	
	
	private static final Logger LOG = Logger.getLogger(RequestHandler.class);
	private static RequestHandler REQUEST_HANDLER;
	private RequestHandler(){
		
	}
	
	public static RequestHandler getInstance(){
		if(REQUEST_HANDLER==null){
			REQUEST_HANDLER = new RequestHandler();
		}
		return REQUEST_HANDLER;
	}
	
	
	public synchronized String getRequestStatus(String reqId) {
		ArrayList<TopologyMO> arrTopo=null;
		StringBuffer nodeInfo = new StringBuffer("<node-list>");
		try {
			arrTopo = new DBUtils().getTopoRecords(reqId);
			Iterator<TopologyMO> it = arrTopo.iterator();
			while (it.hasNext()) {
				TopologyMO topoMo = it.next();
                nodeInfo.append("<node>");
                nodeInfo.append("<mac-id>"+topoMo.getMAC_ID()+"</mac-id>");
                nodeInfo.append("<firmware-id>"+topoMo.getFIRMWARE_ID()+"</firmware-id>");
                nodeInfo.append("<net-id>"+topoMo.getNET_ID()+"</net-id>");
                nodeInfo.append("<unicast-id>"+topoMo.getUNICAST_ID()+"</unicast-id>");                
                nodeInfo.append("</node>");                
			}
            nodeInfo.append("</node-list>");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeInfo.toString();
	}

	
	public synchronized ReturnStatus handleRequest(String request, OutputStream OUTPUT_STREAM, String uniCastId) {
			    
		int db_counter = Integer.parseInt(PropertyUtils.getInstance().getRefreshProperty("count",DB_COUNT_PROP_FILE));
		byte frame_counter = Byte.parseByte(PropertyUtils.getInstance().getRefreshProperty("count",FRAME_PROP_FILE));
		
		String reqIdStr = String.valueOf(db_counter)+String.valueOf(String.format("%02x", frame_counter));
		System.out.println("Request Id is"+Thread.currentThread().getName()+"-"+reqIdStr);
		System.out.println("value of frame counter new id is"+frame_counter);
		String resStatus = null;
		ReturnStatus returnStatus = new ReturnStatus();
		/*
		 * REBOOT ALL
		 */
		if (request.compareToIgnoreCase(REBOOT_ALL) == 0) {
			resStatus = new SendCommand(OUTPUT_STREAM, (byte) frame_counter)
					.sendRebootAllCommand();
			if (resStatus.compareTo("SUCCESS") == 0) {
				returnStatus.setStatus(true);
				returnStatus.setRequestId(reqIdStr);
			} else {
				returnStatus.setStatus(false);
				returnStatus.setError(resStatus);
			}
		}

		/*
		 * REBOOT ONE
		 */
		else if (request.compareToIgnoreCase(REBOOT_ONE) == 0) {
			resStatus = new SendCommand(OUTPUT_STREAM, (byte) frame_counter)
					.sendRebootOneNodeCommand(uniCastId);
			if (resStatus.compareTo("SUCCESS") == 0) {
				returnStatus.setStatus(true);
				returnStatus.setRequestId(reqIdStr);
			} else {
				returnStatus.setStatus(false);
				returnStatus.setError(resStatus);
			}
		}

		/*
		 * REBOOT COORDINATOR
		 */
		else if (request.compareToIgnoreCase(REBOOT_Coordinator) == 0) {
			resStatus = new SendCommand(OUTPUT_STREAM, (byte) frame_counter)
					.sendRebootCoordinatorCommand();
			if (resStatus.compareTo("SUCCESS") == 0) {
				returnStatus.setStatus(true);
				returnStatus.setRequestId(reqIdStr);
			} else {
				returnStatus.setStatus(false);
				returnStatus.setError(resStatus);
			}
		}

		/*
		 * TOPOLOGY ALL
		 */
		else if (request.compareToIgnoreCase(TOPOLOGY_ALL) == 0) {
			resStatus = new SendCommand(OUTPUT_STREAM, (byte) frame_counter)
					.SendTopologyCommand();
			if (resStatus.compareTo("SUCCESS") == 0) {
				returnStatus.setStatus(true);
				returnStatus.setRequestId(reqIdStr);
			} else {
				returnStatus.setStatus(false);
				returnStatus.setError(resStatus);
			}
		}

		/*
		 * TOPOLOGY ONE
		 */
		else if (request.compareToIgnoreCase(TOPOLOGY_ONE) == 0) {
			resStatus = new SendCommand(OUTPUT_STREAM, (byte) frame_counter)
					.SendTopologyOneNodeCommand(uniCastId);
			if (resStatus.compareTo("SUCCESS") == 0) {
				returnStatus.setStatus(true);
				returnStatus.setRequestId(reqIdStr);
			} else {
				returnStatus.setStatus(false);
				returnStatus.setError(resStatus);
			}
		}

		/*
		 * TOPO COORDINATOR
		 */
		else if (request.compareToIgnoreCase(TOPOLOGY_COORDINATOR) == 0) {
			resStatus = new SendCommand(OUTPUT_STREAM, (byte) frame_counter)
					.SendTopologyCoordinatorCommand();
			if (resStatus.compareTo("SUCCESS") == 0) {
				returnStatus.setStatus(true);
				returnStatus.setRequestId(reqIdStr);
			}
		} else {
			returnStatus.setStatus(false);
			returnStatus.setError("Request Name Invalid!");
		}
		PropertyUtils.setProperty("count", Integer.toString((byte)(frame_counter + 1)),"frame_count.properties");
		System.out.println("Writing frame counter id is"+frame_counter+1);
        if(frame_counter==(byte)0xFF){
    		PropertyUtils.setProperty("count", Integer.toString(db_counter + 1),"db_count.properties");
        }
		return returnStatus;
	}
	
}


