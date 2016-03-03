/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
 * Contains a pair of {@link teetime.framework.divideandconquer.AbstractDivideAndConquerProblem}
 *
 * @param <P>
 *            The type of the problems.
 */
public final class DividedDCProblem<P> {

	public final P leftProblem, rightProblem;

	public DividedDCProblem(final P leftProblem, final P rightProblem) {
		super();
		this.leftProblem = leftProblem;
		this.rightProblem = rightProblem;
	}

}
