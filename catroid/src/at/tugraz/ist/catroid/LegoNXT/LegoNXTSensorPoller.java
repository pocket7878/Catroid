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
 *    
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *		   	Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 *		   	This file is part of MINDdroid.
 *
 * 		  	MINDdroid is free software: you can redistribute it and/or modify
 * 		  	it under the terms of the GNU Affero General Public License as
 * 		  	published by the Free Software Foundation, either version 3 of the
 *   		License, or (at your option) any later version.
 *
 *   		MINDdroid is distributed in the hope that it will be useful,
 *   		but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   		GNU Affero General Public License for more details.
 *
 *   		You should have received a copy of the GNU Affero General Public License
 *   		along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.LegoNXT;

import java.util.ArrayList;

/**
 * This class is for talking to a LEGO NXT robot via bluetooth. The
 * communciation to the robot is done via LCP (LEGO communication protocol).
 * Objects of this class can either be run as standalone thread or controlled by
 * the owners, i.e. calling the send/recive methods by themselves.
 */
public class LegoNXTSensorPoller extends Thread {

	private ArrayList<LegoNXTSensor> sensorList;
	private boolean running = false;

	public LegoNXTSensorPoller() {
		running = true;
		sensorList = new ArrayList<LegoNXTSensor>();
	}

	public void stopPolling() {
		running = false;
	}

	public void addSensor(LegoNXTSensor sensor) {
		sensorList.add(sensor);
	}

	@Override
	public void run() {
		while (running) {
			for (LegoNXTSensor item : sensorList) {
				LegoNXT.sendBTCTestMessage(item.getSensor());
			}
			try {
				sleep(200);
				LegoNXTCommunicator.getReceivedSensorMessageList();

				LegoNXTCommunicator.clearSensorMessageList();
			} catch (InterruptedException e) {

			}
		}

	}
}