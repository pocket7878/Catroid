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

public enum ParserFormulaFunctionsTestData {
	SIN("sin(90)", 1.0), COS("cos(180)", -1.0), TAN("tan(45)", 1.0), LN("ln(e ^ 2)", 2.0), LOG("log(10^2)", 2.0), SQRT(
			"sqrt(4^2)", 4.0), ABS("abs(-4)", 4.0), ROUND("round(3.2)", 3.0), PI("pi", Math.PI), RANDOM_1(
			"rand(0.0001, 0.0002)", 0.00015), RANDOM_2("rand(0.0008, 0.0009)", 0.00085), RANDOM_3(
			"rand(0.0005, 0.0006)", 0.00055), RANDOM_4("rand(0.0007, 0.0008)", 0.00075);

	private String input;
	private Double output;

	ParserFormulaFunctionsTestData(String input, Double output) {
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
