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

import android.content.Context;
import android.util.Log;

public class InternToExternGenerator {

	private String generatedExternFormulaString;
	private ExternInternRepresentationMapping generatedExternInternRepresentationMapping;
	private Context context;

	private InternToExternLanguageConverter internToExternLanguageConverter;

	public InternToExternGenerator(Context context) {
		this.context = context;
		generatedExternFormulaString = "";
		generatedExternInternRepresentationMapping = new ExternInternRepresentationMapping();
	}

	public void generateExternStringAndMapping(List<InternToken> internTokenFormula) {
		Log.i("info", "generateExternStringAndMapping:enter");

		List<InternToken> internTokenList = new LinkedList<InternToken>();

		for (InternToken internToken : internTokenFormula) {
			internTokenList.add(internToken);
		}

		generatedExternInternRepresentationMapping = new ExternInternRepresentationMapping();

		generatedExternFormulaString = "";
		InternToken currentToken = null;
		InternToken nextToken = null;
		String externTokenString;
		int externStartIndex;
		int externEndIndex;

		int internTokenListIndex = 0;

		while (internTokenList.isEmpty() == false) {
			if (appendWhiteSpace(currentToken, nextToken)) {
				generatedExternFormulaString += " ";
			}
			externStartIndex = generatedExternFormulaString.length();
			currentToken = internTokenList.get(0);
			if (internTokenList.size() < 2) {
				nextToken = null;
			} else {
				nextToken = internTokenList.get(1);
			}
			//			Log.i("info", "generateExternStringAndMapping: currentTokenText = " + currentToken.getTokenSringValue());
			externTokenString = generateExternStringFromToken(currentToken);
			generatedExternFormulaString += externTokenString;
			externEndIndex = generatedExternFormulaString.length(); //TODO cursor position determination

			externEndIndex--; //TODO check Mapping

			if (externEndIndex < externStartIndex) {
				return;
			}

			generatedExternInternRepresentationMapping.putExternInternMapping(externStartIndex, externEndIndex,
					internTokenListIndex);
			generatedExternInternRepresentationMapping.putInternExternMapping(internTokenListIndex, externStartIndex,
					externEndIndex + 1);

			internTokenList.remove(0);
			internTokenListIndex++;

		}

		generatedExternFormulaString += " ";
		Log.i("info", "generateExternStringAndMapping: generatedExternFormulaString = " + generatedExternFormulaString);

	}

	private String generateExternStringFromToken(InternToken internToken) {
		switch (internToken.getInternTokenType()) {
			case NUMBER:
				String number = internToken.getTokenSringValue();

				if (!number.contains(".")) {
					return number;
				}

				String left = number.substring(0, number.indexOf("."));
				String right = number.substring(number.indexOf(".") + 1);

				return left + InternToExternLanguageConverter.getExternStringForInternTokenValue(".", context) + right;

			case OPERATOR:
				return internToken.getTokenSringValue();

			case BRACKET_OPEN:
			case FUNCTION_PARAMETERS_BRACKET_OPEN:
				return "(";
			case BRACKET_CLOSE:
			case FUNCTION_PARAMETERS_BRACKET_CLOSE:
				return ")";
			case FUNCTION_PARAMETER_DELIMITER:
				return ","; //TODO hardcoded delimiter value

			default:
				return InternToExternLanguageConverter.getExternStringForInternTokenValue(
						internToken.getTokenSringValue(), context);

				//TODO handle all cases(UserVariables etc...)
		}
	}

	private boolean appendWhiteSpace(InternToken currentToken, InternToken nextToken) {
		if (currentToken == null) {
			return false;
		}
		if (nextToken == null) {
			return true;
		}

		switch (nextToken.getInternTokenType()) {
			case FUNCTION_PARAMETERS_BRACKET_OPEN:
				return false;
		}
		return true;

	}

	public String getGeneratedExternFormulaString() {
		return generatedExternFormulaString;
	}

	public ExternInternRepresentationMapping getGeneratedExternInternRepresentationMapping() {
		return generatedExternInternRepresentationMapping;
	}
}
