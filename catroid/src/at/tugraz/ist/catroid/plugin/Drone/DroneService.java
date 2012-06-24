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
package at.tugraz.ist.catroid.plugin.Drone;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import at.tugraz.ist.droned.client.CatroidDrone;

public class DroneService extends Service {

	// Binder given to clients
	private final IBinder mBinder = new LocalDroneServiceBinder();
	// Random number generator
	private final Random mGenerator = new Random();

	private CatroidDrone catroidDrone;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Toast.makeText(getApplicationContext(), "OnServiceCreate - DroneService created", 1000);
		//		try {
		//			drone = new DroneLibraryWrapper(getApplicationContext());
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		catroidDrone = new CatroidDrone();

	}

	public CatroidDrone getCatroidDrone() {
		return this.catroidDrone;
	}

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalDroneServiceBinder extends Binder {
		public DroneService getDroneService() {
			// Return this instance of LocalService so clients can call public methods
			return DroneService.this;
		}

		public CatroidDrone getCatroidDrone() {
			return catroidDrone;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/** method for clients */
	public int getRandomNumber() {

		return mGenerator.nextInt(100);
	}

}
