package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.util.concurrent.atomic.AtomicBoolean;

import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;

public abstract class AbstractPipe<T> implements IPipe<T> {

	private final AtomicBoolean closed = new AtomicBoolean();
	private StageWithPort<T, ?> targetStage;

	@Override
	public boolean isClosed() {
		return this.closed.get();
	}

	@Override
	public void close() {
		this.closed.lazySet(true); // lazySet is legal due to our single-writer requirement
	}

	@Override
	public StageWithPort<T, ?> getTargetStage() {
		return this.targetStage;
	}

	public void setTargetStage(StageWithPort<T, ?> targetStage) {
		this.targetStage = targetStage;
	}

}
