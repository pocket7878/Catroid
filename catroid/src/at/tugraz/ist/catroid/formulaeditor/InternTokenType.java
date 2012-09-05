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

public enum InternTokenType {
	NUMBER(":number:"), OPERATOR(":operator:"), FUNCTION_NAME(":function_name:"), BRACKET_OPEN(":bracket_open:"), BRACKET_CLOSE(
			":bracket_close:"), SENSOR(":sensor:"), FUNCTION_PARAMETERS_BRACKET_OPEN(
			":function_parameter_bracket_open:"), FUNCTION_PARAMETERS_BRACKET_CLOSE(
			":function_parameters_bracket_close:"), COSTUME(":costume:"), PERIOD(":period:");

	private String tokenPrefix;

	InternTokenType(String tokenPrefix) {
		this.tokenPrefix = tokenPrefix;
	}

	public static InternTokenType getInternTokenTypeByString(String value) {
		for (InternTokenType internTokenType : EnumSet.allOf(InternTokenType.class)) {
			if (value.equals(internTokenType.tokenPrefix)) {
				return internTokenType;
			}
		}
		return null;
	}

	public String getInternTokenPrefix() {
		return tokenPrefix;
	}

}
