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

import android.util.Log;

public class InternFormulaToInternTokenGenerator {

	public InternFormulaToInternTokenGenerator() {
	}

	public static List<InternToken> generateInternRepresentationByString(String internFormulaRepresentation) {

		Log.i("info", "generateInternRepresentationByString:enter");
		Log.i("info", "generateInternRepresentationByString: internFormulaRepresentation = "
				+ internFormulaRepresentation);
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		int currentIndex = 0;

		while (currentIndex < internFormulaRepresentation.length()) {
			InternToken tokenToAdd = generateInternTokenByIndex(currentIndex, internFormulaRepresentation);
			if (tokenToAdd == null) {
				return null;
			}
			tokenToAdd.setInternPositionIndex(currentIndex);
			internTokenList.add(tokenToAdd);

			currentIndex += tokenToAdd.toString().length();

		}

		return internTokenList;
	}

	public static List<InternToken> generateInternTokenListByFunctionIndex(int functionStartIndex,
			String internFormulaRepresentation) {

		List<InternToken> functionInternTokenList = new LinkedList<InternToken>();

		InternToken functionNameToken = generateInternTokenByIndex(functionStartIndex, internFormulaRepresentation);

		if (functionNameToken.getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		functionInternTokenList.add(functionNameToken);

		int functionIndex = functionStartIndex + functionNameToken.toString().length();

		InternToken functionStartParameter = generateInternTokenByIndex(functionIndex, internFormulaRepresentation);

		if (functionStartParameter == null) {
			return functionInternTokenList;
		}

		functionInternTokenList.add(functionStartParameter);

		functionIndex += functionStartParameter.toString().length();
		InternToken tempSearchToken;
		int nestedFunctionsCounter = 1;

		do {
			tempSearchToken = generateInternTokenByIndex(functionIndex, internFormulaRepresentation);
			if (tempSearchToken == null) {
				return null;
			}
			functionIndex += tempSearchToken.toString().length();
			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
				nestedFunctionsCounter++;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
				nestedFunctionsCounter--;
			}
			functionInternTokenList.add(tempSearchToken);

		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE
				|| nestedFunctionsCounter != 0);

		return functionInternTokenList;

	}

	public static InternToken generateInternTokenByIndex(int index, String internFormulaRepresentation) {
		internFormulaRepresentation = internFormulaRepresentation.substring(index);

		if (internFormulaRepresentation.startsWith(":") == false) {
			return null;
		}
		InternToken returnToken = getNextToken(internFormulaRepresentation);
		returnToken.setInternPositionIndex(index);

		return returnToken;
	}

	private static InternToken getNextToken(String internFormulaRepresentation) {

		if (internFormulaRepresentation.startsWith(":") == false) {
			return null;
		}

		int internTokenTypeNameEndIndex = internFormulaRepresentation.indexOf(":", 1);
		String internTokenTypeName = internFormulaRepresentation.substring(0, internTokenTypeNameEndIndex + 1);

		InternTokenType internTokenType = InternTokenType.getInternTokenTypeByString(internTokenTypeName);

		String internTokenValue = internFormulaRepresentation.substring(internTokenTypeNameEndIndex + 1);
		int internTokenValueEndIndex = internTokenValue.indexOf(":");
		if (internTokenValueEndIndex != -1) {
			internTokenValue = internTokenValue.substring(0, internTokenValueEndIndex);
		}

		InternToken returnInternToken = new InternToken(internTokenValue, internTokenType);

		return returnInternToken;
	}

}
