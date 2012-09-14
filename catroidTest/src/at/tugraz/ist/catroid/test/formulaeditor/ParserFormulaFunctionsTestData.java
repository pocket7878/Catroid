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
	SIN(":function_name:sin:function_parameter_bracket_open::number:90:function_parameter_bracket_close:", 1.0), COS(
			":function_name:cos:function_parameter_bracket_open::number:180:function_parameter_bracket_close:", -1.0), TAN(
			":function_name:tan:function_parameter_bracket_open::number:45:function_parameter_bracket_close:", 1.0), LN(
			":function_name:ln:function_parameter_bracket_open::function_name:e:operator:^:number:2:function_parameter_bracket_close:",
			2.0), LOG(
			":function_name:log:function_parameter_bracket_open::number:10:operator:^:number:2:function_parameter_bracket_close:",
			2.0), SQRT(
			":function_name:sqrt:function_parameter_bracket_open::number:4:operator:^:number:2:function_parameter_bracket_close:",
			4.0), ABS(
			":function_name:abs:function_parameter_bracket_open::operator:-:number:4:function_parameter_bracket_close:",
			4.0), ROUND(
			":function_name:round:function_parameter_bracket_open::number:3.2:function_parameter_bracket_close:", 3.0), PI(
			":function_name:pi", Math.PI), RANDOM_1(
			":function_name:rand:function_parameter_bracket_open::number:0.0001:function_parameter_delimiter:,:number:0.0002:function_parameter_bracket_close:",
			0.00015), RANDOM_2(
			":function_name:rand:function_parameter_bracket_open::number:0.0008:function_parameter_delimiter:,:number:0.0009:function_parameter_bracket_close:",
			0.00085), RANDOM_3(
			":function_name:rand:function_parameter_bracket_open::number:0.0005:function_parameter_delimiter::number:0.0006:function_parameter_bracket_close:",
			0.00055), RANDOM_4(
			":function_name:rand:function_parameter_bracket_open::number:0.0007:function_parameter_delimiter::number:0.0008:function_parameter_bracket_close:",
			0.00075);

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
