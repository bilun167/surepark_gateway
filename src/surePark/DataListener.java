package surePark;

import java.io.IOException;
import java.io.InputStream;

public class DataListener implements Runnable 
{
    InputStream in;
    static int i=0;

    public DataListener ( InputStream in )
    {
        this.in = in;
    }

    public void run ()
    {
        byte[] buffer = new byte[1024];
        int len = -1;
        try
        {
            System.out.println("Started listening");
            while ( ( len = this.in.read(buffer)) > -1 )
            {
                System.out.println("Received a signal."+ i++);
                String str=new String(buffer,0,len);
                System.out.println( "string is["+str+"]");
                System.out.println("Hex is "+TalkWithCoordinator.toHexString(buffer,str));
            }
            System.out.println("Stopped listening");
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }            
    }
    
    public static String toHexString(byte[] ba,String str2) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < str2.length(); i++)
            str.append(String.format("%x", ba[i]));
        return str.toString();
    }

}
