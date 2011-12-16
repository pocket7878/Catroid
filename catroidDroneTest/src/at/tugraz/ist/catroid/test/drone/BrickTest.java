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
package at.tugraz.ist.catroid.test.drone;

import org.easymock.EasyMock;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;
import at.tugraz.ist.catroid.plugin.Drone.IDrone;
import at.tugraz.ist.catroid.plugin.Drone.bricks.DroneTakeOffBrick;


public class BrickTest extends InstrumentationTestCase {

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
	}
	

	public void testTakeOffBrick() {
	
		DroneHandler dronehandler = DroneHandler.getInstance();
		IDrone idronemock = EasyMock.createMock(IDrone.class);
		dronehandler.setIDrone(idronemock);
		DroneTakeOffBrick takeoffbrick  = new DroneTakeOffBrick(null);
		EasyMock.expect(idronemock.connect());
//		EasyMock.expect(idronemock.takeoff());
		takeoffbrick.execute();
		
	}
}
