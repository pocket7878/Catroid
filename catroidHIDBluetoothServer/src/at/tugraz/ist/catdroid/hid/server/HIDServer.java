
package at.tugraz.ist.catdroid.hid.server;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class HIDServer implements Runnable {
	
private static boolean testMode = false;
private static String SERVER_NAME = "WirelesHumanInterfaceServer";
    
private static StreamConnectionNotifier notifier;
private static StreamConnection connection = null;

private static Collection<Thread> threadList = new LinkedList<Thread>();

    public static void main(String[] args) {
    	if (args.length >= 1 && args[0].equalsIgnoreCase("--mode=test")) {
    		testMode = true;
    		System.out.println("Run in Test Mode");
    	}
    	
        initServerConnection();
    }
    
    public static void initServerConnection() {
    LocalDevice local = null;

    try {
      
      String url = "";
      local = LocalDevice.getLocalDevice();
      local.setDiscoverable(DiscoveryAgent.GIAC);

      UUID uuid = new UUID("04c6093b00001000800000805f9b34fb", false);
      System.out.println("Service UUID: " + uuid.toString());
      System.out.println();

      url = "btspp://localhost:" + uuid.toString() + ";name=" + SERVER_NAME;
      notifier = (StreamConnectionNotifier) Connector.open(url);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    
    Thread w = new Thread(new HIDServer());
    w.start();
    
    try {
		System.in.read();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	//TODO find better solution!
	w.stop();
	stopAllThreads();

    }
//    try {
//		receiveThread.join();
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	}
	@Override
	public void run() {
	    Thread receiveThread;
	    int i = 1;
	    while (true) {
	      try {
	        System.out.println("Waiting for new connection...");
	        connection = notifier.acceptAndOpen();
	        System.out.println("  - Connected with device # "+ i++);
	        System.out.println();

	        receiveThread = new Thread(new ReceiveBytesThread(connection, testMode));
	        receiveThread.start();
	        threadList.add(receiveThread);

	      } catch (Exception e) {
	        e.printStackTrace();
	        return;
	      }
	    }
	}
	
	private static void stopAllThreads()
	{
		for (Thread t : threadList)
		{
			if (t != null && t.isAlive())
				t.stop();
		}
		
		System.out.println("----------------------------------");
		System.out.println("Destroyed all Connections.");
	}
}