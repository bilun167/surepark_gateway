package surePark;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class ConnectToCoordinator {
	
private static final Logger LOG = Logger.getLogger(sureParkServlet.class);
private final int TIME_OUT=6000;
private final int BAUD_RATE=115200;



public SerialPort connect(String portName) throws Exception{
	
    CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
    SerialPort serialPort = null;
    if ( portIdentifier.isCurrentlyOwned() )
    {
        System.out.println("Error: Port is currently in use");
        LOG.error("Error: Port is currently in use");
    }
    else
    {
        System.out.println("Initiating Connection......");
        if (LOG.isInfoEnabled()) LOG.info("Initiating Connection......");
        
        CommPort commPort = portIdentifier.open(this.getClass().getName(),TIME_OUT);

        if ( commPort instanceof SerialPort )
        {
        	System.out.println("Connection Established.\nSetting serial port parameters");
            if (LOG.isInfoEnabled()) LOG.info("Connection Established.\nSetting serial port parameters");
            
            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(BAUD_RATE,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            
            System.out.println("Serial port parameters----------------");
            System.out.println("BaudRate: " + serialPort.getBaudRate());
            System.out.println("DataBIts: " + serialPort.getDataBits());
            System.out.println("StopBits: " + serialPort.getStopBits());
            System.out.println("Parity: " + serialPort.getParity());
            System.out.println("FlowControl: " + serialPort.getFlowControlMode());
            System.out.println("-------------------------------------");
            
            if (LOG.isInfoEnabled()){
            LOG.info("Serial port parameters----------------");	
            LOG.info("BaudRate: " + serialPort.getBaudRate());
            LOG.info("DataBIts: " + serialPort.getDataBits());
            LOG.info("StopBits: " + serialPort.getStopBits());
            LOG.info("Parity: " + serialPort.getParity());
            LOG.info("FlowControl: " + serialPort.getFlowControlMode());
            LOG.info("-------------------------------------");
            }
            

        }
        else
        {
            System.out.println("Error: Only serial ports are handled by this example.");
            LOG.error("Error: Only serial ports are handled by this example.");
        }
    }   
    return serialPort;

}
}
