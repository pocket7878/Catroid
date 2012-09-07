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

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

public class InternFormula {

	private ExternInternRepresentationMapping externInternRepresentationMapping;
	private InternToExternGenerator internToExternGenerator;

	private String internalFormulaString;
	private String externFormulaString;

	private boolean tokenSelection;
	private int externCursorPosition;
	private InternToken cursorPositionInternToken;

	public InternFormula(String internalFormulaString, Context context) {
		this.internalFormulaString = internalFormulaString;
		externFormulaString = null;
		internToExternGenerator = new InternToExternGenerator(context);
		externInternRepresentationMapping = new ExternInternRepresentationMapping();
		tokenSelection = false;
		externCursorPosition = 0;
	}

	public void setCursorAndSelection(int externCursorPosition, boolean tokenIsSelected) {
		this.tokenSelection = tokenIsSelected;
		this.externCursorPosition = externCursorPosition;
		Integer cursorPositionTokenIndex = externInternRepresentationMapping
				.getInternTokenByExternIndex(externCursorPosition);

		if (cursorPositionTokenIndex == null) {
			this.cursorPositionInternToken = null;
		} else {
			this.cursorPositionInternToken = InternFormulaToInternTokenGenerator.generateInternTokenByIndex(
					cursorPositionTokenIndex, internalFormulaString);

			Log.i("info",
					"cursorPositionInternToken = "
							+ cursorPositionInternToken.getInternTokenType().getInternTokenPrefix()
							+ cursorPositionInternToken.getTokenSringValue());
		}
	}

	public String getExternFormulaString() {
		Log.i("info", "Intern Formula = \"" + internalFormulaString + "\"");
		return externFormulaString;
	}

	public void handleKeyInput(CatKeyEvent catKeyEvent) {
		Log.i("info", "handleKeyInput:enter");

		List<InternToken> catKeyEventTokenList = catKeyEvent.createInternTokensByCatKeyEvent();

		//TODO handle deletion
		if (catKeyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {

		} else if (cursorPositionInternToken == null) {

			appendToFirstLeftToken(externCursorPosition, catKeyEventTokenList);

		} else if (tokenSelection) {

			replaceSelectionByTokenList(catKeyEventTokenList);

		} else {

			replaceCursorPositionInternTokenByTokenList(catKeyEventTokenList);

		}

		externFormulaString = null;

	}

	public void generateExternFormulaStringAndInternExternMapping() {
		Log.i("info", "generateExternFormulaStringAndInternExternMapping:enter");
		internToExternGenerator.generateExternStringAndMapping(internalFormulaString);
		externFormulaString = internToExternGenerator.getGeneratedExternFormulaString();
		externInternRepresentationMapping = internToExternGenerator.getGeneratedExternInternRepresentationMapping();

		setCursorAndSelection(externCursorPosition, tokenSelection); //TODO refactor

	}

	private void appendToFirstLeftToken(int externCursorPosition, List<InternToken> internTokensToAppend) {

		Log.i("info", "appendToLeftToken:enter");

		InternToken firstLeftToken = getFirstLeftInternToken(externCursorPosition);

		if (firstLeftToken == null) {
			internalFormulaString = InternFormulaStringModify.generateInternStringByInsertAtBeginning(
					internTokensToAppend, internalFormulaString);

		} else if (firstLeftToken.isNumber() && InternToken.isNumberToken(internTokensToAppend)) {
			firstLeftToken.appendToTokenStringValue(internTokensToAppend);
			internalFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					firstLeftToken.getInternPositionIndex(), firstLeftToken, internalFormulaString);
		} else if (firstLeftToken.isNumber() && InternToken.isPeriodToken(internTokensToAppend)) {
			String numberString = firstLeftToken.getTokenSringValue();
			if (numberString.contains(".")) //TODO Hardcoded period, may search for better solution
			{
				return;
			}
			firstLeftToken.appendToTokenStringValue("."); //TODO Hardcoded period, may search for better solution
			internalFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					firstLeftToken.getInternPositionIndex(), firstLeftToken, internalFormulaString);
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

	private void replaceSelectionByTokenList(List<InternToken> internTokensToReplaceWith) {
		//TODO implement selection replace
	}

	private void replaceCursorPositionInternTokenByTokenList(List<InternToken> internTokensToReplaceWith) {

		Log.i("info", "replaceCursorPositionInternTokenByTokenList:enter");
		int internTokenToReplaceIndex = cursorPositionInternToken.getInternPositionIndex();

		if (cursorPositionInternToken.getInternTokenType() == InternTokenType.NUMBER
				&& InternToken.isNumberToken(internTokensToReplaceWith)) {

			InternToken numberTokenToInsert = internTokensToReplaceWith.get(0);

			int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
					internTokenToReplaceIndex);

			if (externNumberOffset == -1) {
				return;
			}

			InternToken modifiedToken = InternTokenModify.insertNumberIntoNumberToken(cursorPositionInternToken,
					externNumberOffset, numberTokenToInsert.getTokenSringValue());

			internalFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					cursorPositionInternToken.getInternPositionIndex(), modifiedToken, internalFormulaString);

			Log.i("info",
					"replaceCursorPositionInternTokenByTokenList: modifiedToken = "
							+ modifiedToken.getTokenSringValue());

			Log.i("info", "replaceCursorPositionInternTokenByTokenList: generated internalFormulaString = "
					+ internalFormulaString);

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.NUMBER
				&& InternToken.isFunctionToken(internTokensToReplaceWith)) {
			//When NUMBER selected
			//  set Number to first parameter when FUNCTION inserted

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.FUNCTION_NAME
				|| cursorPositionInternToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN
				|| cursorPositionInternToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {

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
