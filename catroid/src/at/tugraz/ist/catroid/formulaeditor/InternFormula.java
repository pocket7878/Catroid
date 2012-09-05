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

		if (cursorPositionToken == null) {
			InternToken firstLeftToken = getFirstLeftInternToken(externCursorPosition);
			if (firstLeftToken == null) {
				insertInternTokenByCatKeyEvent(0, catKeyEvent);
			} else {
				appendInternTokenByKeyEvent(firstLeftToken, externCursorPosition, catKeyEvent);
			}
		} else {
			if (cursorPositionToken.isNumber() && catKeyEvent.isNumber()) {
				insertNumberIntoNumberToken(cursorPositionToken, cursorPositionTokenIndex, externCursorPosition,
						catKeyEvent.getDisplayLabelString());
			} else {
				replaceInternTokenByCatKeyEvent(cursorPositionToken, cursorPositionTokenIndex, catKeyEvent);
			}
		}

	}

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

	private void appendInternTokenByKeyEvent(InternToken firstLeftToken, int externCursorPosition,
			CatKeyEvent catKeyEvent) {

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

	private void replaceInternTokenByCatKeyEvent(InternToken internTokenToReplace, int internTokenToReplaceIndex,
			CatKeyEvent catKeyEvent) {

		if (internTokenToReplace.getInternTokenType() == InternTokenType.NUMBER && catKeyEvent.isFunction()) {
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
			int endIndexToReplace = lastFunctionToken.getExternPositionIndex();

			List<InternToken> replacedFunctionTokens = replaceFunctionByCatKeyEvent(functionInternTokens, catKeyEvent);

			internalFormulaString = InternFormulaStringModify.generateInternStringByReplace(internTokenToReplaceIndex,
					endIndexToReplace, replacedFunctionTokens, internalFormulaString);

		} else if (catKeyEvent.isFunction()) {
			//TODO: handle single token value replaced by function
		} else {
			List<InternToken> replacedTokens = catKeyEvent.createInternTokensByCatKeyEvent();
			internalFormulaString = InternFormulaStringModify.generateInternStringByReplace(internTokenToReplaceIndex,
					replacedTokens, internalFormulaString);
		}

	}

	public InternToken getFirstLeftInternToken(int externIndex) {
		for (int searchIndex = externIndex; searchIndex >= 0; searchIndex--) {
			if (externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex) != null) {
				int internTokenIndex = externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex);
				InternToken internTokenToReturn = InternFormulaToInternTokenGenerator.generateInternTokenByIndex(
						internTokenIndex, internalFormulaString);
				internTokenToReturn.setExternPositionIndex(searchIndex);
				return internTokenToReturn;
			}
		}

		return null;
	}

	private List<InternToken> replaceFunctionByCatKeyEvent(List<InternToken> functionToReplace, CatKeyEvent catKeyEvent) {

		if (catKeyEvent.isFunction()) {
			//TODO replace function with function
			//keep all parameters of the replaced function

		} else {

			return catKeyEvent.createInternTokensByCatKeyEvent();
		}

		return null;
	}

}
