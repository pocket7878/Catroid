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
package at.tugraz.ist.catroid.test.formulaeditor;

import at.tugraz.ist.catroid.formulaeditor.InternFormulaParser;

public enum InvalidParserFormulaTestData {
	EMPTY_FORMULA("", InternFormulaParser.PARSER_NO_INPUT), OPERATOR_MISSING_1(":number:1:number:1", 1),

	//	OPERATOR_MISSING_2("1+2+3 4+5+6", 6), OPERATOR_MISSING_3(
	//			"1*2--3 32/2*1+3", 7), OPERATOR_MISSING_4("1--1--1 1--1", 8), NUMBER_MISSING_1("-", 1), NUMBER_MISSING_2(
	//			"--", 1), NUMBER_MISSING_3("-1--", 4), NUMBER_MISSING_4("+", 0), NUMBER_MISSING_5("*", 0), NUMBER_MISSING_6(
	//			"/", 0), NUMBER_MISSING_7("+1", 0), NUMBER_MISSING_8("*1", 0), NUMBER_MISSING_9("/1", 0), NUMBER_MISSING_10(
	//			"1+1+1+1+1+", 10), TOO_MANY_OPERATORS("1+++++++", 2), RIGHT_BRACKET_MISSING_1("-((2)+2+3", 9);
	;

	private String input;
	private Integer firstErrorPosition;

	InvalidParserFormulaTestData(String input, Integer firstErrorPosition) {
		this.input = input;
		this.firstErrorPosition = firstErrorPosition;
	}

	public String getInput() {
		return input;
	}

	public Integer getFirstErrorPosition() {
		return firstErrorPosition;
	}

}
