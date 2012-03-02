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
package at.tugraz.ist.catroid;

import java.io.File;

import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.stage.StageListener;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class NativeDesktop {
	
	private static StageListener stageListener;
	
	public static void main (String[] argv) {
		if(argv.length < 1) {
			System.out.println("no commandline argument!!");
			return;
		}
		String projectName = argv[0];

		Values.NATIVE_DESKTOP_PLAYER = true;
		Consts.DEFAULT_ROOT = new File(".").getAbsolutePath();
		System.out.println("default root: "+Consts.DEFAULT_ROOT);
		Values.SCREEN_WIDTH = 320;
		Values.SCREEN_HEIGHT = 480;
				
		if(!checkProjectExists(projectName)) {
			System.out.println("The Project with the name "+projectName+" does not exist!!");
			return;
		}
		
		stageListener = new StageListener();
		ProjectManager.getInstance().loadProject(projectName, null, false);
				
		boolean resizePossible = calculateScreenSizes();
		
		new JoglApplication(stageListener, "Hello World", Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT, true);
		
	}
	
	private static boolean calculateScreenSizes() {
		boolean resizePossible;
		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().virtualScreenWidth;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().virtualScreenHeight;
		if (virtualScreenWidth == Values.SCREEN_WIDTH && virtualScreenHeight == Values.SCREEN_HEIGHT) {
			resizePossible = false;
			return resizePossible;
		}
		resizePossible = true;
		stageListener.maximizeViewPortWidth = Values.SCREEN_WIDTH + 1;
		do {
			stageListener.maximizeViewPortWidth--;
			stageListener.maximizeViewPortHeight = (int) (((float) stageListener.maximizeViewPortWidth / (float) virtualScreenWidth) * virtualScreenHeight);
		} while (stageListener.maximizeViewPortHeight > Values.SCREEN_HEIGHT);

		stageListener.maximizeViewPortX = (Values.SCREEN_WIDTH - stageListener.maximizeViewPortWidth) / 2;
		stageListener.maximizeViewPortY = (Values.SCREEN_HEIGHT - stageListener.maximizeViewPortHeight) / 2;
		return resizePossible;
	}
	
	private static boolean checkProjectExists(String projectName) {
		String projectPath = Consts.DEFAULT_ROOT+"/"+projectName;
		File projectDirectory =  new File(projectPath);
		if(!projectDirectory.exists()) {
			return false;
		}
		File projectFile = new File(projectDirectory+"/"+projectName+Consts.PROJECT_EXTENTION);
		if(!projectFile.exists()) {
			return false;
		}
		return true;
	}
	
}
