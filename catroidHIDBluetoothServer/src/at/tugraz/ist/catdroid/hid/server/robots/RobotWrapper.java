package at.tugraz.ist.catdroid.hid.server.robots;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.List;

public class RobotWrapper {

	private Robot robot;

	public RobotWrapper() throws AWTException {
		robot = new Robot();
		robot.setAutoDelay(250);
	}

	public void mouseMove(int x, int y) {
		robot.mouseMove(x, y);
	}

	public void mouseWheel(int x) {
		robot.mouseWheel(x);
	}

	public void mouseClick(int code) {
		robot.mousePress(code);
		robot.delay(100);
		robot.mouseRelease(code);
		robot.delay(100);
	}

	public void keyPressAndRelease(List<Integer> codes) throws AWTException {
		for (int i = 0; i < codes.size(); i++) {
			robot.keyPress(codes.get(i));
		}

		for (int i = codes.size() - 1; i >= 0 ; i--) {
			robot.keyRelease(codes.get(i));
		}
	}
}