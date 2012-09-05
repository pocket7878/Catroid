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

import java.util.LinkedList;
import java.util.List;

public class InternFormulaStringModify {

	public static String generateInternStringByReplace(int startIndexToReplace, int endIndexToReplace,
			List<InternToken> tokensToReplaceWith, String internalFormulaString) {

		String leftPart = internalFormulaString.substring(0, startIndexToReplace);

		//		internalFormulaString = internalFormulaString.substring(startIndexToReplace);

		InternToken endTokenToReplace = InternFormulaToInternTokenGenerator.generateInternTokenByIndex(
				endIndexToReplace, internalFormulaString);

		int righPartStartIndex = endTokenToReplace.toString().length() + endIndexToReplace;

		String rightPart = internalFormulaString.substring(righPartStartIndex);
		String middlePart = generateInternStringByInternTokenList(tokensToReplaceWith);

		return leftPart + middlePart + rightPart;

	}

	public static String generateInternStringByReplace(int internTokenToReplaceIndex,
			List<InternToken> tokensToReplaceWith, String internalFormulaString) {

		return generateInternStringByReplace(internTokenToReplaceIndex, internTokenToReplaceIndex, tokensToReplaceWith,
				internalFormulaString);
	}

	public static String generateInternStringByReplace(int internTokenToReplaceIndex, InternToken tokenToReplace,
			String internalFormulaString) {
		List<InternToken> tokensToReplaceWith = new LinkedList<InternToken>();
		tokensToReplaceWith.add(tokenToReplace);
		return generateInternStringByReplace(internTokenToReplaceIndex, internTokenToReplaceIndex, tokensToReplaceWith,
				internalFormulaString);
	}

	public static String generateInternStringByAppend(int internTokenToAppendIndex, List<InternToken> tokensToAppend,
			String internalFormulaString) {

		//TODO implement iff needed
		return null;
	}

	public static String generateInternStringByInsertAtBeginning(List<InternToken> tokensToInsert,
			String internalFormulaString) {
		return generateInternStringByInternTokenList(tokensToInsert) + internalFormulaString;
	}

	private static String generateInternStringByInternTokenList(List<InternToken> internTokenList) {
		String returnValue = "";
		for (InternToken internToken : internTokenList) {
			returnValue += internToken.toString();
		}
		return returnValue;
	}

}
