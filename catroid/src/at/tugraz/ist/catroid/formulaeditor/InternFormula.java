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

import java.util.List;

public class InternFormula {

	//TODO: enter all prefixes
	public static final String INTERN_TOKEN_PREFIX_NUMBER = ":number:";

	private ExternInternRepresentationMapping externInternRepresentationMapping;

	private String internalFormulaString;

	public InternFormula(String internalFormulaString) {
		this.internalFormulaString = internalFormulaString;
	}

	public void setInternExternRepresentationMapping(ExternInternRepresentationMapping internExternRepresentationMapping) {
		this.externInternRepresentationMapping = internExternRepresentationMapping;
	}

	public void handleKeyInput(CatKeyEvent catKeyEvent, int externCursorPosition) {
		Integer cursorPositionTokenIndex = externInternRepresentationMapping
				.getInternTokenByExternIndex(externCursorPosition);

		InternToken cursorPositionToken = InternFormulaToInternTokenGenerator.generateInternTokenByIndex(
				cursorPositionTokenIndex, internalFormulaString);

		List<InternToken> catKeyEventTokenList = catKeyEvent.createInternTokensByCatKeyEvent();

		if (cursorPositionToken == null) {
			InternToken firstLeftToken = getFirstLeftInternToken(externCursorPosition);
			appendToLeftToken(firstLeftToken, cursorPositionTokenIndex, externCursorPosition, catKeyEventTokenList);

		} else {
			if (cursorPositionToken.isNumber() && InternToken.isNumberToken(catKeyEventTokenList)) {
				//TODO handle PERIOD(Comma)
				insertNumberIntoNumberToken(cursorPositionToken, cursorPositionTokenIndex, externCursorPosition,
						catKeyEvent.getDisplayLabelString());
			} else {
				replaceInternTokenByTokenList(cursorPositionToken, cursorPositionTokenIndex, catKeyEventTokenList);
			}
		}

	}

	//TODO move to correct position
	private void insertNumberIntoNumberToken(InternToken numberTokenToBeModified, int cursorPositionTokenIndex,
			int externCursorPosition, String numberToInsert) {

		int externNumberCursorIndex = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
				cursorPositionTokenIndex);

		String numberString = numberTokenToBeModified.getTokenSringValue();
		numberString = numberString.substring(INTERN_TOKEN_PREFIX_NUMBER.length());
		String leftPart = numberString.substring(0, externNumberCursorIndex);
		String rightPart = numberString.substring(externNumberCursorIndex);

		numberTokenToBeModified.setTokenStringValue(leftPart + numberToInsert + rightPart);

	}

	private void appendToLeftToken(InternToken firstLeftToken, int firstLeftTokenInternIndex, int externCursorPosition,
			List<InternToken> internTokensToAppend) {

		if (firstLeftToken == null) {
			internalFormulaString = InternFormulaStringModify.generateInternStringByInsertAtBeginning(
					internTokensToAppend, internalFormulaString);

		} else if (firstLeftToken.isNumber() && InternToken.isNumberToken(internTokensToAppend)) {
			firstLeftToken.appendToTokenStringValue(internTokensToAppend);
			internalFormulaString = InternFormulaStringModify.generateInternStringByReplace(firstLeftTokenInternIndex,
					firstLeftToken, internalFormulaString);
		} else if (firstLeftToken.isNumber() && InternToken.isPeriodToken(internTokensToAppend)) {
			String numberString = firstLeftToken.getTokenSringValue();
			if (numberString.contains(".")) //TODO Hardcoded period, may search for better solution
			{
				return;
			}
			firstLeftToken.appendToTokenStringValue("."); //TODO Hardcoded period, may search for better solution
			internalFormulaString = InternFormulaStringModify.generateInternStringByReplace(firstLeftTokenInternIndex,
					firstLeftToken, internalFormulaString);
		}

		//		int indexToInsert = externInternRepresentationMapping.indexOf(firstLeftToken);
		//
		//		if (indexToInsert == -1) {
		//			return;
		//		}
		//
		//		if (firstLeftToken.isNumber() && catKeyEvent.isNumber()) {
		//			firstLeftToken.appendToTokenStringValue(catKeyEvent.getDisplayLabelString());
		//			InternFormulaStringModify
		//		} else {
		//			internTokenList.addAll(indexToInsert, catKeyEvent.createInternTokensByCatKeyEvent());
		//		}

	}

	private void insertInternTokenByCatKeyEvent(int internTokenListIndex, CatKeyEvent catKeyEvent) {
		//		List<InternToken> internTokensToInsert = catKeyEvent.createInternTokensByCatKeyEvent();
		//
		//		internTokenList.addAll(internTokenListIndex, internTokensToInsert);

		//TODO GOON here

	}

	private void replaceInternTokenByTokenList(InternToken internTokenToReplace, int internTokenToReplaceIndex,
			List<InternToken> internTokensToReplaceWith) {

		if (internTokenToReplace.getInternTokenType() == InternTokenType.NUMBER
				&& InternToken.isFunctionToken(internTokensToReplaceWith)) {
			//When NUMBER selected
			//  set Number to first parameter when FUNCTION inserted

		} else if (internTokenToReplace.getInternTokenType() == InternTokenType.FUNCTION_NAME
				|| internTokenToReplace.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN
				|| internTokenToReplace.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {

			List<InternToken> functionInternTokens = InternFormulaToInternTokenGenerator
					.generateInternTokenListByFunctionIndex(internTokenToReplaceIndex, internalFormulaString);

			if (functionInternTokens.size() == 0) {
				return;
			}

			int lastListIndex = functionInternTokens.size() - 1;
			InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);
			int endIndexToReplace = lastFunctionToken.getInternPositionIndex();

			List<InternToken> replacedFunctionTokens = InternTokenModify.replaceFunctionByTokens(functionInternTokens,
					internTokensToReplaceWith);

			internalFormulaString = InternFormulaStringModify.generateInternStringByReplace(internTokenToReplaceIndex,
					endIndexToReplace, replacedFunctionTokens, internalFormulaString);

		} else if (InternToken.isFunctionToken(internTokensToReplaceWith)) {
			//TODO: handle single token value replaced by function
		} else {

			internalFormulaString = InternFormulaStringModify.generateInternStringByReplace(internTokenToReplaceIndex,
					internTokensToReplaceWith, internalFormulaString);
		}

	}

	public InternToken getFirstLeftInternToken(int externIndex) {
		for (int searchIndex = externIndex; searchIndex >= 0; searchIndex--) {
			if (externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex) != null) {
				int internTokenIndex = externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex);
				InternToken internTokenToReturn = InternFormulaToInternTokenGenerator.generateInternTokenByIndex(
						internTokenIndex, internalFormulaString);
				internTokenToReturn.setInternPositionIndex(searchIndex);
				return internTokenToReturn;
			}
		}

		return null;
	}

}
