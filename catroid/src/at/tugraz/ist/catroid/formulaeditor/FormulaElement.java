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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum ElementType {
		OPERATOR, FUNCTION, NUMBER, SENSOR, USER_VARIABLE, BRACKET
	}

	//	public static final int ELEMENT_OPERATOR = 2;
	//	public static final int ELEMENT_FUNCTION = 3;
	//	public static final int ELEMENT_VALUE = 4;
	//	public static final int ELEMENT_SENSOR = 5;
	//	public static final int ELEMENT_CONSTANT = 6;
	//	public static final int ELEMENT_VARIABLE = 7;

	//	private static HashMap<String, Integer> variableMap = new HashMap<String, Integer>(); TODO
	//private int type;
	private ElementType type;
	private String value;
	private FormulaElement leftChild = null;
	private FormulaElement rightChild = null;
	private FormulaElement parent = null;

	public FormulaElement(ElementType type, String value, FormulaElement parent) {
		this.type = type;
		this.value = value;
		this.parent = parent;
	}

	public FormulaElement(ElementType type, String value, FormulaElement parent, FormulaElement leftChild,
			FormulaElement rightChild) {
		this.type = type;
		this.value = value;
		this.parent = parent;
		this.leftChild = leftChild;
		this.rightChild = rightChild;

		if (leftChild != null) {
			this.leftChild.parent = this;
		}
		if (rightChild != null) {
			this.rightChild.parent = this;
		}

	}

	public ElementType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public FormulaElement getLeftChild() {
		return leftChild;
	}

	public FormulaElement getRightChild() {
		return rightChild;
	}

	public List<InternToken> getInternTokenList() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		switch (type) {
			case BRACKET:
				internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
				if (rightChild != null) {
					internTokenList.addAll(rightChild.getInternTokenList());
				}
				internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));
				break;
			case OPERATOR:
				if (leftChild != null) {
					internTokenList.addAll(leftChild.getInternTokenList());
				}
				internTokenList.add(new InternToken(InternTokenType.OPERATOR, this.value));
				if (rightChild != null) {
					internTokenList.addAll(rightChild.getInternTokenList());
				}
				break;
			case FUNCTION:
				internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, value));
				boolean functionHasParameters = false;
				if (leftChild != null) {
					internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
					functionHasParameters = true;
					internTokenList.addAll(leftChild.getInternTokenList());
				}
				if (rightChild != null) {
					internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
					internTokenList.addAll(rightChild.getInternTokenList());
				}
				if (functionHasParameters) {
					internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
				}
				break;
			case USER_VARIABLE:
				internTokenList.add(new InternToken(InternTokenType.USER_VARIABLE, this.value));
				break;
			case NUMBER:
				internTokenList.add(new InternToken(InternTokenType.NUMBER, this.value));
				break;
			case SENSOR:
				internTokenList.add(new InternToken(InternTokenType.SENSOR, this.value));
				break;
		}
		return internTokenList;
	}

	//TODO remove Method
	public String getEditTextRepresentation() {
		String result = "";

		switch (type) {
			case BRACKET:
				result += "( ";
				if (rightChild != null) {
					result += rightChild.getEditTextRepresentation();
				}
				result += ") ";
				break;
			case OPERATOR:
				if (leftChild != null) {
					result += leftChild.getEditTextRepresentation();
				}
				result += this.value + " ";
				if (rightChild != null) {
					result += rightChild.getEditTextRepresentation();
				}
				break;
			case FUNCTION:
				result += this.value + "( ";
				if (leftChild != null) {
					result += leftChild.getEditTextRepresentation();
				}
				if (rightChild != null) {
					result += ", ";
					result += rightChild.getEditTextRepresentation();
				}
				result += ") ";
				break;
			case USER_VARIABLE:
				result += this.value + " ";
				break;
			case NUMBER:
				result += this.value + " ";
				break;
			case SENSOR:
				result += this.value + " ";
				break;
		}
		return result;
	}

	public FormulaElement getRoot() {
		FormulaElement root = this;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}

	public Double interpretRecursive() {

		Double returnValue = 0.0d;

		if (type == ElementType.BRACKET) {
			returnValue = rightChild.interpretRecursive();
		}
		if (type == ElementType.NUMBER) {
			returnValue = Double.parseDouble(value);
		} else if (type == ElementType.OPERATOR) {
			if (leftChild != null) {// binär operator
				Double left = leftChild.interpretRecursive();
				Double right = rightChild.interpretRecursive();

				if (value.equals("+")) {
					returnValue = left + right;
				}
				if (value.equals("-")) {
					returnValue = left - right;
				}
				if (value.equals("*")) {
					returnValue = left * right;
				}
				if (value.equals("/")) {

					returnValue = left / right;
				}
				if (value.equals("^")) {
					returnValue = java.lang.Math.pow(left, right);
				}
			} else {//unary operators
				Double right = rightChild.interpretRecursive();
				//				if (value.equals("+")) {
				//					return right;
				//				}
				if (value.equals("-")) {
					returnValue = -right;
				}

			}
		} else if (type == ElementType.FUNCTION) {
			Double left = 0.0d;
			if (leftChild != null) {
				left = leftChild.interpretRecursive();
			}

			if (value.equals("sin")) {
				returnValue = java.lang.Math.sin(Math.toRadians(left));
			}
			if (value.equals("cos")) {
				returnValue = java.lang.Math.cos(Math.toRadians(left));
			}
			if (value.equals("tan")) {
				returnValue = java.lang.Math.tan(Math.toRadians(left));
			}
			if (value.equals("ln")) {
				returnValue = java.lang.Math.log(left);
			}
			if (value.equals("log")) {
				returnValue = java.lang.Math.log10(left);
			}
			if (value.equals("sqrt")) {
				returnValue = java.lang.Math.sqrt(left);
			}
			if (value.equals("rand")) {
				double min = left;
				double max = rightChild.interpretRecursive();
				returnValue = min + (java.lang.Math.random() * (max - min));
			}
			if (value.equals("abs")) {
				returnValue = java.lang.Math.abs(left);
			}
			if (value.equals("round")) {
				returnValue = (double) java.lang.Math.round(left);
			}
			if (value.equals("pi")) {
				returnValue = java.lang.Math.PI;
			}
			if (value.equals("e")) {
				returnValue = java.lang.Math.E;
			}
		} else if (type == ElementType.SENSOR) {
			returnValue = SensorManager.getSensorValue(value);
		} else if (type == ElementType.USER_VARIABLE) {
			//			TODO ^_^
			return null;
		}

		returnValue = checkDegeneratedDoubleValues(returnValue);

		return returnValue;

	}

	private Double checkDegeneratedDoubleValues(Double valueToCheck) {
		if (valueToCheck.doubleValue() == Double.NEGATIVE_INFINITY) {
			return -Double.MAX_VALUE;
		}
		if (valueToCheck.doubleValue() == Double.POSITIVE_INFINITY) {
			return Double.MAX_VALUE;
		}
		if (valueToCheck.isNaN()) {
			return 1.0;
		}

		return 1.0;
	}

	public FormulaElement getParent() {
		return parent;
	}

	public void setRightChild(FormulaElement rightChild) {
		this.rightChild = rightChild;
		this.rightChild.parent = this;
	}

	public void setLeftChild(FormulaElement leftChild) {
		this.leftChild = leftChild;
		this.leftChild.parent = this;
	}

	public void replaceElement(FormulaElement current) {
		parent = current.parent;
		leftChild = current.leftChild;
		rightChild = current.rightChild;
		value = current.value;
		type = current.type;

		if (leftChild != null) {
			leftChild.parent = this;
		}
		if (rightChild != null) {
			rightChild.parent = this;
		}
	}

	public void replaceElement(ElementType type, String value) {
		this.value = value;
		this.type = type;
	}

	public void replaceElement(ElementType type, String value, FormulaElement leftChild, FormulaElement rightChild) {
		this.value = value;
		this.type = type;
		this.leftChild = leftChild;
		if (this.leftChild != null) {
			this.leftChild.parent = this;
		}
		this.rightChild = rightChild;
		if (rightChild != null) {
			this.rightChild.parent = this;
		}
	}

	public void replaceWithSubElement(String operator, FormulaElement rightChild) {

		FormulaElement cloneThis = new FormulaElement(ElementType.OPERATOR, operator, this.getParent(), this,
				rightChild);

		cloneThis.parent.rightChild = cloneThis;
	}

	@Override
	public String toString() {
		return value;

	}

	public String getTreeString() {
		String text = "";

		text = "(" + type + "/" + value + " ";

		if (leftChild == null && rightChild == null) {
			return text + ") ";
		}

		if (leftChild != null) {
			text += leftChild.getTreeString() + " ";

		} else {
			text += "( )";
		}
		if (rightChild != null) {
			text += rightChild.getTreeString() + " ";
		} else {
			text += "( )";
		}
		return text + ") ";
	}

}
