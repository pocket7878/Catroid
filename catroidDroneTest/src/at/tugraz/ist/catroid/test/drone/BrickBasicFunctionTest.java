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
import android.util.Log;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;
import at.tugraz.ist.catroid.plugin.Drone.IDrone;
import at.tugraz.ist.catroid.plugin.Drone.bricks.DroneChangeFlyingModeBrick;
import at.tugraz.ist.catroid.plugin.Drone.bricks.DroneConfigBrick;
import at.tugraz.ist.catroid.plugin.Drone.bricks.DroneLandBrick;
import at.tugraz.ist.catroid.plugin.Drone.bricks.DroneLedAnimationBrick;
import at.tugraz.ist.catroid.plugin.Drone.bricks.DroneTakeOffBrick;

import static org.easymock.EasyMock.*;

//import org.junit.*;

public class BrickBasicFunctionTest extends InstrumentationTestCase {

	IDrone idronemock;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createDroneMock();
	}

	private void verifytest(Brick brick) {
		Log.d(this.getName(), "verifytest()");
		EasyMock.expectLastCall().times(1);
		replay(idronemock);
		brick.execute();
		verify(idronemock);
		createDroneMock();
	}

	private void createDroneMock() {
		idronemock = null;
		idronemock = EasyMock.createMock(IDrone.class);
		DroneHandler.getInstance().setIDrone(idronemock);
	}

	public void testDroneTakeOffBrick() {
		DroneTakeOffBrick takeoffbrick = new DroneTakeOffBrick(null);
		idronemock.takeoff();
		verifytest((Brick) takeoffbrick);
	}

	public void testDroneLandBrick() {
		DroneLandBrick landbrick = new DroneLandBrick(null);
		idronemock.land();
		
	}

	public void testDroneChangeFlyingModeBrick() {
		//TODO change int
		DroneChangeFlyingModeBrick brick = new DroneChangeFlyingModeBrick(null,1);
		expect(idronemock.changeFlyingMode(1)).andReturn(true);
		verifytest((Brick) brick);
	}

	public void testDroneConfigBrick() {
		//TODO change int
		DroneConfigBrick landbrick = new DroneConfigBrick(null, 0,0);
		expect(idronemock.setConfiguration("AT*CONFIG=#SEQ#," + "\"control:altitude_max\",\"1000\"", true)).andReturn(true);
		verifytest((Brick) landbrick);
	}

	public void testDroneLedAnimationBrick() {
		DroneLedAnimationBrick brick = new DroneLedAnimationBrick(null,1,1,1);
		idronemock.playLedAnimation(1, 1, 1);
		verifytest((Brick) brick);
	}

	public void testDroneMoveBrick() {
		DroneLedAnimationBrick brick = new DroneLedAnimationBrick(null,1,1,1);
		idronemock.playLedAnimation(1, 1, 1);
		verifytest((Brick) brick);
	}

	public void testDroneSaveSnapshotBrick() {

	}

	public void testDroneStopMoveBrick() {

	}

	public void testDroneStartVideoRecorderBrick() {

	}

	public void testDroneStartVideoBrick() {

	}

	public void testDroneStopVideoBrick() {

	}

	// public void test(){
	//
	// }

}
