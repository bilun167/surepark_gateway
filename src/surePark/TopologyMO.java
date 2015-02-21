package surePark;

public class TopologyMO {
 private String ID;
 private String MAC_ID;
 private String FIRMWARE_ID;
 private String UNICAST_ID;
 private String NET_ID;
 
 public TopologyMO(String ID,String MAC_ID, String FIRMWARE_ID, String UNICAST_ID, String NET_ID){
	 this.ID=ID;
	 this.MAC_ID=MAC_ID;
	 this.FIRMWARE_ID=FIRMWARE_ID;
	 this.UNICAST_ID=UNICAST_ID;
	 this.NET_ID=NET_ID;
 }
 
public String getID() {
	return ID;
}
public void setID(String iD) {
	ID = iD;
}
public String getMAC_ID() {
	return MAC_ID;
}
public void setMAC_ID(String mAC_ID) {
	MAC_ID = mAC_ID;
}
public String getFIRMWARE_ID() {
	return FIRMWARE_ID;
}
public void setFIRMWARE_ID(String fIRMWARE_ID) {
	FIRMWARE_ID = fIRMWARE_ID;
}
public String getUNICAST_ID() {
	return UNICAST_ID;
}
public void setUNICAST_ID(String uNICAST_ID) {
	UNICAST_ID = uNICAST_ID;
}
public String getNET_ID() {
	return NET_ID;
}
public void setNET_ID(String nET_ID) {
	NET_ID = nET_ID;
}
	
}
