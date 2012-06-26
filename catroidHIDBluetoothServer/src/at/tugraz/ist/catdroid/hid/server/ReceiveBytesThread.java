/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.tugraz.ist.catdroid.hid.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.microedition.io.StreamConnection;

import at.tugraz.ist.catdroid.hid.server.robots.RobotMapper;
import at.tugraz.ist.catdroid.hid.server.robots.RobotWrapper;


/**
 *
 * @author Pointner
 */
class ReceiveBytesThread implements Runnable {

    private StreamConnection stream_connection;
    private int EXIT_CODE = -1;
    private boolean testMode;

    public ReceiveBytesThread(StreamConnection connection, boolean testMode) {
        stream_connection = connection;
        this.testMode = testMode;
    }

    public void run() {
        
	    InputStream input_stream = null;
	    OutputStream output_stream = null;
	    
        try {
        	input_stream = stream_connection.openInputStream();
        	
            if (testMode)
            	output_stream = stream_connection.openDataOutputStream();
            
            //System.out.println("waiting for input...");

            while (true) {
                
                byte[] buffer = new byte[10];
                input_stream.read(buffer);
                
                if (testMode) {
		            output_stream.write(buffer);
		            output_stream.flush();
                }
                else {
	                if (lookupCMD(buffer) == EXIT_CODE) {
	                    break;
	                }
                }
            }

	        if (input_stream != null)
	        	input_stream.close();
            if (output_stream != null)
            	output_stream.close();
            
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private int lookupCMD(byte[] input) {
      RobotMapper rm = null;
      RobotWrapper rw = null;
      byte[] exit_byte = { -1 };
      
      if(input == exit_byte)
      {
          return -1;
      }
      
        try {
           rm = new RobotMapper();
           rw = new RobotWrapper();
        } catch (Exception e) {
            return -1;
        }
        
        ArrayList<Integer> list = rm.getKeyList(input);
        try {
            rw.keyPressAndRelease(list);
        } catch (Exception e) {
            return -1;
        }
        
        return 0;
    }
}
