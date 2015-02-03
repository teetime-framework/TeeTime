package teetime.framework;

import java.util.Collection;
import java.util.List;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

public abstract class CompositeStage extends Stage {

	private final Stage firstStage;
	private final Collection<Stage> lastStages;

	protected CompositeStage(final Stage firstStage, final Collection<Stage> lastStages) {
		this.firstStage = firstStage;
		this.lastStages = lastStages;
	}

	@Override
	protected void executeWithPorts() {
		firstStage.executeWithPorts();
	}

	@Override
	protected void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		firstStage.onSignal(signal, inputPort);
	}

	@Override
	protected TerminationStrategy getTerminationStrategy() {
		return firstStage.getTerminationStrategy();
	}

	@Override
	protected void terminate() {
		firstStage.terminate();
	}

	@Override
	protected boolean shouldBeTerminated() {
		return firstStage.shouldBeTerminated();
	}

	@Override
	protected InputPort<?>[] getInputPorts() {
		return firstStage.getInputPorts();
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		for (final Stage s : lastStages) {
			s.validateOutputPorts(invalidPortConnections);
		}
	}

	@Override
	protected boolean isStarted() {
		boolean isStarted = true;
		for (final Stage s : lastStages) {
			isStarted = isStarted && s.isStarted();
		}
		return isStarted;
	}

}
