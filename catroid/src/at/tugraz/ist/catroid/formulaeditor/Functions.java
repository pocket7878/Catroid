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

import java.util.EnumSet;

public enum Functions {
	//PLUS("+", 0), MINUS("-", 0), MULT("*", 1), DIVIDE("/", 1), MOD("%", 1), POW("^", 2);
	SIN("sin"), COS("cos"), TAN("tan"), LN("ln"), LOG("log"), SQRT("sqrt"), RAND("rand"), ROUND("round");
	private final String functionName;

	Functions(String value) {
		this.functionName = value;
	}

	//	public static Functions geFunctionByValue(String value) {
	//		for (Functions fct : EnumSet.allOf(Functions.class)) {
	//			if (fct.value.equals(value)) {
	//				return fct;
	//			}
	//		}
	//		return null;
	//	}

	public static boolean isFunction(String value) {
		for (Functions fct : EnumSet.allOf(Functions.class)) {
			if (value.startsWith(fct.functionName)) {
				return true;
			}
		}
		return false;
	}

}
