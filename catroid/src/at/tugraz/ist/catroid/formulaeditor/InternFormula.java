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
import android.view.KeyEvent;

public class InternFormula {

	public static enum CursorTokenPosition {
		LEFT, MIDDLE, RIGHT;
	};

	private ExternInternRepresentationMapping externInternRepresentationMapping;
	private InternToExternGenerator internToExternGenerator;

	private List<InternToken> internTokenFormulaList;
	private String internFormulaString;
	private String externFormulaString;

	private boolean tokenSelection;
	private int internTokenSelectionStart;
	private int internTokenSelectionEnd;
	private int externCursorPosition;

	private InternToken cursorPositionInternToken;
	private int cursorPositionInternTokenIndex;
	private CursorTokenPosition cursorTokenPosition;

	public InternFormula(String internalFormulaString, Context context) {
		this.internFormulaString = internalFormulaString;
		internTokenFormulaList = new LinkedList<InternToken>();

		externFormulaString = null;
		internToExternGenerator = new InternToExternGenerator(context);
		externInternRepresentationMapping = new ExternInternRepresentationMapping();
		tokenSelection = false;
		externCursorPosition = 0;
		internTokenSelectionEnd = -1;
		internTokenSelectionStart = -1;
		cursorPositionInternTokenIndex = 0;

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
				this.cursorPositionInternToken = internTokenFormulaList.get(cursorPositionTokenIndex);
				this.cursorPositionInternTokenIndex = cursorPositionTokenIndex;
				Log.i("info", "LEFT of " + cursorPositionInternToken.getTokenSringValue());
				break;
			case MIDDLE:
				this.cursorPositionInternToken = internTokenFormulaList.get(cursorPositionTokenIndex);
				this.cursorPositionInternTokenIndex = cursorPositionTokenIndex;
				Log.i("info", "SELECTED " + cursorPositionInternToken.getTokenSringValue());
				break;
			case RIGHT:
				this.cursorPositionInternToken = internTokenFormulaList.get(leftCursorPositionTokenIndex);
				this.cursorPositionInternTokenIndex = leftCursorPositionTokenIndex;
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

	private void replaceSelection(List<InternToken> tokenListToInsert) {

		replaceInternTokens(tokenListToInsert, internTokenSelectionStart, internTokenSelectionEnd);

	}

	private void deleteInternTokens(int deleteIndexStart, int deleteIndexEnd) {
		List<InternToken> tokenListToInsert = new LinkedList<InternToken>();
		replaceInternTokens(tokenListToInsert, deleteIndexStart, deleteIndexEnd);
	}

	private void replaceInternTokens(List<InternToken> tokenListToInsert, int replaceIndexStart, int replaceIndexEnd) {
		if (replaceIndexStart > replaceIndexEnd || replaceIndexStart < 0 || replaceIndexEnd < 0) {
			return;
		}

		for (int tokensToRemove = replaceIndexEnd - replaceIndexStart; tokensToRemove >= 0; tokensToRemove--) {
			internTokenFormulaList.remove(replaceIndexStart);
		}

		internTokenFormulaList.addAll(replaceIndexStart, tokenListToInsert);
	}

	private void handleDeletion() {
		if (tokenSelection) {
			deleteInternTokens(internTokenSelectionStart, internTokenSelectionEnd);

			tokenSelection = false;
			internTokenSelectionStart = -1;
			internTokenSelectionEnd = -1;
		} else if (cursorTokenPosition == null) {

			InternToken firstLeftInternToken = getFirstLeftInternToken(externCursorPosition);

			if (firstLeftInternToken == null) {
				return;
			}

			int firstLeftInternTokenIndex = internTokenFormulaList.indexOf(firstLeftInternToken);

			deleteInternTokenByIndex(firstLeftInternTokenIndex);

		} else if (cursorTokenPosition == CursorTokenPosition.LEFT) {

			InternToken firstLeftInternToken = getFirstLeftInternToken(externCursorPosition - 1);

			if (firstLeftInternToken == null) {
				return;
			}

			int firstLeftInternTokenIndex = internTokenFormulaList.indexOf(firstLeftInternToken);

			deleteInternTokenByIndex(firstLeftInternTokenIndex);

		} else if (cursorTokenPosition == CursorTokenPosition.MIDDLE) {

			deleteInternTokenByIndex(cursorPositionInternTokenIndex);

		} else if (cursorTokenPosition == CursorTokenPosition.RIGHT) {

			deleteInternTokenByIndex(cursorPositionInternTokenIndex);

		}

	}

	private void deleteInternTokenByIndex(int internTokenIndex) {

		Log.i("info", "deleteInternTokenByIndex:enter");

		InternToken tokenToDelete = internTokenFormulaList.get(internTokenIndex);

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
					internTokenFormulaList.remove(internTokenIndex);
				}

				externCursorPosition--;
				break;

			case FUNCTION_NAME:
				List<InternToken> functionInternTokens = InternTokenGroups.getFunctionByName(internTokenFormulaList,
						internTokenIndex);

				if (functionInternTokens.size() == 0) {
					return;
				}

				int lastListIndex = functionInternTokens.size() - 1;
				InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);
				int endIndexToDelete = internTokenFormulaList.indexOf(lastFunctionToken);

				deleteInternTokens(internTokenIndex, endIndexToDelete);
				break;

			case FUNCTION_PARAMETERS_BRACKET_OPEN:
				functionInternTokens = InternTokenGroups.getFunctionByFunctionBracketOpen(internTokenFormulaList,
						internTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				int functionInternTokensLastIndex = functionInternTokens.size() - 1;

				int startDeletionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endIndexToDelete = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				deleteInternTokens(startDeletionIndex, endIndexToDelete);

				break;
			case FUNCTION_PARAMETERS_BRACKET_CLOSE:
				functionInternTokens = InternTokenGroups.getFunctionByFunctionBracketClose(internTokenFormulaList,
						internTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startDeletionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endIndexToDelete = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				deleteInternTokens(startDeletionIndex, endIndexToDelete);

				break;
			case FUNCTION_PARAMETER_DELIMITER:
				functionInternTokens = InternTokenGroups.getFunctionByParameterDelimiter(internTokenFormulaList,
						internTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startDeletionIndex = functionInternTokens.indexOf(functionInternTokens.get(0));
				endIndexToDelete = functionInternTokens
						.indexOf(functionInternTokens.get(functionInternTokensLastIndex));

				deleteInternTokens(startDeletionIndex, endIndexToDelete);

				break;
			case BRACKET_OPEN:
				List<InternToken> bracketsInternTokens = InternTokenGroups.generateTokenListByBracketOpen(
						internTokenFormulaList, internTokenIndex);

				if (bracketsInternTokens == null || bracketsInternTokens.size() == 0) {
					return;
				}

				int bracketsInternTokensLastIndex = bracketsInternTokens.size() - 1;

				endIndexToDelete = internTokenFormulaList.indexOf(bracketsInternTokens
						.get(bracketsInternTokensLastIndex));

				deleteInternTokens(internTokenIndex, endIndexToDelete);

				break;

			case BRACKET_CLOSE:

				bracketsInternTokens = InternTokenGroups.generateTokenListByBracketClose(internTokenFormulaList,
						internTokenIndex);

				if (bracketsInternTokens == null || bracketsInternTokens.size() == 0) {
					return;
				}

				bracketsInternTokensLastIndex = bracketsInternTokens.size() - 1;

				startDeletionIndex = internTokenFormulaList.indexOf(bracketsInternTokens.get(0));
				endIndexToDelete = internTokenFormulaList.indexOf(bracketsInternTokens
						.get(bracketsInternTokensLastIndex));

				deleteInternTokens(startDeletionIndex, endIndexToDelete);

				break;
			default:
				deleteInternTokens(internTokenIndex, internTokenIndex);
				break;
		}

		Log.i("info", "deleteInternTokenByIndex: resulting internFormulaString = " + internFormulaString);
	}

	public void generateExternFormulaStringAndInternExternMapping() {
		Log.i("info", "generateExternFormulaStringAndInternExternMapping:enter");
		internToExternGenerator.generateExternStringAndMapping(internTokenFormulaList);
		externFormulaString = internToExternGenerator.getGeneratedExternFormulaString();
		externInternRepresentationMapping = internToExternGenerator.getGeneratedExternInternRepresentationMapping();

		setCursorAndSelection(externCursorPosition, tokenSelection); //TODO refactor

	}

	private void selectCursorPositionInternToken() {
		switch (cursorPositionInternToken.getInternTokenType()) {
			case FUNCTION_NAME:
				List<InternToken> functionInternTokens = InternTokenGroups.getFunctionByName(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (functionInternTokens.size() == 0) {
					return;
				}

				int lastListIndex = functionInternTokens.size() - 1;
				InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);

				int endSelectionIndex = internTokenFormulaList.indexOf(lastFunctionToken);

				this.internTokenSelectionStart = cursorPositionInternTokenIndex;
				this.internTokenSelectionEnd = endSelectionIndex;

				break;
			case FUNCTION_PARAMETERS_BRACKET_OPEN:

				functionInternTokens = InternTokenGroups.getFunctionByFunctionBracketOpen(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				int functionInternTokensLastIndex = functionInternTokens.size() - 1;

				int startSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				this.internTokenSelectionStart = startSelectionIndex;
				this.internTokenSelectionEnd = endSelectionIndex;

				break;
			case FUNCTION_PARAMETERS_BRACKET_CLOSE:
				functionInternTokens = InternTokenGroups.getFunctionByFunctionBracketClose(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				this.internTokenSelectionStart = startSelectionIndex;
				this.internTokenSelectionEnd = endSelectionIndex;
				break;

			case FUNCTION_PARAMETER_DELIMITER:
				functionInternTokens = InternTokenGroups.getFunctionByParameterDelimiter(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				this.internTokenSelectionStart = startSelectionIndex;
				this.internTokenSelectionEnd = endSelectionIndex;

				break;

			case BRACKET_OPEN:
				List<InternToken> bracketsInternTokens = InternTokenGroups.generateTokenListByBracketOpen(
						internTokenFormulaList, cursorPositionInternTokenIndex);

				if (bracketsInternTokens == null || bracketsInternTokens.size() == 0) {
					return;
				}

				int bracketsInternTokensLastIndex = bracketsInternTokens.size() - 1;

				startSelectionIndex = cursorPositionInternTokenIndex;
				endSelectionIndex = internTokenFormulaList.indexOf(bracketsInternTokens
						.get(bracketsInternTokensLastIndex));

				this.internTokenSelectionStart = startSelectionIndex;
				this.internTokenSelectionEnd = endSelectionIndex;

				break;

			case BRACKET_CLOSE:

				bracketsInternTokens = InternTokenGroups.generateTokenListByBracketClose(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (bracketsInternTokens == null || bracketsInternTokens.size() == 0) {
					return;
				}

				bracketsInternTokensLastIndex = bracketsInternTokens.size() - 1;

				startSelectionIndex = internTokenFormulaList.indexOf(bracketsInternTokens.get(0));
				endSelectionIndex = internTokenFormulaList.indexOf(bracketsInternTokens
						.get(bracketsInternTokensLastIndex));

				this.internTokenSelectionStart = startSelectionIndex;
				this.internTokenSelectionEnd = endSelectionIndex;

				break;

			default:
				this.internTokenSelectionStart = cursorPositionInternTokenIndex;
				this.internTokenSelectionEnd = cursorPositionInternTokenIndex;
				break;
		}

	}

	private void insertLeftToCurrentToken(List<InternToken> internTokensToInsert) {
		Log.i("info", "insertLeftToCurrentToken:enter");

		if (cursorPositionInternToken.isNumber() && InternToken.isNumberToken(internTokensToInsert)) {

			String numberToInsert = internTokensToInsert.get(0).getTokenSringValue();

			InternTokenModify.insertIntoNumberToken(cursorPositionInternToken, 0, numberToInsert);

			externCursorPosition++;

		} else if (cursorPositionInternToken.isNumber() && InternToken.isPeriodToken(internTokensToInsert)) {
			String numberString = cursorPositionInternToken.getTokenSringValue();
			if (numberString.contains(".")) //TODO Hardcoded period, may search for better solution
			{
				return;
			}

			InternTokenModify.insertIntoNumberToken(cursorPositionInternToken, 0, "."); //TODO Hardcoded period, may search for better solution

			externCursorPosition++;

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
			replaceCursorPositionInternTokenByTokenList(internTokensToInsert);
		} else if (InternToken.isPeriodToken(internTokensToInsert)) {
			return; //TODO Find better period solution
		} else {

			internTokenFormulaList.addAll(cursorPositionInternTokenIndex, internTokensToInsert);

			externCursorPosition++;
		}
	}

	private void insertRightToCurrentToken(List<InternToken> internTokensToInsert) {
		Log.i("info", "insertRightToCurrentToken:enter");

		if (cursorPositionInternToken.isNumber() && InternToken.isNumberToken(internTokensToInsert)) {

			cursorPositionInternToken.appendToTokenStringValue(internTokensToInsert);

			externCursorPosition++;

		} else if (cursorPositionInternToken.isNumber() && InternToken.isPeriodToken(internTokensToInsert)) {
			String numberString = cursorPositionInternToken.getTokenSringValue();
			if (numberString.contains(".")) //TODO Hardcoded period, may search for better solution
			{
				return;
			}
			cursorPositionInternToken.appendToTokenStringValue("."); //TODO Hardcoded period, may search for better solution

			externCursorPosition++;

		} else if (InternToken.isPeriodToken(internTokensToInsert)) {
			return; //TODO Find better period solution
		} else {

			internTokenFormulaList.addAll(cursorPositionInternTokenIndex + 1, internTokensToInsert);

			externCursorPosition++;
		}
	}

	private void appendToFirstLeftToken(List<InternToken> internTokensToAppend) {

		Log.i("info", "appendToFirstLeftToken:enter");

		InternToken firstLeftToken = getFirstLeftInternToken(externCursorPosition);

		if (firstLeftToken == null) {

			internTokenFormulaList.addAll(0, internTokensToAppend);

			externCursorPosition++;

		} else if (firstLeftToken.isNumber() && InternToken.isNumberToken(internTokensToAppend)) {

			firstLeftToken.appendToTokenStringValue(internTokensToAppend);

			externCursorPosition++;

		} else if (firstLeftToken.isNumber() && InternToken.isPeriodToken(internTokensToAppend)) {
			String numberString = firstLeftToken.getTokenSringValue();
			if (numberString.contains(".")) //TODO Hardcoded period, may search for better solution
			{
				return;
			}
			firstLeftToken.appendToTokenStringValue("."); //TODO Hardcoded period, may search for better solution

			externCursorPosition++;

		} else if (InternToken.isPeriodToken(internTokensToAppend)) {
			return; //TODO Find better period solution
		} else {

			int firstLeftTokenListIndex = internTokenFormulaList.indexOf(firstLeftToken);

			internTokenFormulaList.addAll(firstLeftTokenListIndex + 1, internTokensToAppend);

			externCursorPosition++;
		}

	}

	private void replaceCursorPositionInternTokenByTokenList(List<InternToken> internTokensToReplaceWith) {

		Log.i("info", "replaceCursorPositionInternTokenByTokenList:enter");

		if (cursorPositionInternToken.isNumber() && InternToken.isNumberToken(internTokensToReplaceWith)) {

			InternToken numberTokenToInsert = internTokensToReplaceWith.get(0);

			int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
					cursorPositionInternTokenIndex);

			Log.i("info", "replaceCursorPositionInternTokenByTokenList: externCursorPosition = " + externCursorPosition);

			Log.i("info", "replaceCursorPositionInternTokenByTokenList: externNumberOffset = " + externNumberOffset);

			if (externNumberOffset == -1) {
				return;
			}

			InternTokenModify.insertIntoNumberToken(cursorPositionInternToken, externNumberOffset,
					numberTokenToInsert.getTokenSringValue());

			externCursorPosition++;

		} else if (cursorPositionInternToken.isNumber() && InternToken.isPeriodToken(internTokensToReplaceWith)) {

			int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
					cursorPositionInternTokenIndex);

			Log.i("info", "replaceCursorPositionInternTokenByTokenList: externCursorPosition = " + externCursorPosition);

			Log.i("info", "replaceCursorPositionInternTokenByTokenList: externNumberOffset = " + externNumberOffset);

			if (externNumberOffset == -1) {
				return;
			}

			InternTokenModify.insertPeriodIntoNumberToken(cursorPositionInternToken, externNumberOffset);

			externCursorPosition++;

		} else if (cursorPositionInternToken.isNumber() && InternToken.isFunctionToken(internTokensToReplaceWith)) {

			//TODO: When NUMBER selected
			//  set Number to first parameter when FUNCTION inserted

		} else if (cursorPositionInternToken.isFunctionName()) {

			List<InternToken> functionInternTokens = InternTokenGroups.getFunctionByName(internTokenFormulaList,
					cursorPositionInternTokenIndex);

			if (functionInternTokens.size() == 0) {
				return;
			}

			int lastListIndex = functionInternTokens.size() - 1;
			InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);
			int endIndexToReplace = internTokenFormulaList.indexOf(lastFunctionToken);

			List<InternToken> tokensToInsert = InternTokenModify.replaceFunctionByTokens(functionInternTokens,
					internTokensToReplaceWith);

			replaceInternTokens(tokensToInsert, cursorPositionInternTokenIndex, endIndexToReplace);

		} else if (cursorPositionInternToken.isFunctionParameterBracketOpen()) {

			List<InternToken> functionInternTokens = InternTokenGroups.getFunctionByFunctionBracketOpen(
					internTokenFormulaList, cursorPositionInternTokenIndex);

			if (functionInternTokens.size() == 0) {
				return;
			}

			int lastListIndex = functionInternTokens.size() - 1;
			InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);

			int startInternIndexToReplace = internTokenFormulaList.indexOf(functionInternTokens.get(0));
			int endIndexToReplace = internTokenFormulaList.indexOf(lastFunctionToken);

			List<InternToken> tokensToInsert = InternTokenModify.replaceFunctionByTokens(functionInternTokens,
					internTokensToReplaceWith);

			replaceInternTokens(tokensToInsert, startInternIndexToReplace, endIndexToReplace);

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
			//TODO implement
		} else if (InternToken.isFunctionToken(internTokensToReplaceWith)) {
			//TODO: handle single token value replaced by function
		} else if (InternToken.isPeriodToken(internTokensToReplaceWith)) {
			return; //TODO: Find better solution for period
		} else {

		}

	}

	public InternToken getFirstLeftInternToken(int externIndex) {
		for (int searchIndex = externIndex; searchIndex >= 0; searchIndex--) {
			if (externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex) != null) {
				int internTokenIndex = externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex);
				InternToken internTokenToReturn = internTokenFormulaList.get(internTokenIndex);
				return internTokenToReturn;
			}
		}

		return null;
	}

	public int getExternCursorPosition() {

		return this.externCursorPosition;
	}

}
