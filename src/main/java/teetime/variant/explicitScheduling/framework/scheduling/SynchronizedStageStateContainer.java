package teetime.variant.explicitScheduling.framework.scheduling;

import java.util.concurrent.atomic.AtomicInteger;

import teetime.variant.explicitScheduling.framework.core.IStage;

public final class SynchronizedStageStateContainer extends StageStateContainer {

	private final AtomicInteger numOpenedPorts = new AtomicInteger();

	public SynchronizedStageStateContainer(final IStage stage) {
		super(stage);
		this.numOpenedPorts.set(stage.getInputPorts().size());
	}

	@Override
	public int decNumOpenedPorts() {
		return this.numOpenedPorts.decrementAndGet();
	}

}
