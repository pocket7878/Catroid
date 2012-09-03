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

import android.util.SparseArray;

public class InternExternRepresentationMapping {

	private SparseArray<Integer> externInternMapping;
	private SparseArray<Integer> internExternMapping;

	private int externInternMappingMaximalIndex;
	private int internExternMappingMaximalIndex;

	public InternExternRepresentationMapping() {
		externInternMapping = new SparseArray<Integer>();
		internExternMapping = new SparseArray<Integer>();
	}

	public void insertNewMapping() {

	}

	public void insertNewExternInternMapping(int keyStartIndex, int keyEndIndex, int mappingIndex) {
		externInternMapping.put(keyStartIndex, mappingIndex);
		externInternMapping.put(keyEndIndex, mappingIndex);
	}

	public void insertNewInternExternMapping(int keyStartIndex, int keyEndIndex, int mappingIndex) {
		internExternMapping.put(keyStartIndex, mappingIndex);
		internExternMapping.put(keyEndIndex, mappingIndex);
	}

	public Integer getInternStartIndexByExternIndex(int externIndex) {

		Integer searchDownInternTokenIndex = searchDown(externInternMapping, externIndex - 1);
		Integer currentInternTokenIndex = externInternMapping.get(externIndex);
		Integer searchUpInternTokenIndex = searchUp(externInternMapping, externIndex + 1,
				externInternMappingMaximalIndex);

		if (currentInternTokenIndex != null) {
			return currentInternTokenIndex;
		}
		if (searchDownInternTokenIndex != null && searchUpInternTokenIndex != null) {
			if (searchDownInternTokenIndex == searchUpInternTokenIndex) {
				return searchDownInternTokenIndex;
			}
		}

		return null;
	}

	public Integer getExternStartIndexByInternIndex(int internIndex) {
		Integer searchDownInternTokenIndex = searchDown(internExternMapping, internIndex - 1);
		Integer currentInternTokenIndex = internExternMapping.get(internIndex);
		Integer searchUpInternTokenIndex = searchUp(internExternMapping, internIndex + 1,
				internExternMappingMaximalIndex);

		if (currentInternTokenIndex != null) {
			return currentInternTokenIndex;
		}
		if (searchDownInternTokenIndex != null && searchUpInternTokenIndex != null) {
			if (searchDownInternTokenIndex == searchUpInternTokenIndex) {
				return searchDownInternTokenIndex;
			}
		}

		return null;
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
