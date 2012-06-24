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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

/**
 * @author faxxe
 * 
 */
public class Tutor extends SurfaceObjectTutor implements SurfaceObject {

	public enum ACTIONS {
		REWIND, FORWARD
	}

	private Context context;
	private TutorialOverlay tutorialOverlay;
	private Resources ressources;
	private Bitmap bitmap;
	private int state = -1;
	private int currentStep = 0;
	private Paint paint;
	private int sizeX = 110;
	private int sizeY = 102;
	private Bubble tutorBubble;

	/*
	 * State + State Variables
	 */
	private int targetX;
	private int targetY;
	private boolean flip = false;

	private TutorState currentState;
	private TutorStateHistory tutorStateHistory;

	private int flipFlag = 2;
	private boolean reset = true;

	private boolean walkFast = false;
	private int walkToX;
	private int walkToY;
	private boolean directionX;
	private boolean directionY;
	private int distanceX;
	private int distanceY;
	private int factorX;
	private int factorY;

	private long lastUpdateTime = 0;
	private int updateTime = 150;

	private boolean holdTutor = false;

	//	public Tutor(int drawable, TutorialOverlay tutorialOverlay) {
	//		super(Tutorial.getInstance(null).getActualContext(), tutorialOverlay);
	//
	//		context = Tutorial.getInstance(null).getActualContext();
	//		ressources = context.getResources();
	//		BitmapFactory.Options options = new BitmapFactory.Options();
	//		options.inScaled = false;
	//		bitmap = BitmapFactory.decodeResource(ressources, drawable, options);
	//		paint = new Paint();
	//		this.tutorialOverlay = tutorialOverlay;
	//	}

	public Tutor(int drawable, TutorialOverlay tutorialOverlay, int x, int y, Task.Tutor tutorType) {
		super(Tutorial.getInstance(null).getActualContext(), tutorialOverlay);
		context = Tutorial.getInstance(null).getActualContext();
		ressources = context.getResources();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		bitmap = BitmapFactory.decodeResource(ressources, drawable, options);
		paint = new Paint();
		this.tutorialOverlay = tutorialOverlay;

		this.targetX = x;
		this.targetY = y;
		super.tutorType = tutorType;

		tutorStateHistory = new TutorStateHistory(tutorType);
		currentState = new TutorState(targetX, targetY, flip, state);
		tutorStateHistory.addStateToHistory(currentState);

	}

	@Override
	public void flip(boolean flipFast) {
		currentState = new TutorState(targetX, targetY, flip, state);
		tutorStateHistory.addStateToHistory(currentState);

		Log.i("drab", Thread.currentThread().getName() + ": State set for Flipping");

		if (flip) {
			flip = false;
		} else {
			flip = true;
		}

		if (flipFast) {
			flipFlag = 0;
		}
		state = 60;
	}

	@Override
	public void walk(int walkX, int walkY, boolean fastWalk) {
		currentState = new TutorState(targetX, targetY, flip, state);
		tutorStateHistory.addStateToHistory(currentState);

		Log.i("drab", Thread.currentThread().getName() + ": State set for Walking");

		walkFast = fastWalk;
		walkToX = walkX;
		walkToY = walkY;

		if (walkToX > targetX) {
			directionX = true;
			distanceX = walkToX - targetX;
		} else {
			directionX = false;
			distanceX = targetX - walkToX;
		}

		if (walkToY > targetY) {
			directionY = true;
			distanceY = walkToY - targetY;
		} else {
			directionY = false;
			distanceY = targetY - walkToY;
		}

		if (distanceX > distanceY && distanceY > 0) {
			factorY = Math.round((distanceX / distanceY) + 0.5f);
			factorX = 1;
		} else if (distanceX < distanceY && distanceX > 0) {
			factorX = Math.round((distanceY / distanceX) + 0.5f);
			factorY = 1;
		} else {
			factorX = 1;
			factorY = 1;
		}

		if (!flip) {
			state = 8;
		} else {
			state = 9;
		}
	}

	@Override
	public void idle() {
		if (!flip) {
			state = 1;
		} else {
			state = 5;
		}
	}

	@Override
	public void sleep() {
		currentState = new TutorState(targetX, targetY, flip, state);
		tutorStateHistory.addStateToHistory(currentState);

		Log.i("drab", Thread.currentThread().getName() + ": State set for sleep");
		if (!flip) {
			state = 1;
		} else {
			state = 5;
		}
	}

	@Override
	public void say(String text) {
		currentState = new TutorState(targetX, targetY, flip, state);
		tutorStateHistory.addStateToHistory(currentState);

		Log.i("drab", Thread.currentThread().getName() + ": State set for Saying");
		tutorBubble = new Bubble(text, tutorialOverlay, this, targetX, targetY);

		if (!flip) {
			state = 2;
		} else {
			state = 6;
		}
	}

	@Override
	public void jumpTo(int x, int y) {
		currentState = new TutorState(targetX, targetY, flip, state);
		tutorStateHistory.addStateToHistory(currentState);

		Log.i("drab", Thread.currentThread().getName() + ": State set for Jumping");
		targetX = x;
		targetY = y;
		state = 61;
	}

	@Override
	public void appear(int x, int y) {
		currentState = new TutorState(targetX, targetY, flip, state);
		tutorStateHistory.addStateToHistory(currentState);
		targetX = x;
		targetY = y;
		Log.i("drab", Thread.currentThread().getName() + ": State set for appear");

		if (!flip) {
			state = 0;
		} else {
			state = 4;
		}
	}

	@Override
	public void disappear() {
		currentState = new TutorState(targetX, targetY, flip, state);
		tutorStateHistory.addStateToHistory(currentState);

		Log.i("drab", Thread.currentThread().getName() + ": State set for disappearing");

		if (!flip) {
			state = 3;
		} else {
			state = 7;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		Bitmap todraw = Bitmap.createBitmap(bitmap, 0, 0, sizeX, sizeY);
		if (currentStep > 9) {
			currentStep = 0;
			reset = true;
		}

		//Log.i("drab", Thread.currentThread().getName() + ": TutorDraw()");

		if (!holdTutor) {
			switch (state) {
				case 0:
				case 4: //APPEARING
					if (currentStep == 9) {
						state = 1;
						Tutorial.getInstance(null).setNotification("appear done!");
						break;
					}
					todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * sizeY, sizeX, sizeY);
					break;

				case 1:
				case 5: //IDLE
					if (currentStep == 9) {
						currentStep = 0;
					}
					todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * sizeY, sizeX, sizeY);
					break;

				case 2:
				case 6: //SAYING
					todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * sizeY, sizeX, sizeY);
					break;

				case 3:
				case 7: //DISAPPEARING
					if (currentStep == 9) {
						state = 100;
						Tutorial.getInstance(null).setNotification("disappear done!");
						break;
					}
					todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * sizeY, sizeX, sizeY);
					break;

				case 8:
				case 9: //WALK
					if (walkToX == targetX && walkToY == targetY) {
						if (flip) {
							state = 5;
						} else {
							state = 1;
						}
						todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * sizeY, sizeX, sizeY);
						Tutorial.getInstance(null).setNotification("walk done!");
					} else {
						if (walkFast || (currentStep % 2) == 0) {
							if (directionX && targetX != walkToX && distanceX % factorX == 0) {
								targetX++;
							} else if (!directionX && targetX != walkToX && distanceX % factorX == 0) {
								targetX--;
							}

							if (directionY && targetY != walkToY && distanceY % factorY == 0) {
								targetY++;
							} else if (!directionY && targetY != walkToY && distanceY % factorY == 0) {
								targetY--;
							}
							distanceX--;
							distanceY--;
						}
						todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * sizeY, sizeX, sizeY);
					}

					break;

				case 60: //FLIP
					if (flipFlag > 0) {
						if (flip && reset) {
							if (flipFlag == 2) {
								todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, 306, sizeX, sizeY);
							} else {
								todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, 408, sizeX, sizeY);
							}
						} else if (reset) {
							if (flipFlag == 2) {
								todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, 714, sizeX, sizeY);
							} else {
								todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, 0, sizeX, sizeY);
							}
						}
						//						Log.i("drab", "START of FLIP | currentStep: " + currentStep + " state: " + state
						//								+ " flipFlag: " + flipFlag + " flip: " + flip);
						if (currentStep == 9 && reset) {
							flipFlag--;
							reset = false;
						}
					} else {
						if (flip) {
							state = 5;
						} else {
							state = 1;
						}
						flipFlag = 2;
						todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * sizeY, sizeX, sizeY);
						Tutorial.getInstance(null).setNotification("flip done!");
					}
					//					Log.i("drab", "END of FLIP | currentStep: " + currentStep + " state: " + state + " flipFlag: "
					//							+ flipFlag);
					break;

				case 61: //JUMP
					if (flip) {
						state = 5;
					} else {
						state = 1;
					}
					Tutorial.getInstance(null).setNotification("Jump done!");
					break;

				default:
					return;
			}
		}
		canvas.drawBitmap(todraw, targetX, targetY, paint);
	}

	public void setHoldTutor(boolean holdTutor) {
		this.holdTutor = holdTutor;
	}

	@Override
	public void update(long gameTime) {
		if ((lastUpdateTime + updateTime) < gameTime && !holdTutor) {
			lastUpdateTime = gameTime;
			currentStep++;
		}
	}

	public void register(TutorialOverlay overlay) {
		overlay.addSurfaceObject(this);
	}

	@Override
	public void setInterruptOfSequence(ACTIONS action) {

		if (tutorBubble != null) {
			Tutorial.getInstance(null).setNotification("Bubble finished!");
			tutorBubble.interruptAndClear();
			tutorBubble = null;
			Log.i("drab", Thread.currentThread().getName() + ": Bubble deleted for " + this.tutorType);
		}

		TutorState newState;

		if (action == ACTIONS.REWIND) {
			newState = tutorStateHistory.setBackAndReturnState();
		} else {
			newState = tutorStateHistory.setBackAndReturnState();
		}
		Log.i("drab", Thread.currentThread().getName() + ": NEW-STATE retrieved for " + this.tutorType + ": X="
				+ newState.getX() + " Y=" + newState.getY() + " state=" + newState.getState());

		targetX = newState.getX();
		targetX = newState.getX();
		flip = newState.isFlip();
		state = newState.getState();
		currentStep = 0;

		Log.i("drab", Thread.currentThread().getName() + ": New state is SET and Steps reseted for " + this.tutorType);
	}
}
