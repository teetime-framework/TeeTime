package teetime.stage.taskfarm.monitoring.extraction;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;

class ExtractionTestPipe<T> implements IMonitorablePipe, IPipe<T> {

	private long numPushes = 0;
	private long numPulls = 0;
	private int size = 1;
	private final int capacity = 1000;
	private long pushThroughput = 1;
	private long pullThroughput = 1;
	private int numWaits = 1;
	private boolean active = true;

	@Override
	public long getNumPushes() {
		if (active) {
			this.numPushes++;
		} else {
			this.numPushes = 0;
		}
		return this.numPushes;
	}

	@Override
	public long getNumPulls() {
		if (active) {
			this.numPulls += 2;
		} else {
			this.numPulls = 0;
		}
		return this.numPulls;
	}

	@Override
	public int size() {
		if (active) {
			this.size *= 2;
		} else {
			this.size = 0;
		}
		return this.size;
	}

	@Override
	public int capacity() {
		return this.capacity;
	}

	@Override
	public long getPushThroughput() {
		if (active) {
			this.pushThroughput += 3;
		} else {
			this.pushThroughput = 0;
		}
		return this.pushThroughput;
	}

	@Override
	public long getPullThroughput() {
		if (active) {
			this.pullThroughput += 5;
		} else {
			this.pullThroughput = 0;
		}
		return this.pullThroughput;
	}

	@Override
	public int getNumWaits() {
		if (active) {
			this.numWaits += 7;
		} else {
			this.numWaits = 0;
		}
		return this.numWaits;
	}

	public void setNumPushes(final long numPushes) {
		this.numPushes = numPushes;
	}

	public void setNumPulls(final long numPulls) {
		this.numPulls = numPulls;
	}

	public void setSize(final int size) {
		this.size = size;
	}

	public void setPushThroughput(final long pushThroughput) {
		this.pushThroughput = pushThroughput;
	}

	public void setPullThroughput(final long pullThroughput) {
		this.pullThroughput = pullThroughput;
	}

	public void setNumWaits(final int numWaits) {
		this.numWaits = numWaits;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	@Override
	public boolean add(final Object element) {
		return false;
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Object removeLast() {
		return null;
	}

	@Override
	public OutputPort<? extends T> getSourcePort() {
		return null;
	}

	@Override
	public InputPort<T> getTargetPort() {
		return null;
	}

	@Override
	public void sendSignal(final ISignal signal) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reportNewElement() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public boolean hasMore() {
		return false;
	}

	@Override
	public void waitForStartSignal() throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void waitForInitializingSignal() throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}
}
