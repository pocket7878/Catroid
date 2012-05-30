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

import android.util.Log;

/**
 * @author faxxe
 * 
 */
public class TutorialThread extends Thread implements Runnable {
	private LessonCollection lessonCollection;
	public boolean tutorialThreadRunning = true;
	private boolean threadWait = true;
	private volatile ArrayList<String> notifies = new ArrayList<String>();
	private String currentNotification;

	public TutorialThread() {
		Thread thisThread = new Thread(this);
		thisThread.setName("NewTutorialThread");
		Log.i("faxxe", "New TutorialThread started... ");
	}

	@Override
	public void run() {
		runTutorial();
	}

	public void startThread() {
		tutorialThreadRunning = true;
		this.start();
	}

	public void stopThread() {
		tutorialThreadRunning = false;
		boolean retry = true;
		while (retry) {
			try {
				synchronized (this) {
					this.notifyAll();
				}
				this.join(1);
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}
	}

	public void notifyThread() {
		doNotify();
	}

	private void doNotify() {
		Log.i("faxxe", "TutorialThread: notifying!");
		synchronized (this) {
			this.notify();
		}
	}

	public void waitThread() {
		threadWait = true;
	}

	private void runTutorial() {
		do {
			boolean notification = lessonCollection.executeTask();
			if (notification == true) {
				synchronized (this) {
					try {
						Log.i("faxxe", " waiting for notification");
						wait();
					} catch (InterruptedException e) {
						Log.i("faxxe", "TutorialThread: wait() failed!");
						e.printStackTrace();
					}
				}
			}
		} while (lessonCollection.forwardStep() && tutorialThreadRunning);

		if (!lessonCollection.forwardStep()) {
			lessonCollection.resetCurrentLesson();
		}

		if (tutorialThreadRunning) {
			lessonCollection.nextLesson();
			Tutorial.getInstance(null).stopTutorial();
		}
		return;
	}

	public void stopTutorial() {
		//TODO: stop tutorial here :)
	}

	public void setLessonCollection(LessonCollection lessonCollection) {
		this.lessonCollection = lessonCollection;
	}

	public void setNotification(String notification) {
		notifies.add(notification);
	}

}
