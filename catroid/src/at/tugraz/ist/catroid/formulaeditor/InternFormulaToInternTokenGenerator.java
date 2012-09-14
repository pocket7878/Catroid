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

import java.util.Collections;
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

		List<InternToken> internTokenList = generateInternRepresentationByString(internFormulaRepresentation);

		int internTokenListIndex = 0;

		for (InternToken internToken : internTokenList) {
			if (internToken.getInternPositionIndex() == functionStartIndex) {
				break;
			}
			internTokenListIndex++;
		}

		if (internTokenListIndex == internTokenList.size()) {
			return null;
		}

		if (internTokenList.get(internTokenListIndex).getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		List<InternToken> functionInternTokenList = getFunctionFromList(internTokenListIndex, internTokenList);

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

	public static List<InternToken> generateInternTokenListByFunctionBracketOpen(int internPositionIndex,
			String internFormulaString) {

		List<InternToken> internTokenList = generateInternRepresentationByString(internFormulaString);

		int functionBracketOpenInternTokenListIndex = 0;

		for (InternToken internToken : internTokenList) {
			if (internToken.getInternPositionIndex() == internPositionIndex) {
				break;
			}
			functionBracketOpenInternTokenListIndex++;
		}

		if (functionBracketOpenInternTokenListIndex == 0
				|| functionBracketOpenInternTokenListIndex == internTokenList.size()) {
			return null;
		}

		InternToken functionNameInternToken = internTokenList.get(functionBracketOpenInternTokenListIndex - 1);

		if (functionNameInternToken.getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		List<InternToken> functionInternTokenList = getFunctionFromList(functionBracketOpenInternTokenListIndex - 1,
				internTokenList);

		return functionInternTokenList;
	}

	public static List<InternToken> generateInternTokenListByFunctionBracketClose(int internPositionIndex,
			String internFormulaString) {
		List<InternToken> internTokenList = generateInternRepresentationByString(internFormulaString);

		int functionBracketCloseInternTokenListIndex = 0;

		for (InternToken internToken : internTokenList) {
			if (internToken.getInternPositionIndex() == internPositionIndex) {
				break;
			}
			functionBracketCloseInternTokenListIndex++;
		}

		if (functionBracketCloseInternTokenListIndex == 0
				|| functionBracketCloseInternTokenListIndex == internTokenList.size()) {
			return null;
		}

		List<InternToken> functionInternTokenList = new LinkedList<InternToken>();
		functionInternTokenList.add(internTokenList.get(functionBracketCloseInternTokenListIndex));

		int functionIndex = functionBracketCloseInternTokenListIndex - 1;
		InternToken tempSearchToken;
		int nestedFunctionsCounter = 1;

		do {
			if (functionIndex < 0) {
				return null;
			}
			tempSearchToken = internTokenList.get(functionIndex);
			functionIndex--;

			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
				nestedFunctionsCounter--;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
				nestedFunctionsCounter++;
			}
			functionInternTokenList.add(tempSearchToken);

		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN
				|| nestedFunctionsCounter != 0);

		tempSearchToken = internTokenList.get(functionIndex);

		if (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		functionInternTokenList.add(tempSearchToken);

		Collections.reverse(functionInternTokenList);

		return functionInternTokenList;

	}

	public static List<InternToken> generateInternTokenListByFunctionParameterDelimiter(int internPositionIndex,
			String internFormulaString) {
		List<InternToken> internTokenList = generateInternRepresentationByString(internFormulaString);

		int functionParameterDelimiterInternTokenListIndex = 0;

		for (InternToken internToken : internTokenList) {
			if (internToken.getInternPositionIndex() == internPositionIndex) {
				break;
			}
			functionParameterDelimiterInternTokenListIndex++;
		}

		if (functionParameterDelimiterInternTokenListIndex == 0
				|| functionParameterDelimiterInternTokenListIndex == internTokenList.size()) {
			return null;
		}

		List<InternToken> functionInternTokenList = new LinkedList<InternToken>();
		functionInternTokenList.add(internTokenList.get(functionParameterDelimiterInternTokenListIndex));

		int functionIndex = functionParameterDelimiterInternTokenListIndex - 1;
		InternToken tempSearchToken;
		int nestedFunctionsCounter = 1;

		do {
			if (functionIndex < 0) {
				return null;
			}
			tempSearchToken = internTokenList.get(functionIndex);
			functionIndex--;

			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
				nestedFunctionsCounter--;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
				nestedFunctionsCounter++;
			}
			functionInternTokenList.add(tempSearchToken);

		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN
				|| nestedFunctionsCounter != 0);

		tempSearchToken = internTokenList.get(functionIndex);

		if (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		functionInternTokenList.add(tempSearchToken);

		Collections.reverse(functionInternTokenList);

		functionIndex = functionParameterDelimiterInternTokenListIndex + 1;
		nestedFunctionsCounter = 1;

		do {
			if (functionIndex >= internTokenList.size()) {
				return null;
			}
			tempSearchToken = internTokenList.get(functionIndex);
			functionIndex++;

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

	public static List<InternToken> generateTokenListByBracketOpen(int internPositionIndex, String internFormulaString) {

		List<InternToken> internTokenList = generateInternRepresentationByString(internFormulaString);

		int internTokenListIndex = 0;

		for (InternToken internToken : internTokenList) {
			if (internToken.getInternPositionIndex() == internPositionIndex) {
				break;
			}
			internTokenListIndex++;
		}

		if (internTokenListIndex == internTokenList.size()) {
			return null;
		}

		if (internTokenList.get(internTokenListIndex).getInternTokenType() != InternTokenType.BRACKET_OPEN) {
			return null;
		}

		List<InternToken> bracketInternTokenListToReturn = new LinkedList<InternToken>();
		bracketInternTokenListToReturn.add(internTokenList.get(internTokenListIndex));

		int bracketsIndex = internTokenListIndex + 1;
		int nestedBracketsCounter = 1;
		InternToken tempSearchToken;

		do {
			if (bracketsIndex >= internTokenList.size()) {
				return null;
			}
			tempSearchToken = internTokenList.get(bracketsIndex);
			bracketsIndex++;

			if (tempSearchToken.getInternTokenType() == InternTokenType.BRACKET_OPEN) {
				nestedBracketsCounter++;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.BRACKET_CLOSE) {
				nestedBracketsCounter--;
			}
			bracketInternTokenListToReturn.add(tempSearchToken);

		} while (tempSearchToken.getInternTokenType() != InternTokenType.BRACKET_CLOSE || nestedBracketsCounter != 0);

		return bracketInternTokenListToReturn;

	}

	public static List<InternToken> generateTokenListByBracketClose(int internPositionIndex, String internFormulaString) {
		List<InternToken> internTokenList = generateInternRepresentationByString(internFormulaString);

		int internTokenListIndex = 0;

		for (InternToken internToken : internTokenList) {
			if (internToken.getInternPositionIndex() == internPositionIndex) {
				break;
			}
			internTokenListIndex++;
		}

		if (internTokenListIndex == internTokenList.size()) {
			return null;
		}

		if (internTokenList.get(internTokenListIndex).getInternTokenType() != InternTokenType.BRACKET_CLOSE) {
			return null;
		}

		List<InternToken> bracketInternTokenListToReturn = new LinkedList<InternToken>();
		bracketInternTokenListToReturn.add(internTokenList.get(internTokenListIndex));

		int bracketSearchIndex = internTokenListIndex - 1;
		int nestedBracketsCounter = 1;
		InternToken tempSearchToken;

		do {
			if (bracketSearchIndex < 0) {
				return null;
			}
			tempSearchToken = internTokenList.get(bracketSearchIndex);
			bracketSearchIndex--;

			if (tempSearchToken.getInternTokenType() == InternTokenType.BRACKET_CLOSE) {
				nestedBracketsCounter++;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.BRACKET_OPEN) {
				nestedBracketsCounter--;
			}
			bracketInternTokenListToReturn.add(tempSearchToken);

		} while (tempSearchToken.getInternTokenType() != InternTokenType.BRACKET_OPEN || nestedBracketsCounter != 0);

		Collections.reverse(bracketInternTokenListToReturn);
		return bracketInternTokenListToReturn;
	}

	private static List<InternToken> getFunctionFromList(int functionStartListIndex, List<InternToken> internTokenList) {

		InternToken functionNameToken = internTokenList.get(functionStartListIndex);

		List<InternToken> functionInternTokenList = new LinkedList<InternToken>();

		if (functionNameToken.getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		functionInternTokenList.add(functionNameToken);

		int functionIndex = functionStartListIndex + 1;

		if (functionIndex >= internTokenList.size()) {
			return functionInternTokenList;
		}

		InternToken functionStartParameter = internTokenList.get(functionIndex);

		if (functionStartParameter == null) {
			return functionInternTokenList;
		}

		if (functionStartParameter.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
			return functionInternTokenList;
		}

		functionInternTokenList.add(functionStartParameter);

		functionIndex++;
		InternToken tempSearchToken;
		int nestedFunctionsCounter = 1;

		do {
			if (functionIndex >= internTokenList.size()) {
				return null;
			}
			tempSearchToken = internTokenList.get(functionIndex);
			functionIndex++;

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

}
