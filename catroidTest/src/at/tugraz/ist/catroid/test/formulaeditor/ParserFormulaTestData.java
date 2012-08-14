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

public enum ParserFormulaTestData {
	UNARY_MINUS_1("-1", -1.0), UNARY_MINUS_2("-1--1", 0.0), UNARY_MINUS_MULT("-1*-1", 1.0), UNARY_MINUS_DIVIDE("-1/-1",
			1.0), UNARY_MINUS_BRACKETS("-(2)-(-2)", 0.0), OPERATOR_PRIORITY_1("1-2*2", -3.0), OPERATOR_PRIORITY_2(
			"1+2*2", 5.0), OPERATOR_PRIORITY_3("1-2/2", 0.0), OPERATOR_PRIORITY_4("1+2/2", 2.0), OPERATOR_PRIORITY_LEFT_BINDING_1(
			"5-4-1", 0.0), OPERATOR_PRIORITY_LEFT_BINDING_2("100/10/10", 1.0), OPERATOR_PRIORITY_LONG_1(
			"2*2*2*2 + 3*3*3*3", 97.0), OPERATOR_PRIORITY_LONG_2("16/2/2/2 + 81/3/3/3", 5.0), OPERATOR_CHAIN_1(
			"1 + 2 * 3 ^ 2 + 1", 20.0), OPERATOR_CHAIN_2("10+ 12 - 2 * 3 - 4 ", 12.0), BRACKET_TEST_1("(2+2)*3", 12.0), BRACKET_TEST_2(
			"(1+2)*(1+2)", 9.0);

	private String input;
	private Double output;

	ParserFormulaTestData(String input, Double output) {
		this.input = input;
		this.output = output;
	}

	public String getInput() {
		return input;
	}

	public Double getOutput() {
		return output;
	}

}
