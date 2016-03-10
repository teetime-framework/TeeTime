/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.framework.divideandconquer;

/**
 * Generic template for divide and conquer solutions.
 *
 * @author
 *         Robin Mohr
 *
 * @param <S>
 *            The type of the solution.
 */
public abstract class AbstractDivideAndConquerSolution<S> extends Identifiable {

	protected AbstractDivideAndConquerSolution() {
		super();
	}

	protected AbstractDivideAndConquerSolution(final int id) {
		super(id);
	}

	/**
	 * Method to join the given inputs together and send to the output port.
	 *
	 * @param s1
	 *            The solution to combine this one with.
	 * @return S
	 *         The combined solution.
	 */
	public abstract S combine(S s1);

}
