//package at.tugraz.ist.catdroid.hid.server.robots;
//
//import java.awt.event.KeyEvent;
//import java.util.ArrayList;
//
//import junit.framework.TestCase;
//
//public class RobotMapperTest extends TestCase {
//
//	public void testLookup() {
//		try {
//			RobotMapper rm = new RobotMapper();
//			
//			byte b = 4;
//			Integer a = rm.lookup(b);
//			if ((a == null) || (a != KeyEvent.VK_A))
//				assertTrue(false);
//			
//			b = 29;
//			Integer z = rm.lookup(b);
//			if ((z == null) || (z != KeyEvent.VK_Z))
//				assertTrue(false);
//			
//			b = 39;
//			Integer zero = rm.lookup(b);
//			if ((zero == null) || (zero != KeyEvent.VK_0))
//				assertTrue(false);
//			
//			b = 38;
//			Integer nine = rm.lookup(b);
//			if ((nine == null) || (nine != KeyEvent.VK_9))
//				assertTrue(false);
//		} catch (Exception e) {
//			assertTrue(false);
//		}
//	}
//	
//	public void testGetKeyList() {
//		byte[] values = new byte[10];
//		
//		values[0] = 0;
//		values[1] = 0;
//		values[2] = 0x67;
//		values[3] = 0;
//		values[4] = 29;
//		values[5] = 29;
//		values[6] = 29;
//		values[7] = 29;
//		values[8] = 29;
//		values[9] = 29;
//		
//		try {
//			RobotMapper rm = new RobotMapper();
//			
//			ArrayList<Integer> list = rm.getKeyList(values);
//			
//			assertNotNull(list);
//			assertTrue(list.size() > 0);
//			assertTrue(list.get(0) == KeyEvent.VK_CONTROL);
//			assertTrue(list.get(1) == KeyEvent.VK_SHIFT);
//			assertTrue(list.get(2) == KeyEvent.VK_SHIFT);
//			assertTrue(list.get(3) == KeyEvent.VK_ALT);
//			assertTrue(list.get(4) == KeyEvent.VK_ALT_GRAPH);
//			assertTrue(list.size() == 11);
//		} catch (Exception e) {
//			assertTrue(false);
//		}
//	}
//	
//	public void testIllegalLookup() {
//		try {
//			RobotMapper rm = new RobotMapper();
//			byte b = -1;
//			Integer i = rm.lookup(b);
//			if (i != null)
//				assertTrue(false);
//		} catch (Exception e) {
//			assertTrue(false);
//		}
//	}
//}
