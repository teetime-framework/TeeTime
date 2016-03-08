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
 * Generic template for divide and conquer problems.
 *
 * @author
 *         Robin Mohr
 *
 * @param <P>
 *            The type of the problem.
 *
 * @param <S>
 *            The type of the problems solution.
 */
public abstract class AbstractDivideAndConquerProblem<P, S> extends Identifiable {

	protected AbstractDivideAndConquerProblem() {
		super();
	}

	protected AbstractDivideAndConquerProblem(final int id) {
		super(id);
	}

	/**
	 * Determines whether or not the problem is a base case.
	 *
	 * @return
	 *         The boolean whose properties determine the split condition
	 */
	public abstract boolean isBaseCase();

	/**
	 * Divides the problem and creates a <code>DividedDCProblem</code>.
	 *
	 * @return
	 *         {@link teetime.framework.divideandconquer.DividedDCProblem}
	 */
	public abstract DividedDCProblem<P> divide();

	/**
	 * Solves the problem, only if it is a base case problem.
	 *
	 * @return
	 *         {@link teetime.framework.divideandconquer.AbstractDivideAndConquerSolution}
	 */
	public abstract S baseSolve();

}
