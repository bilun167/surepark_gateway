package surePark;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class DBUtils {

	private  Connection sqLiteConn = null;

	public DBUtils(){
	      try {
	    	String sqliteDb = PropertyUtils.getProperty("sqlite_home", "config.properties");  
		    Class.forName("org.sqlite.JDBC");
			sqLiteConn = DriverManager.getConnection("jdbc:sqlite:"+sqliteDb);
			//sqLiteConn = DriverManager.getConnection("jdbc:sqlite:"+"/Users/ashutoshprasar/Documents/sqlite-autoconf-3080802/SurePark.db");
		    sqLiteConn.setAutoCommit(false);
		    System.out.println("Opened database successfully");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      catch(ClassNotFoundException e){
	    	  
	      }
	}
	
	
	public void insertTopoRecords(TopologyMO topologyMO) throws SQLException {
		try {
			Statement stmt = sqLiteConn.createStatement();
			String sql = "INSERT INTO TOPOLOGY VALUES ('" + topologyMO.getID()
					+ "', '" + topologyMO.getMAC_ID() + "', '"
					+ topologyMO.getFIRMWARE_ID() + "', '"
					+ topologyMO.getUNICAST_ID() + "', '"
					+ topologyMO.getNET_ID() + "');";
			stmt.executeUpdate(sql);
			stmt.close();
			sqLiteConn.commit();
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}
		finally{
			sqLiteConn.close();			
		}
	}
	
	public ArrayList<TopologyMO> getTopoRecords(String reqId) throws SQLException{
	      ArrayList<TopologyMO> topoArr = new ArrayList<TopologyMO>();
		try{
		  Statement stmt = sqLiteConn.createStatement();
	      ResultSet rs = stmt.executeQuery( "select * from TOPOLOGY where ID='"+reqId+"'" );
	         while ( rs.next() ) {
	         String  ID = rs.getString("ID");
	         String  MAC_ID = rs.getString("MAC_ID");
	         String  FIRMWARE_ID = rs.getString("FIRMWARE_ID");
	         String  UNICAST_ID = rs.getString("UNICAST_ID");
	         String  NET_ID = rs.getString("NET_ID");
	         System.out.println(ID+","+ MAC_ID+","+ FIRMWARE_ID+","+ UNICAST_ID+","+ NET_ID);
             topoArr.add(new TopologyMO(ID, MAC_ID, FIRMWARE_ID, UNICAST_ID, NET_ID));
	      }
		 	 rs.close();
		     stmt.close();
		}
		catch(Exception e){
	         System.out.println("Exception"+e);
		}
		finally{
		      sqLiteConn.close();
		}
		return topoArr;
	}
	
	
	public static void main(String args[]){
		try {
			//TopologyMO topo = new TopologyMO("0124", "1212121212121278", "2332", "3422", "4243");
			//new DBUtils().insertTopoRecords(topo);
			ArrayList<TopologyMO> arrTopo=  new DBUtils().getTopoRecords("0124");
			Iterator<TopologyMO> it = arrTopo.iterator();
			while(it.hasNext())
			{
				TopologyMO topoMo = it.next();
				System.out.println(topoMo.getID()+","+topoMo.getMAC_ID()+","+topoMo.getFIRMWARE_ID()+","+topoMo.getUNICAST_ID()+","+topoMo.getNET_ID());
			    //Do something with obj
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
