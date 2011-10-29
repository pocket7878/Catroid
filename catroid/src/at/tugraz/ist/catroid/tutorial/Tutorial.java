/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.tutorial;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import at.tugraz.ist.catroid.common.Values;

/**
 * @author faxxe
 * 
 */
public class Tutorial {
	public static final boolean DEBUG = true;

	private static final Tutorial tutorial = new Tutorial();
	private static boolean tutorialActive = false;
	private static Context context;
	private static TutorialXmlHandler txh;
	private static ArrayList[] code;
	private static TutorialThread tutorialThread = null;
	private static WindowManager.LayoutParams dragViewParameters;
	private int instructionCounter = 0;
	private TutorialState tutorialState = new TutorialState();
	public volatile ArrayList<String> notifies = new ArrayList<String>();
	public HashMap<String, TutorialThread> threads = new HashMap<String, TutorialThread>();
	public TutorialOverlay to;
	private Dialog dialog;
	WindowManager windowManager;
	int test = 0;
	private String currentNotification = "";

	private Tutorial() {

	}

	public ArrayList[] getCode() {
		return txh.getActions();
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	public Dialog getDialog() {
		return this.dialog;
	}

	public int getIdFromOpCode(String opCode) {
		if (opCode.compareTo("jump") == 0) {
			return 1137;
		}
		if (opCode.compareTo("goto") == 0) {
			return 1138;
		}
		if (opCode.compareTo("waitfor") == 0) {
			return 1139;
		}
		if (opCode.compareTo("say") == 0) {
			return 1140;
		}
		if (opCode.compareTo("appear") == 0) {
			return 1141;
		}
		if (opCode.compareTo("disappear") == 0) {
			return 1142;
		}
		if (opCode.compareTo("point") == 0) {
			return 1143;
		}
		if (opCode.compareTo("sleep") == 0) {
			return 1144;
		}
		if (opCode.compareTo("flip") == 0) {
			return 1145;
		}
		if (opCode.compareTo("dog") == 0) {
			return 1146;
		}
		if (opCode.compareTo("cat") == 0) {
			return 1147;
		}

		//add missing opCodes here!
		return -1;
	}

	public void execute(String opCode, String attribute) throws InterruptedException {
		Log.i("catroid", "executing...");
		int opCodeId = getIdFromOpCode(opCode);
		switch (opCodeId) {
			case 1137:
				jump(attribute);
				break;
			case 1138:
				goTo(attribute);
				break;
			case 1139:
				waitFor(attribute);
				break;
			case 1140:
				say(attribute);
				break;
			case 1141:
				appear(attribute);
				break;
			case 1142:
				disappear(attribute);
				break;
			case 1143:
				point(attribute);
				break;
			case 1144:
				Thread.sleep(3000);
				break;
			case 1145:
				flip(attribute);
				break;
			case 1146:
				switchToDog();
				break;
			case 1147:
				switchToCat();
				break;
			//add more commands here ;)
			default:
				//Error... please do someone implement an Errorhandler... please!
				// someone has to implement it... and if its me later!\
				Log.i("catroid", "opCode error: " + opCode + " not implemented yet");

		}

	}

	public void switchToDog() {
		to.switchToDog();
	}

	public void switchToCat() {
		to.switchToCat();
	}

	public void appear(String attribute) throws InterruptedException {
		int x = Integer.valueOf(attribute.substring(0, attribute.indexOf(",")));
		int y = Integer.valueOf(attribute.substring(attribute.indexOf(",") + 1));
		to.appear(x, y);
		waitForNotification("AppearDone");
	}

	public void disappear(String attribute) throws InterruptedException {
		to.disappear();
		waitForNotification("DisappearDone");
	}

	public void flip(String attribute) {
		to.flip();
	}

	public void point(String attribute) {
		to.point();
	}

	public void jump(String attribute) {
		Log.i("catroid", "jumpint somewere");
	}

	public void goTo(String attribute) throws InterruptedException {
		Log.i("catroid", "going somewere");
		int x = Integer.valueOf(attribute.substring(0, attribute.indexOf(",")));
		int y = Integer.valueOf(attribute.substring(attribute.indexOf(",") + 1));

		to.jumpTo(x, y);
		waitForNotification("JumpDone");
	}

	public void waitFor(String attribute) throws InterruptedException {
		Log.i("catroid", "waitin for: " + attribute);
		waitForNotification(attribute);
		Log.i("catroid", "event happened, enough waited!...");

		//not implemented yet... this one could get a bit tricky
	}

	public void setNotification(String notification) {
		Log.i("catroid", "setting Notification " + notification);
		notifies.add(notification);
	}

	public void waitForNotification(String waitNotification) throws InterruptedException {
		Log.i("catroid", "waiting for: " + waitNotification);
		while (true) {
			for (int i = 0; i < notifies.size(); i++) {
				currentNotification = notifies.get(i);
				if (currentNotification.compareTo(waitNotification) == 0) {
					notifies.remove(i);
					Log.i("catroid", "waited enough!" + waitNotification);
					return;
				}
				synchronized (this) {
					wait(500); // hmm ??????
				}
			}
		}
	}

	public void say(String attribute) throws InterruptedException {
		Log.i("catroid", "saying something");
		to.say(attribute);
		waitForNotification("BubbleDone");
	}

	public void interpret() throws InterruptedException {
		Log.i("catroid", "interpreter up and running!");

		for (; instructionCounter < code[0].size(); instructionCounter++) {
			String opCode = (String) code[0].get(instructionCounter);
			String attribute = (String) code[1].get(instructionCounter);
			execute(opCode, attribute);
		}

	}

	public static Tutorial getInstance(Context con) {
		if (con != null) {
			context = con;
		}
		return tutorial;
	}

	private class TutorialThread extends Thread implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {
				continueTutorial();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}

	}

	private boolean startTutorial() {
		tutorialActive = true;
		Log.i("catroid", "starting tutorial...");
		//some code to start the tutorial
		//tutorialThread = new TutorialThread();
		resumeTutorial();
		return tutorialActive;
	}

	public void resumeTutorial() {
		if (!tutorialActive) {
			return; //Do nothing... there is nothing that can bee resumed... GREEN! bssssss			
		}
		dragViewParameters = createLayoutParameters();
		windowManager = ((Activity) context).getWindowManager();
		to = new TutorialOverlay(context, test);
		windowManager.addView(to, dragViewParameters);

		tutorialThread = new TutorialThread();
		tutorialThread.start();
	}

	public void saveActualState() {
		tutorialState.saveActualState(((Activity) context).getLocalClassName(), instructionCounter);
	}

	public void pauseTutorial() {
		if (!tutorialActive) {
			return;
		}
		saveActualState();
		instructionCounter = 0;
		tutorialThread.interrupt();
		if (to != null) {
			//to.clearScreen();
			//to.clearAnimation(); // net geholfen
			//to.destroyDrawingCache(); // net geholfen
			//to.forceLayout(); // nicht geholfen

			windowManager.removeView(to);
			to = null;

		}
	}

	private boolean stopTutorial() {
		tutorialState.saveActualState(((Activity) context).getLocalClassName(), instructionCounter);
		tutorialState.clear();
		tutorialActive = false;
		Log.i("catroid", "stopping tutorial...");
		//some code to remove the overlay
		if (to != null) {
			windowManager.removeView(to);
		}
		//some code to store actual TutorialState
		return tutorialActive;
	}

	public boolean toggleTutorial() {
		if (tutorialActive == false) {
			startTutorial();
		} else {
			stopTutorial();
		}
		return tutorialActive;
	}

	public void goToLastState() {
		instructionCounter = tutorialState.getLastState(((Activity) context).getLocalClassName());
	}

	private WindowManager.LayoutParams createLayoutParameters() {

		WindowManager.LayoutParams windowParameters = new WindowManager.LayoutParams();
		//windowParameters.gravity = Gravity.TOP | Gravity.LEFT;
		windowParameters.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

		windowParameters.height = Values.SCREEN_HEIGHT;
		windowParameters.width = Values.SCREEN_WIDTH;
		windowParameters.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| /* WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | */WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParameters.format = PixelFormat.TRANSLUCENT;

		return windowParameters;
	}

	public boolean continueTutorial() throws InterruptedException {
		if (!tutorialActive) {
			//Tutorial is not active, so there is nothing to continue...
		} else {
			//			goToLastState();
			txh = new TutorialXmlHandler(context);
			code = getCode();
			interpret();

		}

		return tutorialActive;
	}

}
