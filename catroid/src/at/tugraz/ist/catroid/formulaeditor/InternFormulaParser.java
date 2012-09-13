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

public class InternFormulaParser {

	private List<InternToken> internTokensToParse;
	private int currentTokenParseIndex;
	private InternToken currentToken;

	public InternFormulaParser(List<InternToken> internTokensToParse) {
		this.internTokensToParse = internTokensToParse;
	}

	private void getNextToken() {
		currentTokenParseIndex++;
		if (currentTokenParseIndex == internTokensToParse.size()) {
			currentToken = null;
			return;
		}
		if (currentTokenParseIndex > internTokensToParse.size()) {
			currentToken = null;
			return; //TODO throw Error
		}
		currentToken = internTokensToParse.get(currentTokenParseIndex);

	}

	private FormulaElement findLowerPriorityOperatorElement(Operators currentOp, FormulaElement curElem) {
		FormulaElement returnElem = curElem.getParent();
		FormulaElement notNullElem = curElem;
		boolean goon = true;

		while (goon) {
			if (returnElem == null) {
				goon = false;
				returnElem = notNullElem;
			} else {
				Operators parentOp = Operators.getOperatorByValue(returnElem.getValue());
				int compareOp = parentOp.compareOperatorTo(currentOp);
				if (compareOp < 0) {
					goon = false;
					returnElem = notNullElem;
				} else {
					notNullElem = returnElem;
					returnElem = returnElem.getParent();
				}
			}
		}
		return returnElem;
	}

	public void handleOperator(String operator, FormulaElement curElem, FormulaElement newElem) {
		//        System.out.println("handleOperator: operator="+operator + " curElem="+curElem.getValue() + " newElem="+newElem.getValue());

		if (curElem.getParent() == null) {
			new FormulaElement(FormulaElement.ElementType.OPERATOR, operator, null, curElem, newElem);
			//            System.out.println("handleOperator-after: " + curElem.getRoot().getTreeString());
			return;
		}

		Operators parentOp = Operators.getOperatorByValue(curElem.getParent().getValue());
		Operators currentOp = Operators.getOperatorByValue(operator);

		int compareOp = parentOp.compareOperatorTo(currentOp);

		if (compareOp >= 0) {
			FormulaElement newLeftChild = findLowerPriorityOperatorElement(currentOp, curElem);
			//            System.out.println("findLowerPriorityOperatorElement: " + newLeftChild.getValue());
			FormulaElement newParent = newLeftChild.getParent();

			if (newParent != null) {
				newLeftChild.replaceWithSubElement(operator, newElem);
			} else {
				new FormulaElement(FormulaElement.ElementType.OPERATOR, operator, null, newLeftChild, newElem);
			}
		} else {
			curElem.replaceWithSubElement(operator, newElem);
		}

		//        System.out.println("handleOperator-after: " + curElem.getRoot().getTreeString());
	}

	public FormulaElement parseFormula() {
		currentTokenParseIndex = 0;
		currentToken = internTokensToParse.get(0); //TODO sizecheck

		//TODO enter EOF Token at the end :)

		return formula();

	}

	private FormulaElement formula() {
		FormulaElement termListTree = termList();

		if (currentTokenParseIndex == internTokensToParse.size()) {
			return termListTree;
		}

		return termListTree;
	}

	private FormulaElement termList() {
		FormulaElement curElem = term();

		FormulaElement loopTermTree;
		String operatorStringValue;
		while (currentToken.isOperator()) {

			operatorStringValue = currentToken.getTokenSringValue();
			getNextToken();

			loopTermTree = term();
			handleOperator(operatorStringValue, curElem, loopTermTree);
			curElem = loopTermTree;
		}

		return curElem.getRoot();
	}

	private FormulaElement term() {

		FormulaElement termTree = new FormulaElement(FormulaElement.ElementType.VALUE, null, null);
		FormulaElement curElem = termTree;

		if (currentToken.isOperator() && currentToken.getTokenSringValue() == "-") {

			curElem = new FormulaElement(FormulaElement.ElementType.VALUE, null, termTree, null, null);
			termTree.replaceElement(FormulaElement.ElementType.OPERATOR, "-", null, curElem);

			getNextToken();
		}

		if (currentToken.isNumber()) {

			curElem.replaceElement(FormulaElement.ElementType.VALUE, number());

		} else if (currentToken.isBracketOpen()) {

			curElem.replaceElement(FormulaElement.ElementType.BRACKET, null, null, termList());

			if (!currentToken.isBracketClose()) {
				//TODO throw error
			}

		} else if (currentToken.isFunctionName()) {
			curElem.replaceElement(FormulaElement.ElementType.BRACKET, null, null, function());
			getNextToken();

		} else if (currentToken.isSensor()) {

			//TODO implement

		} else if (currentToken.isCostume()) {

			//TODO implement

		} else {
			//TODO throw error
		}

		return termTree;

	}

	private FormulaElement function() {
		FormulaElement functionTree = new FormulaElement(FormulaElement.ElementType.FUNCTION, null, null);

		if (currentToken.isFunctionName()) {
			//TODO check if functionName is valid
			functionTree = new FormulaElement(FormulaElement.ElementType.FUNCTION, currentToken.getTokenSringValue(),
					null);
			getNextToken();
		} else {
			//TODO throw error
		}

		if (currentToken.isFunctionParameterBracketOpen()) {
			functionTree.setLeftChild(termList());

			if (currentToken.isFunctionParameterDelimiter()) {
				getNextToken();
				functionTree.setRightChild(termList());
			}

			if (!currentToken.isFunctionParameterBracketClose()) {
				//TODO throw error
			}
			getNextToken();

		}

		return functionTree;
	}

	private String number() {
		//TODO implement valid numbers

		String numberToCheck = currentToken.getTokenSringValue();

		if (!numberToCheck.matches("(\\d)+(\\.(\\d)+)?")) {
			//TODO throw Error#
			return null;
		}

		return numberToCheck;
	}
}
