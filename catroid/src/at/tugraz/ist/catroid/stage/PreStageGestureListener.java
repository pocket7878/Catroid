/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.stage;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PreStageGestureListener implements GestureListener {

	private Actor actorToChange = null;
	private float startScaleX;
	private float startScaleY;
	private float startOriginalDistance = -1;

	private float startRotation;
	private Vector2 startInitialFirstPointer;

	public void setActorToChange(Actor actorToChange) {
		this.actorToChange = actorToChange;
	}

	public boolean touchDown(int x, int y, int pointer) {
		return false;
	}

	public boolean tap(int x, int y, int count) {
		return false;
	}

	public boolean longPress(int x, int y) {
		return false;
	}

	public boolean fling(float velocityX, float velocityY) {
		return false;
	}

	public boolean pan(int x, int y, int deltaX, int deltaY) {
		if (actorToChange != null && false) {
			actorToChange.x += deltaX;
			actorToChange.y -= deltaY;
			return true;
		}
		return false;
	}

	public boolean zoom(float originalDistance, float currentDistance) {
		if (actorToChange != null && false) {
			//Gdx.app.log("PreStage", actorToChange.scaleX + "% " + originalDistance + " " + currentDistance);
			if (startOriginalDistance != originalDistance) {
				startOriginalDistance = originalDistance;
				this.startScaleX = actorToChange.scaleX;
				this.startScaleY = actorToChange.scaleY;
			}
			//actorToChange.scaleX = startScaleX - (originalDistance - currentDistance) / originalDistance;
			//actorToChange.scaleY = startScaleY - (originalDistance - currentDistance) / originalDistance;
			actorToChange.scaleX = startScaleX / originalDistance * currentDistance;
			actorToChange.scaleY = startScaleY / originalDistance * currentDistance;
			if (actorToChange.scaleX < 0.01F) {
				actorToChange.scaleX = 0.01F;
			}
			if (actorToChange.scaleY < 0.01F) {
				actorToChange.scaleY = 0.01F;
			}
			//actorToChange.scaleX = startScaleX - currentDistance / originalDistance;
			//actorToChange.scaleY = startScaleY - currentDistance / originalDistance;
			return true;
		}
		return false;
	}

	public boolean pinch(Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer,
			Vector2 secondPointer) {
		if (actorToChange != null) {
			Vector2 vec1 = initialSecondPointer.sub(initialFirstPointer);
			Vector2 vec2 = secondPointer.sub(firstPointer);
			float angle = vec1.angle() - vec2.angle();
			//Gdx.app.log("PreStage", initialFirstPointer + " " + initialSecondPointer + " " + firstPointer + " "
			//		+ secondPointer);
			if (startInitialFirstPointer == initialFirstPointer) {
				startRotation = actorToChange.rotation;
				startInitialFirstPointer = initialFirstPointer;
			}
			actorToChange.rotation = startRotation + angle;
			actorToChange.rotation = actorToChange.rotation % 360F;
			return true;
		}
		return false;
	}

}
