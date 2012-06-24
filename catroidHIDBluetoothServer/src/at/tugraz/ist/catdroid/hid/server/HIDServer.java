
package at.tugraz.ist.catdroid.hid.server;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class HIDServer {
	
private static boolean testMode = false;
private static String SERVER_NAME = "WirelesHumanInterfaceServer";
    
    public static void main(String[] args) {
    	if (args.length >= 1 && args[0].equalsIgnoreCase("--mode=test")) {
    		testMode = true;
    		System.out.println("Run in Test Mode");
    	}
    	
        initServerConnection();
    }
    public static void initServerConnection() {
    LocalDevice local = null;

    StreamConnectionNotifier notifier;
    StreamConnection connection = null;

    try {
      
      String url = "";
      local = LocalDevice.getLocalDevice();
      local.setDiscoverable(DiscoveryAgent.GIAC);

      UUID uuid = new UUID("04c6093b00001000800000805f9b34fb", false);
      System.out.println(uuid.toString());

      url = "btspp://localhost:" + uuid.toString() + ";name=" + SERVER_NAME;
      notifier = (StreamConnectionNotifier) Connector.open(url);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    Thread receiveThread;
    //while (true) {
      try {
        System.out.println("waiting for connection...");
        connection = notifier.acceptAndOpen();

        receiveThread = new Thread(new ReceiveBytesThread(connection, testMode));
        receiveThread.start();

      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
    //}
    try {
		receiveThread.join();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }
}
