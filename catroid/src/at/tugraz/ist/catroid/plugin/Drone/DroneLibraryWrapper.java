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
package at.tugraz.ist.catroid.plugin.Drone;

import android.content.Context;
import android.util.Log;

public class DroneLibraryWrapper implements IDrone {

	private final static String droneNamespace = "at.tugraz.ist.droned";
	private final static String droneClass = "at.tugraz.ist.droned.Drone";

	private Object droneInstance;
	private Context droneContext;

	public DroneLibraryWrapper(Context context) throws Exception {
		droneContext = context.createPackageContext(DroneLibraryWrapper.droneNamespace, Context.CONTEXT_INCLUDE_CODE
				| Context.CONTEXT_IGNORE_SECURITY);

		Class<?> droneClass = Class.forName(DroneLibraryWrapper.droneClass, true, droneContext.getClassLoader());

		droneInstance = droneClass.getMethod("getInstance", (Class[]) null).invoke(null, (Object[]) null);

	}

	public boolean connect() {
		try {
			return (Boolean) this.droneInstance.getClass().getMethod("connect", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
		return false;
	}

	private void printException(Exception e) {
		Log.e(DroneConsts.DroneLogTag, "Error talking to DCF.", e);
	}

	public void disconnect() {
		try {
			this.droneInstance.getClass().getMethod("disconnect", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void takeoff() {
		try {
			this.droneInstance.getClass().getMethod("takeoff", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void land() {
		try {
			this.droneInstance.getClass().getMethod("land", (Class[]) null).invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void playLedAnimation(int animation, float frequency, int durationSeconds) {
		try {
			this.droneInstance.getClass().getMethod("playLedAnimation", int.class, float.class, int.class)
					.invoke(this.droneInstance, animation, frequency, durationSeconds);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void playMoveAnimation(int animation, int durationSeconds) {
		try {
			this.droneInstance.getClass().getMethod("playMoveAnimation", int.class, int.class)
					.invoke(this.droneInstance, animation, durationSeconds);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void move(double throttle, double roll, double pitch, double yaw) {
		try {
			this.droneInstance.getClass().getMethod("move", double.class, double.class, double.class, double.class)
					.invoke(this.droneInstance, throttle, roll, pitch, yaw);
		} catch (Exception e) {
			printException(e);
		}
	}

	public boolean changeFlyingMode(int mode) {
		try {
			return (Boolean) this.droneInstance.getClass().getMethod("changeFlyingMode", int.class)
					.invoke(this.droneInstance, mode);
		} catch (Exception e) {
			printException(e);
		}
		return false;
	}

	public void emergency() {
		try {
			this.droneInstance.getClass().getMethod("emergency", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void emergencyLand() {
		try {
			this.droneInstance.getClass().getMethod("emergencyLand", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
	}

	public boolean doStartUpConfiguration() {
		try {
			return (Boolean) this.droneInstance.getClass().getMethod("doStartUpConfiguration", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
		return false;
	}

	public boolean setConfiguration(String cmd, boolean isIDScmd) {
		try {
			return (Boolean) this.droneInstance.getClass().getMethod("setConfiguration", String.class, boolean.class)
					.invoke(this.droneInstance, cmd, isIDScmd);
		} catch (Exception e) {
			printException(e);
		}
		return false;
	}

	public void setDslTimeout(int seconds) {
		try {
			this.droneInstance.getClass().getMethod("setDslTimeout", int.class).invoke(this.droneInstance, seconds);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void startVideo() {
		try {
			this.droneInstance.getClass().getMethod("startVideo", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void stopVideo() {
		try {
			this.droneInstance.getClass().getMethod("stopVideo", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void renderVideoFrame(int glHandle) {
		try {
			this.droneInstance.getClass().getMethod("renderVideoFrame", int.class).invoke(this.droneInstance, glHandle);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void startVideoRecorder(String path) {
		try {
			this.droneInstance.getClass().getMethod("startVideoRecorder", String.class)
					.invoke(this.droneInstance, path);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void stopVideoRecorder() {
		try {
			this.droneInstance.getClass().getMethod("stopVideoRecorder", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void saveVideoSnapshot(String path) {
		try {
			this.droneInstance.getClass().getMethod("saveVideoSnapshot", String.class).invoke(this.droneInstance, path);
		} catch (Exception e) {
			printException(e);
		}
	}

	public int getFlyingMode() {
		try {
			return (Integer) this.droneInstance.getClass().getMethod("getFlyingMode", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
		return -1;
	}

	public int getCameraOrientation() {
		try {
			return (Integer) this.droneInstance.getClass().getMethod("getCameraOrientation", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
		return -1;
	}

	public int getBatteryLoad() {
		try {
			return (Integer) this.droneInstance.getClass().getMethod("getBatteryLoad", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
		return -1;
	}

	public boolean isConnected() {
		try {
			return (Boolean) this.droneInstance.getClass().getMethod("isConnected", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
		return false;
	}

	public String getFirmwareVersion() {
		try {
			return (String) this.droneInstance.getClass().getMethod("getFirmwareVersion", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
		return null;
	}

	public boolean uploadFirmwareFile() {
		try {
			return (Boolean) this.droneInstance.getClass().getMethod("uploadFirmwareFile", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
		return false;
	}

	public boolean restartDrone() {
		try {
			return (Boolean) this.droneInstance.getClass().getMethod("restartDrone", (Class[]) null)
					.invoke(this.droneInstance, (Object[]) null);
		} catch (Exception e) {
			printException(e);
		}
		return false;
	}
}
