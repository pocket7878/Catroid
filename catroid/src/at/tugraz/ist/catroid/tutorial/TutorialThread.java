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

import android.util.Log;
import at.tugraz.ist.catroid.tutorial.Tutor.ACTIONS;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

/**
 * @author faxxe
 * 
 */
public class TutorialThread extends Thread implements Runnable {
	private LessonCollection lessonCollection;
	public boolean tutorialThreadRunning = true;
	private volatile ArrayList<String> notifies = new ArrayList<String>();
	private boolean interrupted = false;
	private ACTIONS interruptRoutine;
	private boolean iAck = false;
	private ArrayList<Task.Tutor> lastModifiedTutorList = new ArrayList<Task.Tutor>();
	private int lastModifiedTutorIndex = 0;
	private HashMap<Task.Tutor, SurfaceObjectTutor> tutors = new HashMap<Task.Tutor, SurfaceObjectTutor>();

	public TutorialThread(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		Thread thisThread = new Thread(this);
		thisThread.setName("NewTutorialThread");
		this.tutors = tutors;
		Log.i("drab", Thread.currentThread().getName() + ": New TutorialThread started... ");
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
		Log.i("drab", Thread.currentThread().getName() + ": notified");
		synchronized (this) {
			this.notify();
		}
	}

	public void waitThread() {
	}

	private void runTutorial() {
		while (tutorialThreadRunning
				&& lessonCollection.currentStepOfLesson() < lessonCollection.getSizeOfCurrentLesson()) {
			if (!interrupted) {
				boolean notification = lessonCollection.executeTask();

				synchronized (lastModifiedTutorList) {

					//					Log.i("new",
					//							"Adding on " + lastModifiedTutorIndex + " Tutor "
					//									+ lessonCollection.getNameFromCurrentTaskInLesson());
					lastModifiedTutorList
							.add(lastModifiedTutorIndex, lessonCollection.getNameFromCurrentTaskInLesson());

					//					Log.i("new", "\n");
					//					Log.i("new", "#######LastModifiedList#######");
					//					for (int i = 0; i <= lastModifiedTutorIndex; i++) {
					//						Log.i("new", i + ")  " + lastModifiedTutorList.get(i));
					//					}
					//					Log.i("new", "##############################");
					//					Log.i("new", "\n");
					lastModifiedTutorIndex++;
				}

				if (notification == true) {
					synchronized (this) {
						try {
							Log.i("drab", Thread.currentThread().getName() + ": waiting for notification");
							wait();
						} catch (InterruptedException e) {
							Log.i("drab", Thread.currentThread().getName() + ": TutorialThread: wait() failed!");
							e.printStackTrace();
						}
					}
				}
			}
			if (!interrupted) {
				lessonCollection.forwardStep();
			}

			if (interrupted) {
				if (interruptRoutine == ACTIONS.REWIND) {
					setLastTutorModified();
				}
				while (interrupted) {
					synchronized (this) {
						try {
							iAck = true;
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				if (interruptRoutine == ACTIONS.REWIND) {
					lessonCollection.rewindStep();
				}
				iAck = false;
			}
		}

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

	public void setInterrupt(boolean flag) {
		interrupted = flag;
	}

	public void setInterruptRoutine(ACTIONS action) {
		interruptRoutine = action;
	}

	public void setLastTutorModified() {
		synchronized (lastModifiedTutorList) {
			boolean flag1 = decrementLastTutorModifiedIndex();
			Task.Tutor tutor1 = lastModifiedTutorList.get(lastModifiedTutorIndex);
			boolean flag2 = decrementLastTutorModifiedIndex();
			Task.Tutor tutor2 = lastModifiedTutorList.get(lastModifiedTutorIndex);

			if (tutor1 == tutor2) {
				//				if (flag1 && flag2) {
				//					this.tutors.get(tutor1).setBackTutor(1);
				//					Log.i("new", "Setting back 2 steps of " + tutor1 + " tutor");
				//				} else {
				this.tutors.get(tutor1).setBackTutor(1);
				Log.i("new", "Setting back 1 step of " + tutor1 + " tutor");
				//				}
			} else {
				this.tutors.get(tutor1).setBackTutor(1);
				this.tutors.get(tutor2).setBackTutor(1);
				Log.i("new", "Setting back 1 step of both tutors");
			}
		}
	}

	public boolean decrementLastTutorModifiedIndex() {
		synchronized (lastModifiedTutorList) {
			if (lastModifiedTutorIndex > 0) {
				lastModifiedTutorIndex--;
				//Log.i("new", "lastModiefiedIndex is decremented to: " + lastModifiedTutorIndex);
				return true;
			}
			//Log.i("new", "lastModiefiedIndex not decremented: " + lastModifiedTutorIndex);
			return false;
		}
	}

	public boolean getAck() {
		return iAck;
	}
}
