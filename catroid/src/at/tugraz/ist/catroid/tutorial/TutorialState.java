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
package at.tugraz.ist.catroid.tutorial;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author faxxe
 * 
 *         Saving the actual state to a file has not been done jet!
 */
public class TutorialState implements Serializable {
	private HashMap<String, Integer> table;
	int ret;

	public void clear() {
		table = new HashMap<String, Integer>();
	}

	public TutorialState() {
		table = new HashMap<String, Integer>();
	}

	public TutorialState(HashMap table) {
		this.table = table;
	}

	public void saveActualState(String ativityName, Integer instructionCount) {
		table.put(ativityName, instructionCount);
	}

	public int getLastState(String activityName) {
		if (table.containsKey(activityName)) {
			ret = table.get(activityName);
		} else {
			ret = 0;
		}
		return ret;
	}

}
