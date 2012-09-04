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

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.formulaeditor.InternToken;
import at.tugraz.ist.catroid.formulaeditor.InternTokenType;
import at.tugraz.ist.catroid.formulaeditor.StringFormulaToInternTokenGenerator;

public class StringFormulaToInternTokenGeneratorTest extends AndroidTestCase {

	private static final double DELTA = 0.01;

	public void testGenerateInternTokenByIndex() {

		InternToken internToken = StringFormulaToInternTokenGenerator.generateInternTokenByIndex(0, ":number:23");
		assertNotNull("InternToken generation failed", internToken);
		assertEquals("InternToken-type generation failed", InternTokenType.NUMBER, internToken.getInternTokenType());
		assertEquals("InternToken-value generation failed", "23", internToken.getTokenSringValue());
	}
}
