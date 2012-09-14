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

	public static enum CursorTokenPosition {
		LEFT, MIDDLE, RIGHT;
	};

	private ExternInternRepresentationMapping externInternRepresentationMapping;
	private InternToExternGenerator internToExternGenerator;

	private String internFormulaString;
	private String externFormulaString;

	private boolean tokenSelection;
	private int internTokenSelectionStart;
	private int internTokenSelectionEnd;
	private int externCursorPosition;

	private InternToken cursorPositionInternToken;
	private CursorTokenPosition cursorTokenPosition;

	public InternFormula(String internalFormulaString, Context context) {
		this.internFormulaString = internalFormulaString;
		externFormulaString = null;
		internToExternGenerator = new InternToExternGenerator(context);
		externInternRepresentationMapping = new ExternInternRepresentationMapping();
		tokenSelection = false;
		externCursorPosition = 0;
		internTokenSelectionEnd = -1;
		internTokenSelectionStart = -1;
	}

	public synchronized void setCursorAndSelection(int externCursorPosition, boolean tokenIsSelected) {
		this.tokenSelection = tokenIsSelected;
		this.externCursorPosition = externCursorPosition;

		Log.i("info", "setCursorAndSelection: externCursorPosition = " + externCursorPosition);

		Integer cursorPositionTokenIndex = externInternRepresentationMapping
				.getInternTokenByExternIndex(externCursorPosition);

		Integer leftCursorPositionTokenIndex = externInternRepresentationMapping
				.getInternTokenByExternIndex(externCursorPosition - 1);

		if (cursorPositionTokenIndex != null) {
			if (leftCursorPositionTokenIndex != null) {
				if (cursorPositionTokenIndex.equals(leftCursorPositionTokenIndex)) {
					cursorTokenPosition = CursorTokenPosition.MIDDLE;
				} else {
					cursorTokenPosition = CursorTokenPosition.LEFT;
				}
			} else {
				cursorTokenPosition = CursorTokenPosition.LEFT;
			}
		} else if (leftCursorPositionTokenIndex != null) {
			cursorTokenPosition = CursorTokenPosition.RIGHT;

		} else {
			cursorTokenPosition = null;
			this.cursorPositionInternToken = null;
			return; //TODO check if necessary
		}

		switch (cursorTokenPosition) {
			case LEFT:
				this.cursorPositionInternToken = InternFormulaToInternTokenGenerator.generateInternTokenByIndex(
						cursorPositionTokenIndex, internFormulaString);
				Log.i("info", "LEFT of " + cursorPositionInternToken.getTokenSringValue());
				break;
			case MIDDLE:
				this.cursorPositionInternToken = InternFormulaToInternTokenGenerator.generateInternTokenByIndex(
						cursorPositionTokenIndex, internFormulaString);
				Log.i("info", "SELECTED " + cursorPositionInternToken.getTokenSringValue());
				break;
			case RIGHT:
				this.cursorPositionInternToken = InternFormulaToInternTokenGenerator.generateInternTokenByIndex(
						leftCursorPositionTokenIndex, internFormulaString);
				Log.i("info", "RIGHT of " + cursorPositionInternToken.getTokenSringValue());
				break;

		}

		if (tokenIsSelected) {
			selectCursorPositionInternToken();
		} else {
			internTokenSelectionEnd = -1;
			internTokenSelectionStart = -1;
		}

	}

	public int getExternSelectionStartIndex() {
		if (tokenSelection == false) {
			return -1;
		}

		Integer externSelectionStartIndex = externInternRepresentationMapping
				.getExternTokenStartIndex(internTokenSelectionStart);

		if (externSelectionStartIndex == null) {
			return -1;
		}

		return externSelectionStartIndex;
	}

	public int getExternSelectionEndIndex() {
		if (tokenSelection == false) {
			return -1;
		}

		Integer externSelectionEndIndex = externInternRepresentationMapping
				.getExternTokenEndIndex(internTokenSelectionEnd);

		if (externSelectionEndIndex == null) {
			return -1;
		}

		return externSelectionEndIndex;
	}

	public String getExternFormulaString() {
		Log.i("info", "Intern Formula = \"" + internFormulaString + "\"");
		Log.i("info", "Extern Formula = \"" + externFormulaString + "\"");
		return externFormulaString;
	}

	public synchronized void handleKeyInput(CatKeyEvent catKeyEvent) {
		Log.i("info", "handleKeyInput:enter");

		List<InternToken> catKeyEventTokenList = catKeyEvent.createInternTokensByCatKeyEvent();

		if (catKeyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {

			handleDeletion();

		} else if (tokenSelection) {
			replaceSelection(catKeyEventTokenList);
			tokenSelection = false;
			internTokenSelectionStart = -1;
			internTokenSelectionEnd = -1;
		} else if (cursorTokenPosition == null) {

			appendToFirstLeftToken(catKeyEventTokenList);

		} else if (cursorTokenPosition == CursorTokenPosition.LEFT) {

			insertLeftToCurrentToken(catKeyEventTokenList);

		} else if (cursorTokenPosition == CursorTokenPosition.MIDDLE) {

			replaceCursorPositionInternTokenByTokenList(catKeyEventTokenList);

		} else if (cursorTokenPosition == CursorTokenPosition.RIGHT) {

			insertRightToCurrentToken(catKeyEventTokenList);

		} else {

			//			appendToFirstLeftToken(catKeyEventTokenList);

		}

		externFormulaString = null;

	}

	private void replaceSelection(List<InternToken> tokenList) {

		if (internTokenSelectionStart > internTokenSelectionEnd || internTokenSelectionStart < 0
				|| internTokenSelectionEnd < 0) {
			return;
		}

		internFormulaString = InternFormulaStringModify.generateInternStringByReplace(internTokenSelectionStart,
				internTokenSelectionEnd, tokenList, internFormulaString);

	}

	private void handleDeletion() {
		if (tokenSelection) {
			internFormulaString = InternFormulaStringModify.generateInternStringByDelete(internTokenSelectionStart,
					internTokenSelectionEnd, internFormulaString);
			tokenSelection = false;
			internTokenSelectionStart = -1;
			internTokenSelectionEnd = -1;
		} else if (cursorTokenPosition == null) {

			InternToken firstLeftInternToken = getFirstLeftInternToken(externCursorPosition);

			if (firstLeftInternToken == null) {
				return;
			}

			deleteInternTokenByIndex(firstLeftInternToken.getInternPositionIndex());

		} else if (cursorTokenPosition == CursorTokenPosition.LEFT) {

			InternToken firstLeftInternToken = getFirstLeftInternToken(externCursorPosition - 1);

			if (firstLeftInternToken == null) {
				return;
			}

			deleteInternTokenByIndex(firstLeftInternToken.getInternPositionIndex());

		} else if (cursorTokenPosition == CursorTokenPosition.MIDDLE) {

			deleteInternTokenByIndex(cursorPositionInternToken.getInternPositionIndex());

		} else if (cursorTokenPosition == CursorTokenPosition.RIGHT) {

			deleteInternTokenByIndex(cursorPositionInternToken.getInternPositionIndex());

		}

	}

	private void deleteInternTokenByIndex(int internTokenIndex) {

		Log.i("info", "deleteInternTokenByIndex:enter");

		InternToken tokenToDelete = InternFormulaToInternTokenGenerator.generateInternTokenByIndex(internTokenIndex,
				internFormulaString);

		switch (tokenToDelete.getInternTokenType()) {
			case NUMBER:
				int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(
						externCursorPosition, internTokenIndex);

				Log.i("info", "Delete number offset = " + externNumberOffset);

				if (externNumberOffset == -1) {
					return;
				}

				InternToken modifiedToken = InternTokenModify.deleteNumberByOffset(tokenToDelete, externNumberOffset);

				if (modifiedToken == null) {
					Log.i("info", "deleteInternTokenByIndex: Numer modifiedToken = NULL");
					internFormulaString = InternFormulaStringModify.generateInternStringByDelete(
							tokenToDelete.getInternPositionIndex(), internFormulaString);

				} else {
					Log.i("info", "deleteInternTokenByIndex: modifiedToken = " + modifiedToken.toString());
					internFormulaString = InternFormulaStringModify.generateInternStringByReplace(
							tokenToDelete.getInternPositionIndex(), modifiedToken, internFormulaString);
				}
				externCursorPosition--;
				break;

			case FUNCTION_NAME:
				List<InternToken> functionInternTokens = InternFormulaToInternTokenGenerator
						.generateInternTokenListByFunctionIndex(internTokenIndex, internFormulaString);

				if (functionInternTokens.size() == 0) {
					return;
				}

				int lastListIndex = functionInternTokens.size() - 1;
				InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);
				int endIndexToDelete = lastFunctionToken.getInternPositionIndex();

				internFormulaString = InternFormulaStringModify.generateInternStringByDelete(internTokenIndex,
						endIndexToDelete, internFormulaString);
				break;

			case FUNCTION_PARAMETERS_BRACKET_OPEN:
				functionInternTokens = InternFormulaToInternTokenGenerator
						.generateInternTokenListByFunctionBracketOpen(tokenToDelete.getInternPositionIndex(),
								internFormulaString);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				int functionInternTokensLastIndex = functionInternTokens.size() - 1;

				int startDeletionIndex = functionInternTokens.get(0).getInternPositionIndex();
				int endDeletionIndex = functionInternTokens.get(functionInternTokensLastIndex).getInternPositionIndex();

				internFormulaString = InternFormulaStringModify.generateInternStringByDelete(startDeletionIndex,
						endDeletionIndex, internFormulaString);

				break;
			case FUNCTION_PARAMETERS_BRACKET_CLOSE:
				functionInternTokens = InternFormulaToInternTokenGenerator
						.generateInternTokenListByFunctionBracketClose(tokenToDelete.getInternPositionIndex(),
								internFormulaString);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startDeletionIndex = functionInternTokens.get(0).getInternPositionIndex();
				endDeletionIndex = functionInternTokens.get(functionInternTokensLastIndex).getInternPositionIndex();

				internFormulaString = InternFormulaStringModify.generateInternStringByDelete(startDeletionIndex,
						endDeletionIndex, internFormulaString);

				break;
			case FUNCTION_PARAMETER_DELIMITER:
				functionInternTokens = InternFormulaToInternTokenGenerator
						.generateInternTokenListByFunctionParameterDelimiter(tokenToDelete.getInternPositionIndex(),
								internFormulaString);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startDeletionIndex = functionInternTokens.get(0).getInternPositionIndex();
				endDeletionIndex = functionInternTokens.get(functionInternTokensLastIndex).getInternPositionIndex();

				internFormulaString = InternFormulaStringModify.generateInternStringByDelete(startDeletionIndex,
						endDeletionIndex, internFormulaString);

				break;
			case BRACKET_OPEN:
				List<InternToken> bracketsInternTokens = InternFormulaToInternTokenGenerator
						.generateTokenListByBracketOpen(tokenToDelete.getInternPositionIndex(), internFormulaString);

				if (bracketsInternTokens == null || bracketsInternTokens.size() == 0) {
					return;
				}

				int bracketsInternTokensLastIndex = bracketsInternTokens.size() - 1;

				startDeletionIndex = bracketsInternTokens.get(0).getInternPositionIndex();
				endDeletionIndex = bracketsInternTokens.get(bracketsInternTokensLastIndex).getInternPositionIndex();

				internFormulaString = InternFormulaStringModify.generateInternStringByDelete(startDeletionIndex,
						endDeletionIndex, internFormulaString);

				break;

			case BRACKET_CLOSE:

				bracketsInternTokens = InternFormulaToInternTokenGenerator.generateTokenListByBracketClose(
						tokenToDelete.getInternPositionIndex(), internFormulaString);

				if (bracketsInternTokens == null || bracketsInternTokens.size() == 0) {
					return;
				}

				bracketsInternTokensLastIndex = bracketsInternTokens.size() - 1;

				startDeletionIndex = bracketsInternTokens.get(0).getInternPositionIndex();
				endDeletionIndex = bracketsInternTokens.get(bracketsInternTokensLastIndex).getInternPositionIndex();

				internFormulaString = InternFormulaStringModify.generateInternStringByDelete(startDeletionIndex,
						endDeletionIndex, internFormulaString);

				break;
			default:
				internFormulaString = InternFormulaStringModify.generateInternStringByDelete(internTokenIndex,
						internFormulaString);
				break;
		}

		Log.i("info", "deleteInternTokenByIndex: resulting internFormulaString = " + internFormulaString);
	}

	public void generateExternFormulaStringAndInternExternMapping() {
		Log.i("info", "generateExternFormulaStringAndInternExternMapping:enter");
		internToExternGenerator.generateExternStringAndMapping(internFormulaString);
		externFormulaString = internToExternGenerator.getGeneratedExternFormulaString();
		externInternRepresentationMapping = internToExternGenerator.getGeneratedExternInternRepresentationMapping();

		setCursorAndSelection(externCursorPosition, tokenSelection); //TODO refactor

	}

	private void selectCursorPositionInternToken() {
		switch (cursorPositionInternToken.getInternTokenType()) {
			case FUNCTION_NAME:
				List<InternToken> functionInternTokens = InternFormulaToInternTokenGenerator
						.generateInternTokenListByFunctionIndex(cursorPositionInternToken.getInternPositionIndex(),
								internFormulaString);

				if (functionInternTokens.size() == 0) {
					return;
				}

				int lastListIndex = functionInternTokens.size() - 1;
				InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);

				int endSelectionIndex = lastFunctionToken.getInternPositionIndex();

				this.internTokenSelectionStart = cursorPositionInternToken.getInternPositionIndex();
				this.internTokenSelectionEnd = endSelectionIndex;

				break;
			case FUNCTION_PARAMETERS_BRACKET_OPEN:

				functionInternTokens = InternFormulaToInternTokenGenerator
						.generateInternTokenListByFunctionBracketOpen(
								cursorPositionInternToken.getInternPositionIndex(), internFormulaString);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				int functionInternTokensLastIndex = functionInternTokens.size() - 1;

				int startSelectionIndex = functionInternTokens.get(0).getInternPositionIndex();
				endSelectionIndex = functionInternTokens.get(functionInternTokensLastIndex).getInternPositionIndex();

				this.internTokenSelectionStart = startSelectionIndex;
				this.internTokenSelectionEnd = endSelectionIndex;

				break;
			case FUNCTION_PARAMETERS_BRACKET_CLOSE:
				functionInternTokens = InternFormulaToInternTokenGenerator
						.generateInternTokenListByFunctionBracketClose(
								cursorPositionInternToken.getInternPositionIndex(), internFormulaString);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startSelectionIndex = functionInternTokens.get(0).getInternPositionIndex();
				endSelectionIndex = functionInternTokens.get(functionInternTokensLastIndex).getInternPositionIndex();

				this.internTokenSelectionStart = startSelectionIndex;
				this.internTokenSelectionEnd = endSelectionIndex;
				break;

			case FUNCTION_PARAMETER_DELIMITER:
				functionInternTokens = InternFormulaToInternTokenGenerator
						.generateInternTokenListByFunctionParameterDelimiter(
								cursorPositionInternToken.getInternPositionIndex(), internFormulaString);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startSelectionIndex = functionInternTokens.get(0).getInternPositionIndex();
				endSelectionIndex = functionInternTokens.get(functionInternTokensLastIndex).getInternPositionIndex();

				this.internTokenSelectionStart = startSelectionIndex;
				this.internTokenSelectionEnd = endSelectionIndex;

				break;

			case BRACKET_OPEN:
				List<InternToken> bracketsInternTokens = InternFormulaToInternTokenGenerator
						.generateTokenListByBracketOpen(cursorPositionInternToken.getInternPositionIndex(),
								internFormulaString);

				if (bracketsInternTokens == null || bracketsInternTokens.size() == 0) {
					return;
				}

				int bracketsInternTokensLastIndex = bracketsInternTokens.size() - 1;

				startSelectionIndex = bracketsInternTokens.get(0).getInternPositionIndex();
				endSelectionIndex = bracketsInternTokens.get(bracketsInternTokensLastIndex).getInternPositionIndex();

				this.internTokenSelectionStart = startSelectionIndex;
				this.internTokenSelectionEnd = endSelectionIndex;

				break;

			case BRACKET_CLOSE:

				bracketsInternTokens = InternFormulaToInternTokenGenerator.generateTokenListByBracketClose(
						cursorPositionInternToken.getInternPositionIndex(), internFormulaString);

				if (bracketsInternTokens == null || bracketsInternTokens.size() == 0) {
					return;
				}

				bracketsInternTokensLastIndex = bracketsInternTokens.size() - 1;

				startSelectionIndex = bracketsInternTokens.get(0).getInternPositionIndex();
				endSelectionIndex = bracketsInternTokens.get(bracketsInternTokensLastIndex).getInternPositionIndex();

				this.internTokenSelectionStart = startSelectionIndex;
				this.internTokenSelectionEnd = endSelectionIndex;

				break;

			default:
				this.internTokenSelectionStart = cursorPositionInternToken.getInternPositionIndex();
				this.internTokenSelectionEnd = cursorPositionInternToken.getInternPositionIndex();
				break;
		}

	}

	private void insertLeftToCurrentToken(List<InternToken> internTokensToInsert) {
		Log.i("info", "insertLeftToCurrentToken:enter");

		if (cursorPositionInternToken.isNumber() && InternToken.isNumberToken(internTokensToInsert)) {

			String numberToInsert = internTokensToInsert.get(0).getTokenSringValue();

			InternToken modifiedInternTokenNumber = InternTokenModify.insertIntoNumberToken(cursorPositionInternToken,
					0, numberToInsert);

			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					cursorPositionInternToken.getInternPositionIndex(), modifiedInternTokenNumber, internFormulaString);

			externCursorPosition++;

		} else if (cursorPositionInternToken.isNumber() && InternToken.isPeriodToken(internTokensToInsert)) {
			String numberString = cursorPositionInternToken.getTokenSringValue();
			if (numberString.contains(".")) //TODO Hardcoded period, may search for better solution
			{
				return;
			}

			InternToken modifiedInternTokenNumber = InternTokenModify.insertIntoNumberToken(cursorPositionInternToken,
					0, "."); //TODO Hardcoded period, may search for better solution

			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					cursorPositionInternToken.getInternPositionIndex(), modifiedInternTokenNumber, internFormulaString);

			externCursorPosition++;

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
			replaceCursorPositionInternTokenByTokenList(internTokensToInsert);
		} else if (InternToken.isPeriodToken(internTokensToInsert)) {
			return; //TODO Find better period solution
		} else {
			internFormulaString = InternFormulaStringModify.generateInternStringByPrepend(cursorPositionInternToken,
					internTokensToInsert, internFormulaString);

			externCursorPosition++;
		}
	}

	private void insertRightToCurrentToken(List<InternToken> internTokensToInsert) {
		Log.i("info", "insertRightToCurrentToken:enter");

		if (cursorPositionInternToken.isNumber() && InternToken.isNumberToken(internTokensToInsert)) {

			cursorPositionInternToken.appendToTokenStringValue(internTokensToInsert);

			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					cursorPositionInternToken.getInternPositionIndex(), cursorPositionInternToken, internFormulaString);

			externCursorPosition++;

		} else if (cursorPositionInternToken.isNumber() && InternToken.isPeriodToken(internTokensToInsert)) {
			String numberString = cursorPositionInternToken.getTokenSringValue();
			if (numberString.contains(".")) //TODO Hardcoded period, may search for better solution
			{
				return;
			}
			cursorPositionInternToken.appendToTokenStringValue("."); //TODO Hardcoded period, may search for better solution
			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					cursorPositionInternToken.getInternPositionIndex(), cursorPositionInternToken, internFormulaString);
			externCursorPosition++;

		} else if (InternToken.isPeriodToken(internTokensToInsert)) {
			return; //TODO Find better period solution
		} else {
			internFormulaString = InternFormulaStringModify.generateInternStringByAppend(cursorPositionInternToken,
					internTokensToInsert, internFormulaString);

			externCursorPosition++;
		}
	}

	private void appendToFirstLeftToken(List<InternToken> internTokensToAppend) {

		Log.i("info", "appendToFirstLeftToken:enter");

		InternToken firstLeftToken = getFirstLeftInternToken(externCursorPosition);

		if (firstLeftToken == null) {
			internFormulaString = InternFormulaStringModify.generateInternStringByInsertAtBeginning(
					internTokensToAppend, internFormulaString);

			externCursorPosition++;

		} else if (firstLeftToken.isNumber() && InternToken.isNumberToken(internTokensToAppend)) {

			firstLeftToken.appendToTokenStringValue(internTokensToAppend);

			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					firstLeftToken.getInternPositionIndex(), firstLeftToken, internFormulaString);

			externCursorPosition++;

		} else if (firstLeftToken.isNumber() && InternToken.isPeriodToken(internTokensToAppend)) {
			String numberString = firstLeftToken.getTokenSringValue();
			if (numberString.contains(".")) //TODO Hardcoded period, may search for better solution
			{
				return;
			}
			firstLeftToken.appendToTokenStringValue("."); //TODO Hardcoded period, may search for better solution
			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					firstLeftToken.getInternPositionIndex(), firstLeftToken, internFormulaString);
			externCursorPosition++;

		} else if (InternToken.isPeriodToken(internTokensToAppend)) {
			return; //TODO Find better period solution
		} else {
			internFormulaString = InternFormulaStringModify.generateInternStringByAppend(firstLeftToken,
					internTokensToAppend, internFormulaString);

			externCursorPosition++;
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

	private void replaceCursorPositionInternTokenByTokenList(List<InternToken> internTokensToReplaceWith) {

		Log.i("info", "replaceCursorPositionInternTokenByTokenList:enter");
		int internTokenToReplaceIndex = cursorPositionInternToken.getInternPositionIndex();

		if (cursorPositionInternToken.getInternTokenType() == InternTokenType.NUMBER
				&& InternToken.isNumberToken(internTokensToReplaceWith)) {

			InternToken numberTokenToInsert = internTokensToReplaceWith.get(0);

			int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
					internTokenToReplaceIndex);

			Log.i("info", "replaceCursorPositionInternTokenByTokenList: externCursorPosition = " + externCursorPosition);

			Log.i("info", "replaceCursorPositionInternTokenByTokenList: externNumberOffset = " + externNumberOffset);

			if (externNumberOffset == -1) {
				return;
			}

			InternToken modifiedToken = InternTokenModify.insertIntoNumberToken(cursorPositionInternToken,
					externNumberOffset, numberTokenToInsert.getTokenSringValue());

			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					cursorPositionInternToken.getInternPositionIndex(), modifiedToken, internFormulaString);

			externCursorPosition++;

			Log.i("info",
					"replaceCursorPositionInternTokenByTokenList: modifiedToken = "
							+ modifiedToken.getTokenSringValue());

			Log.i("info", "replaceCursorPositionInternTokenByTokenList: generated internalFormulaString = "
					+ internFormulaString);

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.NUMBER
				&& InternToken.isPeriodToken(internTokensToReplaceWith)) {

			int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
					internTokenToReplaceIndex);

			Log.i("info", "replaceCursorPositionInternTokenByTokenList: externCursorPosition = " + externCursorPosition);

			Log.i("info", "replaceCursorPositionInternTokenByTokenList: externNumberOffset = " + externNumberOffset);

			if (externNumberOffset == -1) {
				return;
			}

			InternToken modifiedToken = InternTokenModify.insertPeriodIntoNumberToken(cursorPositionInternToken,
					externNumberOffset);

			if (modifiedToken == null) {
				return;
			}

			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(
					cursorPositionInternToken.getInternPositionIndex(), modifiedToken, internFormulaString);

			externCursorPosition++;

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.NUMBER
				&& InternToken.isFunctionToken(internTokensToReplaceWith)) {
			//TODO: When NUMBER selected
			//  set Number to first parameter when FUNCTION inserted

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.FUNCTION_NAME) {

			List<InternToken> functionInternTokens = InternFormulaToInternTokenGenerator
					.generateInternTokenListByFunctionIndex(internTokenToReplaceIndex, internFormulaString);

			if (functionInternTokens.size() == 0) {
				return;
			}

			int lastListIndex = functionInternTokens.size() - 1;
			InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);
			int endIndexToReplace = lastFunctionToken.getInternPositionIndex();

			List<InternToken> replacedFunctionTokens = InternTokenModify.replaceFunctionByTokens(functionInternTokens,
					internTokensToReplaceWith);

			if (replacedFunctionTokens == null) {
				return;
			}

			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(internTokenToReplaceIndex,
					endIndexToReplace, replacedFunctionTokens, internFormulaString);

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {

			List<InternToken> functionInternTokens = InternFormulaToInternTokenGenerator
					.generateInternTokenListByFunctionBracketOpen(internTokenToReplaceIndex, internFormulaString);

			if (functionInternTokens.size() == 0) {
				return;
			}

			int lastListIndex = functionInternTokens.size() - 1;
			InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);

			int startInternIndexToReplace = functionInternTokens.get(0).getInternPositionIndex();
			int endInternIndexToReplace = lastFunctionToken.getInternPositionIndex();

			List<InternToken> replacedFunctionTokens = InternTokenModify.replaceFunctionByTokens(functionInternTokens,
					internTokensToReplaceWith);

			if (replacedFunctionTokens == null) {
				return;
			}

			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(startInternIndexToReplace,
					endInternIndexToReplace, replacedFunctionTokens, internFormulaString);

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
			//TODO implement
		} else if (InternToken.isFunctionToken(internTokensToReplaceWith)) {
			//TODO: handle single token value replaced by function
		} else if (InternToken.isPeriodToken(internTokensToReplaceWith)) {
			return; //TODO: Find better solution for period
		} else {

			internFormulaString = InternFormulaStringModify.generateInternStringByReplace(internTokenToReplaceIndex,
					internTokensToReplaceWith, internFormulaString);
		}

	}

	public InternToken getFirstLeftInternToken(int externIndex) {
		for (int searchIndex = externIndex; searchIndex >= 0; searchIndex--) {
			if (externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex) != null) {
				int internTokenIndex = externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex);
				InternToken internTokenToReturn = InternFormulaToInternTokenGenerator.generateInternTokenByIndex(
						internTokenIndex, internFormulaString);
				return internTokenToReturn;
			}
		}

		return null;
	}

	public int getExternCursorPosition() {

		return this.externCursorPosition;
	}

}
