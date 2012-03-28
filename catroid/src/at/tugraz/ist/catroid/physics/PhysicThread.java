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

public class PhysicThread extends Thread {
	private final PhysicWorld world;
	private boolean active = false;
	private boolean running = false;

	public PhysicThread(PhysicWorld world) {
		this.world = world;
	}

	@Override
	public synchronized void start() {
		active = true;
		running = true;
		super.start();
	}

	public void pause() {
		running = false;
	}

	public void resumE() {
		running = true;
	}

	public void finish() {
		active = false;
		running = false;
	}

	@Override
	public void run() {
		while (active) {
			if (running) {
				world.step();
			} else {
				Thread.yield();
			}
		}
	}
}
