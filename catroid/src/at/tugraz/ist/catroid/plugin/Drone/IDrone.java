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

public interface IDrone {

	public abstract boolean connect();

	public abstract void disconnect();

	public abstract void takeoff();

	public abstract void land();

	public abstract void emergency();

	public abstract void emergencyLand();

	public abstract void move(double throttle, double roll, double pitch, double yaw);

	public abstract void playLedAnimation(int animation, float frequency, int durationSeconds);

	public abstract void playMoveAnimation(int animation, int durationSeconds);

	public abstract boolean changeFlyingMode(int mode);

	public abstract boolean doStartUpConfiguration();

	public abstract boolean setConfiguration(String configuration, boolean isIDScmd);

	public abstract void setDslTimeout(int seconds);

	public abstract int getFlyingMode();

	public abstract int getCameraOrientation();

	public abstract int getBatteryLoad();

	public abstract boolean isConnected();

	public abstract void startVideo();

	public abstract void stopVideo();

	public abstract void renderVideoFrame(int glHandle);

	public abstract void startVideoRecorder(String path);

	public abstract void stopVideoRecorder();

	public abstract void saveVideoSnapshot(String path);

	public abstract String getFirmwareVersion();

	public abstract boolean uploadFirmwareFile();

	public abstract boolean restartDrone();

}