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
	private int stateCounter = 0;
	private Task.Tutor tutor;
	private boolean stateCountCarry = false;

	private HashMap<Integer, TutorState> stateMap;

	public TutorStateHistory(Task.Tutor tutor) {
		stateMap = new HashMap<Integer, TutorState>();
		this.tutor = tutor;
	}

	public void addStateToHistory(TutorState state) {
		stateMap.put(stateCounter, state);
		Log.i("new", "ADDED STATE: " + state.getState() + " INTO HISTORY for " + tutor + " @ StateCounter: "
				+ stateCounter);
		stateCounter++;
		stateCountCarry = true;
	}

	public TutorState setBackAndReturnState(int setCount) {
		if (stateCountCarry) {
			stateCountCarry = false;
			stateCounter--;
		}
		Log.i("new", "Actual State of " + tutor + ": " + stateCounter + " - setBackSteps: " + setCount);

		if (stateCounter - setCount > 0) {
			stateCounter = stateCounter - setCount;
		} else {
			stateCounter = 0;
		}

		Log.i("new", "New State for Tutor - " + tutor + " will be from StateHistory: " + stateCounter);
		TutorState returnState = stateMap.get(stateCounter);

		//TODO: Have an Eye on this
		if (stateCounter == 0) {
			stateCounter++;
		}
		return returnState;
	}

	public void setStateCounterExtraStep() {
		stateCounter++;
	}

	public void clearStateHistory() {
		stateCountCarry = false;
		stateCounter = 0;
		stateMap.clear();
	}

}
