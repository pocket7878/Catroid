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
import android.graphics.Canvas;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

/**
 * @author faxxe
 * 
 * 
 */

public abstract class SurfaceObjectTutor implements SurfaceObject {

	public Task.Tutor tutorType;
	private TutorialOverlay tutorialOverlay;
	private Context context;

	//	public SurfaceObjectTutor(NewTutorialOverlay tutorialOverlay) {
	//		this.tutorialOverlay = tutorialOverlay;
	//		tutorialOverlay.addSurfaceObject(this);
	//	}

	public SurfaceObjectTutor(Context context, TutorialOverlay tutorialOverlay) {
		this.context = context;
		this.tutorialOverlay = tutorialOverlay;
		//		tutorialOverlay.addSurfaceObject(this);
	}

	@Override
	protected void finalize() throws Throwable {
		tutorialOverlay.removeSurfaceObject(this);
		super.finalize(); //not necessary if extending Object.
	}

	@Override
	public abstract void draw(Canvas canvas);

	@Override
	public abstract void update(long gameTime);

	public abstract void flip();

	public abstract void idle();

	public abstract void say(String text);

	public abstract void jumpTo(int x, int y);

	public abstract void appear(int x, int y);

	public abstract void disappear();

	public abstract void point();

	public abstract void setNewPositionAfterPort();

}
