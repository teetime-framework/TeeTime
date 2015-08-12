package teetime.framework;

import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;

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

	@Override
	public final InputPort<P> getTargetPort() {
		return null; // (InputPort<P>) targetPort;
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

	@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	@Override
	public void waitForStartSignal() throws InterruptedException {
		// do nothing
	}

	@Override
	public final void reportNewElement() {
		this.cachedTargetStage.executeStage();
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
		this.element = null;
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
		this.element = divideAndConquer((P) element);
		this.reportNewElement();
		return true;
	}

	private S divideAndConquer(final P problem) {
		if (problem.isBaseCase()) {
			return problem.solve();
		} else {
			DividedDCProblem<P> dividedProblem = problem.divide();
			S firstSolution = divideAndConquer(dividedProblem.leftProblem); // recursive call
			S secondSolution = divideAndConquer(dividedProblem.rightProblem); // recursive call
			return firstSolution.combine(secondSolution);
		}
	}
}
