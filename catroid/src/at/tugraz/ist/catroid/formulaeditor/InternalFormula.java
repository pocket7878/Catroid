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

public class InternalFormula {

	private ExternInternRepresentationMapping externInternRepresentationMapping;
	private LinkedList<InternToken> internTokenList;

	public InternalFormula(String internalFormulaString,
			ExternInternRepresentationMapping internExternRepresentationMapping) {
		this.externInternRepresentationMapping = internExternRepresentationMapping;
		internTokenList = new LinkedList<InternToken>();

	}

	public void setInternExternRepresentationMapping(ExternInternRepresentationMapping internExternRepresentationMapping) {
		this.externInternRepresentationMapping = internExternRepresentationMapping;
	}

	public void handleKeyInput(CatKeyEvent catKeyEvent, int externCursorPosition) {
		InternToken cursorPositionToken = externInternRepresentationMapping
				.getInternTokenByExternIndex(externCursorPosition);

		if (cursorPositionToken != null) {
			replaceInternTokenByCatKeyEvent(cursorPositionToken, catKeyEvent);
		} else {
			InternToken firstLeftToken = getFirstLeftInternToken(externCursorPosition);
			if (firstLeftToken == null) {
				insertInternTokenByCatKeyEvent(0, catKeyEvent);
			} else {
				appendInternTokenByCatKeyEvent(firstLeftToken, catKeyEvent);
			}
		}

	}

	private void appendInternTokenByCatKeyEvent(InternToken firstLeftToken, CatKeyEvent catKeyEvent) {
		// TODO implement

	}

	private void insertInternTokenByCatKeyEvent(int internTokenListIndex, CatKeyEvent catKeyEvent) {
		List<InternToken> internTokensToInsert = catKeyEvent.createInternTokensByCatKeyEvent();

		internTokenList.addAll(internTokenListIndex, internTokensToInsert);

	}

	private void replaceInternTokenByCatKeyEvent(InternToken internTokenToReplace, CatKeyEvent catKeyEvent) {
		//TODO implement
	}

	private InternToken getFirstLeftInternToken(int externIndex) {
		for (int searchIndex = externIndex; searchIndex >= 0; searchIndex--) {
			if (externInternRepresentationMapping.getInternTokenByExternIndex(externIndex) != null) {
				return externInternRepresentationMapping.getInternTokenByExternIndex(externIndex);
			}
		}

		return null;
	}

}
