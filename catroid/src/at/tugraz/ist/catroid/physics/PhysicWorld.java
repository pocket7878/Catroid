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
import java.util.HashMap;
import java.util.Map;

import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicWorld implements Serializable {
	private static final long serialVersionUID = -9103964560286141267L;
	static {
		GdxNativesLoader.load();
	}

	private transient World world;
	private transient Map<Sprite, Body> bodys;
	private transient PhysicShapeBuilder physicShapeBuilder;

	public PhysicWorld() {
		world = new World(PhysicWorldSetting.defaultgravity, PhysicWorldSetting.ignoreSleepingObjects);
		bodys = new HashMap<Sprite, Body>();
		physicShapeBuilder = new PhysicShapeBuilder(world);
		physicShapeBuilder.createSurroundingBox();
	}

	public void step(float deltaTime) {
		world.step(PhysicWorldSetting.timeStep, PhysicWorldSetting.velocityIterations,
				PhysicWorldSetting.positionIterations);
		refreshSprites();
	}

	private void refreshSprites() {
		for (Sprite sprite : bodys.keySet()) {
			Body body = bodys.get(sprite);
			Vector2 catrobatCoords = PhysicWorldConverter.Vector2FromBox2DToCatroid(body.getPosition());
			//float angle = body.getAngle();

			sprite.costume.aquireXYWidthHeightLock(); // Sinn ?
			sprite.costume.setXYPosition(catrobatCoords.x, catrobatCoords.y);
			//sprite.costume.rotation = angle;
			sprite.costume.releaseXYWidthHeightLock();

			System.out.println("#### DEBUG  x:" + sprite.costume.x + "  y:" + sprite.costume.y);
			physicShapeBuilder.printStaticBodys();
			System.out.println("#### DEBUG - ENDE  ");
		}
	}

	// #############################################################################
	// # PHYSIC-BRICKS-INTERACTION
	// #############################################################################

	/**
	 * @param sprite
	 * @param gravity
	 */
	public void setGravity(Sprite sprite, Vector2 gravity) {
		world.setGravity(PhysicWorldConverter.Vector2FromCatroidToBox2D(gravity));
	}

	/**
	 * @param sprite
	 * @param velocity
	 */
	public void setVelocity(Sprite sprite, Vector2 velocity) {
		Body body = getBody(sprite);
		body.applyLinearImpulse(PhysicWorldConverter.Vector2FromCatroidToBox2D(velocity), body.getPosition());
	}

	/**
	 * @param sprite
	 * @param mass
	 */
	public void setMass(Sprite sprite, float mass) {
		Body body = getBody(sprite);
		MassData mdata = new MassData();
		mdata.mass = mass;
		body.setMassData(mdata);
	}

	private Body getBody(Sprite sprite) {
		Body body = bodys.get(sprite);
		if (null == body) {
			body = physicShapeBuilder.createBody(sprite);
			bodys.put(sprite, body);
		}
		return body;
	}

}