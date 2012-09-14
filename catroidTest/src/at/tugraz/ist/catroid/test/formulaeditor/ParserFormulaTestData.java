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
	UNARY_MINUS_1(":operator:-:number:1", -1.0), UNARY_MINUS_2(":operator:-:number:1:operator:-:operator:-:number:1",
			0.0), UNARY_MINUS_MULT(":operator:-:number:1:operator:*:operator:-:number:1", 1.0), UNARY_MINUS_DIVIDE(
			":operator:-:number:1:operator:/:operator:-:number:1", 1.0), UNARY_MINUS_BRACKETS(
			":operator:-:bracket_open:(:number:2:bracket_close:):operator:-:bracket_open:(:operator:-:number:2:bracket_close:)",
			0.0),

	OPERATOR_PRIORITY_1(":number:1:operator:-:number:2:operator:*:number:2", -3.0), OPERATOR_PRIORITY_2(
			":number:1:operator:+:number:2:operator:*:number:2", 5.0), OPERATOR_PRIORITY_3(
			":number:1:operator:-:number:2:operator:/:number:2", 0.0), OPERATOR_PRIORITY_4(
			":number:1:operator:+:number:2:operator:/:number:2", 2.0), OPERATOR_PRIORITY_LEFT_BINDING_1(
			":number:5:operator:-:number:4:operator:-:number:1", 0.0), OPERATOR_PRIORITY_LEFT_BINDING_2(
			":number:100:operator:/:number:10:operator:/:number:10", 1.0), OPERATOR_PRIORITY_LONG_1(
			":number:2:operator:*:number:2:operator:*:number:2:operator:*:number:2:operator:+:number:3:operator:*:number:3:operator:*:number:3:operator:*:number:3",
			97.0), OPERATOR_PRIORITY_LONG_2(
			":number:16:operator:/:number:2:operator:/:number:2:operator:/:number:2:operator:+:number:81:operator:/:number:3:operator:/:number:3:operator:/:number:3",
			5.0), OPERATOR_CHAIN_1(
			":number:1:operator:+:number:2:operator:*:number:3:operator:^:number:2:operator:+:number:1", 20.0), OPERATOR_CHAIN_2(
			":number:10:operator:+:number:12:operator:-:number:2:operator:*:number:3:operator:-:number:4", 12.0), BRACKET_TEST_1(
			":bracket_open:(:number:2:operator:+:number:2:bracket_close:):operator:*:number:3", 12.0), BRACKET_TEST_2(
			":bracket_open:(:number:1:operator:+:number:2:bracket_close:):operator:*:bracket_open:(:number:1:operator:+:number:2:bracket_close:)",
			9.0);

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
