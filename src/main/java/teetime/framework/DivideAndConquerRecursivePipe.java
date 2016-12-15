/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework;

import teetime.framework.divideandconquer.AbstractDivideAndConquerProblem;
import teetime.framework.divideandconquer.AbstractDivideAndConquerSolution;
import teetime.framework.divideandconquer.DividedDCProblem;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;

/**
 * <pre>
 * p -> |pipe| -> s
 * </pre>
 *
 * @author Christian Wulf
 *
 * @param <P>
 *            D&C problem
 * @param <S>
 *            D&C solution
 */
// p (? extends s) -> |pipe| -> s
@SuppressWarnings("PMD.TooManyMethods")
class DivideAndConquerRecursivePipe<P extends AbstractDivideAndConquerProblem<P, S>, S extends AbstractDivideAndConquerSolution<S>> implements
		IPipe<P> {

	protected final DivideAndConquerStage<P, S> cachedTargetStage;

	private final OutputPort<P> sourcePort;
	private final InputPort<S> targetPort;
	@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
	private final int capacity;

	private boolean closed;

	private S element;

	@SuppressWarnings("unchecked")
	protected DivideAndConquerRecursivePipe(final OutputPort<P> sourcePort, final InputPort<S> targetPort) {
		sourcePort.setPipe(this);
		targetPort.setPipe(this);
		this.sourcePort = sourcePort;
		this.targetPort = targetPort;
		this.capacity = 1;
		this.cachedTargetStage = (DivideAndConquerStage<P, S>) targetPort.getOwningStage();
	}

	@Override
	public final OutputPort<? extends P> getSourcePort() {
		return sourcePort;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public final InputPort getTargetPort() {
		return targetPort;
	}

	@Override
	public final boolean hasMore() {
		return !isEmpty();
	}

	@Override
	public final int capacity() {
		return capacity;
	}

	@Override
	public String toString() {
		return sourcePort.getOwningStage().getId() + " -> " + targetPort.getOwningStage().getId() + " (" + super.toString() + ")";
	}

	@Override
	public final void sendSignal(final ISignal signal) {
		this.cachedTargetStage.onSignal(signal, this.targetPort);
	}

	// @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	@Override
	public void waitForStartSignal() throws InterruptedException {
		// do nothing
	}

	@Override
	public final void reportNewElement() {
		// no nothing
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		closed = true;
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return this.add(element);
	}

	@Override
	public Object removeLast() {
		final Object temp = this.element;
		this.element = null; // NOPMD (indicates an empty pipe)
		return temp;
	}

	@Override
	public boolean isEmpty() {
		return this.element == null;
	}

	@Override
	public int size() {
		return (this.element == null) ? 0 : 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean add(final Object element) {
		if (null == element) {
			throw new IllegalArgumentException("Parameter 'element' is null, but must be non-null.");
		}
		this.element = solve((P) element);
		// this.reportNewElement();
		return true;
	}

	private S solve(final P problem) {
		S solution;
		if (problem.isBaseCase()) {
			solution = problem.baseSolve();
		} else {
			DividedDCProblem<P> dividedProblem = problem.divide();
			S firstSolution = solve(dividedProblem.leftProblem); // recursive call
			S secondSolution = solve(dividedProblem.rightProblem); // recursive call
			solution = firstSolution.combine(secondSolution);
		}

		// solution = (S) ((QuicksortProblem) problem).solveDirectly();

		return solution;
	}
}
