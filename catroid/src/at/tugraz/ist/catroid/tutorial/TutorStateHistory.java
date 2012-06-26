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

import java.util.HashMap;

import android.util.Log;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

/**
 * @author drab
 * 
 */
public class TutorStateHistory {
	private int stateCounter = -1;
	private Task.Tutor tutor;
	private boolean backFlag = false;

	private HashMap<Integer, TutorState> stateMap;

	public TutorStateHistory(Task.Tutor tutor) {
		stateMap = new HashMap<Integer, TutorState>();
		this.tutor = tutor;
	}

	public void addStateToHistory(TutorState state) {
		stateCounter++;
		stateMap.put(stateCounter, state);
		Log.i("new", "New STATE for " + tutor + " added @ " + stateCounter);
	}

	public TutorState setBackAndReturnState(int setCount) {
		Log.i("new", "Actual State of " + tutor + ": " + stateCounter + " - setBackSteps: " + setCount);

		if (stateCounter - setCount >= 0) {
			stateCounter = stateCounter - setCount;
		} else {
			stateCounter = 0;
		}

		Log.i("new", "Tutor - " + tutor + " new State will be from: " + stateCounter);
		TutorState returnState = stateMap.get(stateCounter);

		if (stateCounter > 0) {
			stateCounter--;
		}

		return returnState;
	}
}
