package surePark;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class TalkWithCoordinator
{
public TalkWithCoordinator()
{
    super();
}

void connect ( String portName ) throws Exception
{
    CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
    if ( portIdentifier.isCurrentlyOwned() )
    {
        System.out.println("Error: Port is currently in use");
    }
    else
    {
        System.out.println("Connect 1/2");
        CommPort commPort = portIdentifier.open(this.getClass().getName(),6000);

        if ( commPort instanceof SerialPort )
        {
            System.out.println("Connect 2/2");
            SerialPort serialPort = (SerialPort) commPort;
            System.out.println("BaudRate: " + serialPort.getBaudRate());
            System.out.println("DataBIts: " + serialPort.getDataBits());
            System.out.println("StopBits: " + serialPort.getStopBits());
            System.out.println("Parity: " + serialPort.getParity());
            System.out.println("FlowControl: " + serialPort.getFlowControlMode());
            serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            System.out.println("BaudRate: " + serialPort.getBaudRate());
            System.out.println("DataBIts: " + serialPort.getDataBits());
            System.out.println("StopBits: " + serialPort.getStopBits());
            System.out.println("Parity: " + serialPort.getParity());
            System.out.println("FlowControl: " + serialPort.getFlowControlMode());
            InputStream in = serialPort.getInputStream();
            OutputStream out = serialPort.getOutputStream();

            (new Thread(new SerialReader(in))).start();
            (new Thread(new SerialWriter(out))).start();

        }
        else
        {
            System.out.println("Error: Only serial ports are handled by this example.");
        }
    }     
}

/** */
public static class SerialReader implements Runnable 
{
    InputStream in;

    public SerialReader ( InputStream in )
    {
        this.in = in;
    }

    public void run ()
    {
        byte[] buffer = new byte[1024];
        int len = -1;
        try
        {
            while ( ( len = this.in.read(buffer)) > -1 )
            {
                System.out.println("Received a signal.");
                String str=new String(buffer,0,len);
                System.out.println("Byte array is["+buffer+"] string is["+str+"]");
                System.out.println("Hex is "+TalkWithCoordinator.toHexString(buffer,str));
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }            
    }
}

public static String toHexString(byte[] ba,String str2) {
    StringBuilder str = new StringBuilder();
    for(int i = 0; i < str2.length(); i++)
        str.append(String.format("%x", ba[i]));
    return str.toString();
}


/** */
public static class SerialWriter implements Runnable 
{
    OutputStream out;

    public SerialWriter ( OutputStream out )
    {
        this.out = out;
    }

    public void run ()
    {
        try
        {   
        	/*
        	String APIMODE= "ATAP";
        	byte[] apiMode = APIMODE.getBytes(Charset.forName("UTF-8"));
            System.out.println("writing to Coordinator - ATAP: "+apiMode);

        	this.out.write(apiMode);
            this.out.flush();
        	*/
            byte[] array = {0x7E, 0x0D, 0x17 , 0x02, 0x40 , 0x30 , 0x00 , 0x00 , 0x00 , 0x00, 0x00 ,0x00 , 0x00 , 0x00 , 0x00, 0x00, 0x72 };
            //System.out.println("writing to Coordinator - reboot: "+array);
            
            {  Thread.sleep(2000);
            System.out.println("awake now: "+array);

               this.out.write(array);
               this.out.flush();
               Thread.sleep(1000);  
               System.out.println("thread stop");

            }                
        }
        catch ( IOException | InterruptedException e )
        {
            e.printStackTrace();
        }            
    }
}

public static void main ( String[] args )
{
    try
    {
        (new TalkWithCoordinator()).connect("/dev/tty.usbserial-A6XXH3D2");
    }
    catch ( Exception e )
    {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
}
