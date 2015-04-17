/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package qa;

import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class PluralForCollectionVariableCheck extends Check {

	private final Set<String> collectionNames = new HashSet<String>();

	public PluralForCollectionVariableCheck() {
		collectionNames.add("Collection");
		collectionNames.add("Set");
		collectionNames.add("List");
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.VARIABLE_DEF };
	}

	@Override
	public void visitToken(final DetailAST ast) {
		DetailAST typeAst = ast.findFirstToken(TokenTypes.TYPE);
		DetailAST identifierAst = ast.findFirstToken(TokenTypes.IDENT);

		DetailAST actualTypeAst = typeAst.getFirstChild();
		DetailAST typeArgumentsAst = actualTypeAst.getNextSibling();
		String actualTypeName = actualTypeAst.getText();
		if (collectionNames.contains(actualTypeName)) {
			String identifierName = identifierAst.getText();
			if (!identifierName.endsWith("s")) {
				String message = "The variable '" + identifierName + "' should be named in plural since it represents a " + actualTypeName + " of "
						+ typeArgumentsAst.getText();
				log(ast.getLineNo(), message);
			}
		}
	}

}
