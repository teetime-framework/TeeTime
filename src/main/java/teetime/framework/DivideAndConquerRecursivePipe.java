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
import teetime.framework.pipe.UnsynchedPipe;
import teetime.framework.scheduling.PipeScheduler;
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

	private static class DivideAndConquerIntermediateStage<P extends AbstractDivideAndConquerProblem<P, S>, S extends AbstractDivideAndConquerSolution<S>>
			extends AbstractStage {
		private final InputPort<P> inputPort = createInputPort();
		private final OutputPort<S> outputPort = createOutputPort();

		@Override
		protected void execute() {
			P problem = inputPort.receive();
			if (null == problem) {
				return; // returns null even if the stage is passive (due to AbstractPort.TerminateElement)
			}
			S solution = solve(problem);
			// logger.trace("Sending (reflexive) solution: " + solution + " > " + outputPort.pipe.getTargetPort().getOwningStage());
			outputPort.send(solution);
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

		public InputPort<P> getInputPort() {
			return inputPort;
		}

		public OutputPort<S> getOutputPort() {
			return outputPort;
		}
	}

	private final OutputPort<P> sourcePort;
	private final InputPort<S> targetPort;
	private final UnsynchedPipe<P> outputPipe;

	protected DivideAndConquerRecursivePipe(final OutputPort<P> sourcePort, final InputPort<S> targetPort) {
		this.sourcePort = sourcePort;
		this.targetPort = targetPort;

		DivideAndConquerIntermediateStage<P, S> divideAndConquerIntermediateStage = new DivideAndConquerIntermediateStage<P, S>();
		outputPipe = new UnsynchedPipe<P>(sourcePort, divideAndConquerIntermediateStage.getInputPort());
		new UnsynchedPipe<S>(divideAndConquerIntermediateStage.getOutputPort(), targetPort);

		sourcePort.setPipe(this);
	}

	@Override
	public final OutputPort<? extends P> getSourcePort() {
		return sourcePort;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public final InputPort getTargetPort() {
		return outputPipe.getTargetPort();
	}

	@Override
	public final boolean hasMore() {
		return outputPipe.hasMore();
	}

	@Override
	public final int capacity() {
		return outputPipe.capacity();
	}

	@Override
	public String toString() {
		return sourcePort.getOwningStage().getId() + " -> " + targetPort.getOwningStage().getId() + " (" + super.toString() + ")";
	}

	@Override
	public final void sendSignal(final ISignal signal) {
		outputPipe.sendSignal(signal);
	}

	@Override
	public void waitForStartSignal() throws InterruptedException {
		// do nothing
	}

	/**
	 * @deprecated since 3.0. Is removed without replacement.
	 */
	@Deprecated
	@Override
	public final void reportNewElement() {
		// no nothing
	}

	@Override
	public boolean isClosed() {
		return outputPipe.isClosed();
	}

	@Override
	public void close() {
		outputPipe.close();
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return outputPipe.addNonBlocking(element);
	}

	@Override
	public void add(final Object element) {
		outputPipe.add(element);
	}

	@Override
	public boolean isEmpty() {
		return outputPipe.isEmpty();
	}

	@Override
	public int size() {
		return outputPipe.size();
	}

	@Override
	public Object removeLast() {
		return outputPipe.removeLast();
	}

	@Override
	public void setScheduler(final PipeScheduler scheduler) {
		// is not used since it delegates to another pipe
	}

}
