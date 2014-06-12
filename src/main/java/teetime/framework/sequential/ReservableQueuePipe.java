package teetime.framework.sequential;

import java.util.List;

import teetime.framework.core.IInputPort;
import teetime.framework.core.IOutputPort;
import teetime.framework.core.IReservablePipe;
import teetime.framework.core.ISink;
import teetime.framework.core.ISource;
import teetime.util.list.ReservableArrayList;

public class ReservableQueuePipe<T> extends QueuePipe<T> implements IReservablePipe<T> {

	private final ReservableArrayList<T> reservableQueue = new ReservableArrayList<T>(10);

	static public <S0 extends ISource, S1 extends ISink<S1>, T> void connect(final IOutputPort<S0, ? extends T> sourcePort, final IInputPort<S1, T> targetPort) {
		final QueuePipe<T> pipe = new ReservableQueuePipe<T>();
		pipe.setSourcePort(sourcePort);
		pipe.setTargetPort(targetPort);
	}

	@Override
	public void commit() {
		this.reservableQueue.commit();
	}

	@Override
	public void rollback() {
		this.reservableQueue.rollback();
	}

	@Override
	public void putInternal(final T element) {
		this.reservableQueue.reservedAdd(element);
	}

	@Override
	public T tryTakeInternal() {
		return this.reservableQueue.reservedRemoveLast();
	}

	@Override
	public T read() {
		return this.reservableQueue.getLast();
	}

	@Override
	public boolean isEmpty() {
		return this.reservableQueue.isEmpty();
	}

	@Override
	public void putMultiple(final List<T> elements) {
		throw new IllegalStateException();
	}

	@Override
	public List<?> tryTakeMultiple(final int numElementsToTake) {
		throw new IllegalStateException();
	}

}
