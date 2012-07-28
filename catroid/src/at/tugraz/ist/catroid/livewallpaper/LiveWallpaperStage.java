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
package at.tugraz.ist.catroid.livewallpaper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.ui.MainMenuActivity;

/**
 * @author simgeszgn
 * 
 */
public class LiveWallpaperStage extends Activity {

	private ProjectManager insManager;
	private Project insProject;
	CostumeData convertedCostume;
	MainMenuActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		//		Button convert = (Button) findViewById(R.id.convert);
		//		convert.setOnClickListener(new View.OnClickListener() {
		//
		//			public void onClick(View v) {
		//				// TODO Auto-generated method stub
		//				Log.v("DEBUG", "tıklama algılandı");
		//				if (getCurrProjectName().equals("ada")) {
		//					Log.v("VERBOSE", "ada projesi su anki projedir");
		//				}
		//			}
		//		});
	}

	public String getCurrProjectName() {
		Log.v("DEBUG", "name :" + ProjectManager.getInstance().getCurrentProject().getName());
		return ProjectManager.getInstance().getCurrentProject().getName();
	}
}
