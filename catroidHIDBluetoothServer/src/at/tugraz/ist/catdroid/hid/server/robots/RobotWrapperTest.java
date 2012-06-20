//package at.tugraz.ist.catdroid.hid.server.robots;
//
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import java.awt.MouseInfo;
//import java.awt.event.InputEvent;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.util.ArrayList;
//
//import javax.swing.JFrame;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//
//import junit.framework.TestCase;
//
//public class RobotWrapperTest extends TestCase {
//
//	protected boolean keyAssert = false;
//	protected boolean mouseAssert = false;
//
//	public void testInvalidKey() {
//		try {
//			RobotWrapper rw = new RobotWrapper();
//			ArrayList<Integer> list = new ArrayList<Integer>();
//			list.add(-10);
//			rw.keyPressAndRelease(list);
//			assertTrue(false);
//		} catch (Exception e) {
//			// test successful
//		}
//	}
//
//	public void testKeyPressed() {
//		JFrame frame = new JFrame();
//
//		frame.addKeyListener(new KeyAdapter() {
//			public void keyPressed(KeyEvent ke) {
//				char c = ke.getKeyChar();
//
//				RobotWrapperTest.this.keyAssert = (c == KeyEvent.VK_5);
//			}
//		});
//
//		frame.setVisible(true);
//
//		try {
//			RobotWrapper rw = new RobotWrapper();
//			ArrayList<Integer> list = new ArrayList<Integer>();
//			list.add(KeyEvent.VK_5);
//			rw.keyPressAndRelease(list);
//			Thread.sleep(100);
//		} catch (Exception e) {
//			assertTrue(false);
//		}
//
//		assertTrue(keyAssert);
//	}
//
//	public void testMouseMove() {
//		int x = MouseInfo.getPointerInfo().getLocation().x;
//		int y = MouseInfo.getPointerInfo().getLocation().y;
//
//		x += 50;
//		y += 50;
//
//		RobotWrapper rw = null;
//		try {
//			rw = new RobotWrapper();
//			rw.mouseMove(x, y);
//		} catch (Exception e) {
//			assertTrue(false);
//		}
//
//		int xMove = MouseInfo.getPointerInfo().getLocation().x;
//		int yMove = MouseInfo.getPointerInfo().getLocation().y;
//
//		assertTrue(xMove == x);
//		assertTrue(yMove == y);
//	}
//
//	public void testMouseClicked() {
//		JFrame frame = new JFrame();
//
//		frame.addMouseListener(new MouseListener() {
//			public void mousePressed(MouseEvent e) {
//			}
//
//			public void mouseReleased(MouseEvent e) {
//			}
//
//			public void mouseEntered(MouseEvent e) {
//			}
//
//			public void mouseExited(MouseEvent e) {
//			}
//
//			public void mouseClicked(MouseEvent e) {
//				mouseAssert = true;
//			}
//		});
//
//		frame.setPreferredSize(new Dimension(100, 100));
//		frame.pack();
//		frame.setVisible(true);
//
//		try {
//			Thread.sleep(100);
//			RobotWrapper rw = new RobotWrapper();
//			rw.mouseMove(40, 40);
//			rw.mouseClick(InputEvent.BUTTON1_MASK);
//			Thread.sleep(100);
//		} catch (Exception e) {
//			assertTrue(false);
//		}
//
//		assertTrue(mouseAssert);
//
//	}
//
//	public void testMouseWheel() {
//		JFrame frame = new JFrame();
//
//		JScrollPane jspScroll = new JScrollPane();
//		JTextArea jtaArea = new JTextArea();
//		jtaArea.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n"
//				+ "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,\n"
//				+ "when an unknown printer took a galley of type and scrambled it to make a type specimen book.\n"
//				+ "It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.\n"
//				+ "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,\n"
//				+ "and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
//
//		jspScroll.getViewport().add(jtaArea);
//		frame.setPreferredSize(new Dimension(200, 100));
//		frame.add(jspScroll, BorderLayout.CENTER);
//		frame.pack();
//		frame.setVisible(true);
//
//		int wheel = 52;
//
//		RobotWrapper rw = null;
//		try {
//			Thread.sleep(100);
//			rw = new RobotWrapper();
//			rw.mouseMove(40, 40);
//			Thread.sleep(100);
//			rw.mouseWheel(wheel);
//		} catch (Exception e) {
//			assertTrue(false);
//		}
//
//		if (jspScroll.getVerticalScrollBar().getValue() != wheel) {
//			assertTrue(false);
//		}
//	}
//}
