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
package at.tugraz.ist.catroid.formulaeditor;

import android.util.Log;
import android.util.SparseArray;

public class ExternInternRepresentationMapping {

	private SparseArray<Integer> externInternMapping;
	private SparseArray<Integer> internExternMapping;

	private int externStringLength = 10;
	private int internStringLength;

	public ExternInternRepresentationMapping() {
		externInternMapping = new SparseArray<Integer>();
		internExternMapping = new SparseArray<Integer>();
	}

	public void putExternInternMapping(int externStartIndex, int externEndIndex, int internStartIndex) {
		Log.i("info", "Mapping put extern to intern: FROM extern start/end = " + externStartIndex + "/"
				+ externEndIndex + " TO " + internStartIndex);
		externInternMapping.put(externStartIndex, internStartIndex);
		externInternMapping.put(externEndIndex, internStartIndex);

		if (externEndIndex >= externStringLength) {
			externStringLength = externEndIndex + 1;
		}
	}

	public void putInternExternMapping(int internStartIndex, int externStartIndex) {
		internExternMapping.put(internStartIndex, externStartIndex);

	}

	public Integer getExternIndexByInternIndex(int internIndex) {
		//TODO implement to show parse errors and to update selection indizes

		return null;
	}

	public Integer getInternTokenByExternIndex(int externIndex) {

		Integer searchDownInternToken = searchDown(externInternMapping, externIndex - 1);
		Integer currentInternToken = externInternMapping.get(externIndex);
		Integer searchUpInternToken = searchUp(externInternMapping, externIndex + 1, externStringLength);

		if (currentInternToken != null) {
			return currentInternToken;
		}
		if (searchDownInternToken != null && searchUpInternToken != null) {
			if (searchDownInternToken == searchUpInternToken) {
				return searchDownInternToken;
			}
		}

		//TODO return functionName Token when parameter delimiter deleted

		return null;
	}

	public int getExternTokenStartOffset(int externIndex, Integer internTokenOffsetTo) {
		for (int searchIndex = externIndex; searchIndex > 0; searchIndex--) {
			if (externInternMapping.get(searchIndex) == internTokenOffsetTo) {
				int rightEdgeSelectionToken = getExternTokenStartOffset(searchIndex - 1, internTokenOffsetTo);
				if (rightEdgeSelectionToken == -1) {
					return externIndex - searchIndex;
				} else {
					return rightEdgeSelectionToken + 1;
				}
			} else if (externInternMapping.get(searchIndex) != null) {
				return -1;
			}
		}
		return -1;
	}

	private Integer searchDown(SparseArray<Integer> mapping, int index) {

		for (int searchIndex = index; searchIndex > 0; searchIndex--) {
			if (mapping.get(searchIndex) != null) {
				return mapping.get(searchIndex);
			}
		}
		return null;
	}

	private Integer searchUp(SparseArray<Integer> mapping, int index, int maximalIndex) {
		for (int searchIndex = index; searchIndex < maximalIndex; searchIndex++) {
			if (mapping.get(searchIndex) != null) {
				return mapping.get(searchIndex);
			}
		}
		return null;
	}

}
