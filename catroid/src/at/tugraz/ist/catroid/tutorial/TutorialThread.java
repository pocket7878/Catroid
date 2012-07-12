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

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import at.tugraz.ist.catroid.tutorial.Tutor.ACTIONS;
import at.tugraz.ist.catroid.tutorial.tasks.Task;
import at.tugraz.ist.catroid.tutorial.tasks.Task.Type;

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

	private SparseArray<Task.Type> notificationTasksList = new SparseArray<Task.Type>();
	private int lastNotifyTasksIndex = 0;

	private HashMap<Task.Tutor, SurfaceObjectTutor> tutors = new HashMap<Task.Tutor, SurfaceObjectTutor>();

	public TutorialThread(HashMap<Task.Tutor, SurfaceObjectTutor> tutors, Context context) {
		Thread thisThread = new Thread(this);
		thisThread.setName("TutorialThread");
		this.tutors = tutors;
		Log.i("tutorial", Thread.currentThread().getName() + ": New TutorialThread started... ");
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
			}
		}
	}

	public void notifyThread() {
		doNotify();
	}

	private void doNotify() {
		Log.i("tutorial", Thread.currentThread().getName() + ": notified");
		synchronized (this) {
			this.notify();
		}
	}

	public void waitThread() {
	}

	private void runTutorial() {
		while (tutorialThreadRunning) {
			Task.Type currentTaskType = lessonCollection.getTypeFromCurrentTaskInLesson();

			if (!interrupted) {
				boolean notification = lessonCollection.executeTask();

				synchronized (lastModifiedTutorList) {
					lastModifiedTutorList.add(lastModifiedTutorIndex,
							lessonCollection.getTutorNameFromCurrentTaskInLesson());
					lastModifiedTutorIndex++;
				}

				if (currentTaskType == Task.Type.NOTIFICATION) {
					Type notificationType = lessonCollection.getCurrentTaskObject().getType();
					notificationTasksList.put(lastNotifyTasksIndex, notificationType);
					lastNotifyTasksIndex++;
				}

				if (notification == true) {
					synchronized (this) {
						try {
							Log.i("tutorial", Thread.currentThread().getName() + ": waiting for notification");
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}

			if (interrupted) {
				if (interruptRoutine == ACTIONS.REWIND) {
					//TODO: needs to be implemented for every Notification 
					//Like: TaskNotification.handleRewindNotification()
					//For every NotificationType
					if (currentTaskType == Task.Type.NOTIFICATION) {
						lastNotifyTasksIndex--;
						CloudController co = new CloudController();
						co.disapear();
						lastNotifyTasksIndex--;
					}

					int stepsBack = lessonCollection.rewindStep();

					if (stepsBack == 0) {
						resetAndGoToPreviousLesson();
					} else {
						setLastTutorModified(stepsBack);
					}
					Log.i("tutorial", "STEPS Back: " + stepsBack);

				} else if (interruptRoutine == ACTIONS.FORWARD) {

					Task currentTask = lessonCollection.getCurrentTaskObject();
					if (lessonCollection.getTypeFromCurrentTaskInLesson() == Task.Type.FLIP
							|| lessonCollection.getTypeFromCurrentTaskInLesson() == Task.Type.WALK) {

						currentTask.setEndPositionOfTaskForTutor(tutors);

					}

					boolean nextStepOfLesson = lessonCollection.forwardStep();

					if (!nextStepOfLesson && resetAndCheckIfEndTutorial()) {
						break;
					}
				}

				while (interrupted) {
					synchronized (this) {
						try {
							iAck = true;
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				iAck = false;
			} else {
				boolean nextStepOfLesson = lessonCollection.forwardStep();

				if (!nextStepOfLesson && resetAndCheckIfEndTutorial()) {
					break;
				}
			}
		}
		return;
	}

	public void setLastTutorModified(int stepsBack) {
		if (stepsBack == 0) {
			stepsBack++;
		}

		synchronized (lastModifiedTutorList) {
			boolean notifyTutorForExtraStep = false;
			SurfaceObjectTutor lastTutor = null;
			Task.Tutor tutor1 = null;
			Task.Tutor tutor2 = null;

			decrementLastTutorModifiedIndex();

			for (int i = 0; i < stepsBack; i++) {
				tutor1 = lastModifiedTutorList.get(lastModifiedTutorIndex);
				decrementLastTutorModifiedIndex();
				tutor2 = lastModifiedTutorList.get(lastModifiedTutorIndex);

				if (tutor1 == tutor2) {
					if (tutor1 != null) {
						this.tutors.get(tutor1).setBackStepForTutor();
						lastTutor = this.tutors.get(tutor1);
					}
				} else {
					if (tutor1 != null) {
						this.tutors.get(tutor1).setBackStepForTutor();
					}
					if (tutor2 != null) {
						notifyTutorForExtraStep = true;
						this.tutors.get(tutor2).setBackStepForTutor();
						lastTutor = this.tutors.get(tutor2);
					}
				}
			}

			if (notifyTutorForExtraStep) {
				lastTutor.setExtraStepInStateHistory();
			}
		}
	}

	public boolean decrementLastTutorModifiedIndex() {
		synchronized (lastModifiedTutorList) {
			if (lastModifiedTutorIndex > 0) {
				lastModifiedTutorIndex--;
				Log.i("tutorial", "lastModiefiedIndex is decremented to: " + lastModifiedTutorIndex);
				return true;
			}
			Log.i("tutorial", "lastModiefiedIndex not decremented: " + lastModifiedTutorIndex);
			return false;
		}
	}

	public boolean getAck() {
		return iAck;
	}

	public boolean resetAndCheckIfEndTutorial() {
		Log.i("tutorial", "Tutorial stopped in TUT-Thread");
		lessonCollection.resetCurrentLesson();
		tutors.get(Task.Tutor.CATRO).resetTutor();
		tutors.get(Task.Tutor.MIAUS).resetTutor();
		lastModifiedTutorIndex = 0;
		lastModifiedTutorList.clear();

		boolean nextLesson = lessonCollection.nextLesson();

		if (tutorialThreadRunning && !nextLesson) {
			Log.i("tutorial", "STOP Tutorial");
			Tutorial.getInstance(null).stopButtonTutorial();
			return true;
		}

		return false;
	}

	public boolean resetAndGoToPreviousLesson() {
		Log.i("tutorial", "Tutorial stopped in TUT-Thread");
		lessonCollection.resetCurrentLesson();
		tutors.get(Task.Tutor.CATRO).resetTutor();
		tutors.get(Task.Tutor.MIAUS).resetTutor();
		lastModifiedTutorIndex = 0;
		lastModifiedTutorList.clear();

		boolean previousLesson = lessonCollection.previousLesson();

		if (tutorialThreadRunning && previousLesson) {
			Log.i("tutorial", "Lesson changed to Previous lesson");
			return true;
		}
		return false;
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
}
