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

import java.util.EnumSet;
import java.util.List;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.formulaeditor.FormulaElement;
import at.tugraz.ist.catroid.formulaeditor.InternFormulaParser;
import at.tugraz.ist.catroid.formulaeditor.InternFormulaToInternTokenGenerator;
import at.tugraz.ist.catroid.formulaeditor.InternToken;

public class ParserTest extends AndroidTestCase {

	private static final double DELTA = 0.01;

	public void testParserTreeGenerationFormulas() {
		for (ParserFormulaTestData parserTest : EnumSet.allOf(ParserFormulaTestData.class)) {

			List<InternToken> internTokensToParse = InternFormulaToInternTokenGenerator
					.generateInternRepresentationByString(parserTest.getInput());
			InternFormulaParser parser = new InternFormulaParser(internTokensToParse);

			FormulaElement parserFormulaElement = parser.parseFormula();

			assertNotNull(
					"Formula is not parsed correctly: Testname: " + parserTest.name() + ": " + parserTest.getInput()
							+ "=", parserFormulaElement);

			assertEquals("Formula interpretation is not as expected: Testname: " + parserTest.name() + ": "
					+ parserTest.getInput() + "=", parserTest.getOutput(), parserFormulaElement.interpretRecursive());
		}
	}

	public void testParserFunctionInterpretation() {
		for (ParserFormulaFunctionsTestData parserTest : EnumSet.allOf(ParserFormulaFunctionsTestData.class)) {
			List<InternToken> internTokensToParse = InternFormulaToInternTokenGenerator
					.generateInternRepresentationByString(parserTest.getInput());
			InternFormulaParser parser = new InternFormulaParser(internTokensToParse);

			FormulaElement parserFormulaElement = parser.parseFormula();

			assertNotNull(
					"Formula is not parsed correctly: Testname: " + parserTest.name() + ": " + parserTest.getInput()
							+ "=", parserFormulaElement);

			assertEquals("Formula interpretation is not as expected: Testname: " + parserTest.name() + ": "
					+ parserTest.getInput() + "=", parserTest.getOutput(), parserFormulaElement.interpretRecursive(),
					DELTA);
		}

	}

	public void testParserTreeGenerationInvalidFormulas() {
		for (InvalidParserFormulaTestData parserTest : EnumSet.allOf(InvalidParserFormulaTestData.class)) {
			List<InternToken> internTokensToParse = InternFormulaToInternTokenGenerator
					.generateInternRepresentationByString(parserTest.getInput());
			InternFormulaParser parser = new InternFormulaParser(internTokensToParse);

			FormulaElement parserFormulaElement = parser.parseFormula();

			assertNull("Invalid formula parsed: Testname: " + parserTest.name() + ": " + parserTest.getInput() + "=",
					parserFormulaElement);
			assertEquals("First error character position is not as expected: Testname: " + parserTest.name() + ": "
					+ parserTest.getInput() + "=", parserTest.getFirstErrorPosition(),
					Integer.valueOf(parser.getErrorCharacterPosition()));

		}
	}

}
