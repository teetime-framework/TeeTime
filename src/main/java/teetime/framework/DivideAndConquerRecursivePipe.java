package teetime.framework;

import org.apache.commons.math3.util.Pair;

import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;
import teetime.util.divideAndConquer.Identifiable;

public class DivideAndConquerRecursivePipe<P extends Identifiable, S extends Identifiable> implements IPipe<P> {

	protected final AbstractDCStage<P, S> cachedTargetStage;

	private final OutputPort<? extends P> sourcePort;
	private final InputPort<S> targetPort;
	@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
	private final int capacity;

	private boolean closed;

	private S element;

	@SuppressWarnings("unchecked")
	protected DivideAndConquerRecursivePipe(final OutputPort<? extends P> sourcePort, final InputPort<S> targetPort) {
		if (sourcePort == null) {
			throw new IllegalArgumentException("sourcePort may not be null");
		}
		if (targetPort == null) {
			throw new IllegalArgumentException("targetPort may not be null");
		}

		sourcePort.setPipe(this);
		targetPort.setPipe(this);

		this.sourcePort = sourcePort;
		this.targetPort = targetPort;
		this.capacity = 1;
		this.cachedTargetStage = (AbstractDCStage<P, S>) targetPort.getOwningStage();
	}

	@Override
	public final OutputPort<? extends P> getSourcePort() {
		return sourcePort;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final InputPort<P> getTargetPort() {
		return (InputPort<P>) targetPort;
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
		// do nothing
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
		final AbstractDCStage<P, S> tempTargetStage = cachedTargetStage;
		if (tempTargetStage.isBaseCase(problem)) {
			return tempTargetStage.solve(problem);
		} else {
			Pair<P, P> problems = tempTargetStage.divide(problem);
			S firstSolution = divideAndConquer(problems.getFirst()); // recursive call
			S secondSolution = divideAndConquer(problems.getSecond()); // recursive call
			return tempTargetStage.combine(firstSolution, secondSolution);
		}
	}
}
