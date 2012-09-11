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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicObjectMap implements Iterable<Entry<Sprite, PhysicObject>> {
	private final transient World world;
	private final transient Map<Sprite, PhysicObject> objects = new HashMap<Sprite, PhysicObject>();

	public PhysicObjectMap(World world) {
		this.world = world;
	}

	public PhysicObject get(Sprite sprite) {
		PhysicObject physicObject = objects.get(sprite);
		if (physicObject == null) {
			BodyDef bodyDef = new BodyDef();

			physicObject = new PhysicObject(world.createBody(bodyDef));
			objects.put(sprite, physicObject);
		}

		return physicObject;
	}

	public boolean contains(Sprite sprite) {
		return objects.containsKey(sprite);
	}

	@Override
	public Iterator<Entry<Sprite, PhysicObject>> iterator() {
		return objects.entrySet().iterator();
	}
}
