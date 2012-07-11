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
package at.tugraz.ist.catroid.physics;

import java.io.Serializable;
import java.util.ArrayList;

import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.math.Vector2;

public class PhysicWorld implements Serializable {
	private static final long serialVersionUID = -9103964560286141267L;

	ArrayList<Sprite> spriteList;
	private transient PhysicShapeBuilder physicShapeBuilder;

	public PhysicWorld() {
		spriteList = new ArrayList<Sprite>();
		physicShapeBuilder = new PhysicShapeBuilder();
		physicShapeBuilder.createSurroundingBox();
	}

	public void step(float deltaTime) {
		physicShapeBuilder.getWorld().step(PhysicWorldSetting.timeStep, PhysicWorldSetting.velocityIterations,
				PhysicWorldSetting.positionIterations);
		refreshSprites();
	}

	private void refreshSprites() {
		for (Sprite sprite : spriteList) {
			Vector2 catrobatCoords = physicShapeBuilder.getPosition(sprite);
			float angle = physicShapeBuilder.getAngle(sprite);
			angle = PhysicWorldConverter.angleBox2DToCat(angle);

			System.out.println("#### DEBUG : angle : " + angle);

			sprite.costume.aquireXYWidthHeightLock(); //   ?
			sprite.costume.setXYPosition(catrobatCoords.x, catrobatCoords.y);
			sprite.costume.rotation = angle;
			sprite.costume.releaseXYWidthHeightLock();
			physicShapeBuilder.turn(sprite);
			//System.out.println("#### DEBUG  x:" + sprite.costume.x + "  y:" + sprite.costume.y);
			//physicShapeBuilder.printStaticBodys();
			//System.out.println("#### DEBUG - ENDE  ");
		}
	}

	public void setGravity(Sprite sprite, Vector2 gravity) {
		physicShapeBuilder.setGravity(sprite, gravity);
	}

	public void setVelocity(Sprite sprite, Vector2 velocity) {
		boolean added = physicShapeBuilder.setVelocity(sprite, velocity);
		if (added) {
			spriteList.add(sprite);
		}
	}

	public void setMass(Sprite sprite, float mass) {
		boolean added = physicShapeBuilder.setMassData(sprite, mass);
		if (added) {
			spriteList.add(sprite);
		}
	}

	public void setPhysicShapeBuilderTestMock(PhysicShapeBuilder physShBTestMock) {
		physicShapeBuilder = physShBTestMock;
	}

}