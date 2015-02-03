package teetime.framework;

import java.util.Collection;
import java.util.List;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

/**
 * Represents a minimal stage that composes several other stages.
 *
 * @since 1.1
 * @author Christian Wulf
 *
 */
@SuppressWarnings("PMD.AbstractNaming")
public abstract class CompositeStage extends Stage {

	protected abstract Stage getFirstStage();

	protected abstract Collection<? extends Stage> getLastStages();

	@Override
	protected void executeWithPorts() {
		getFirstStage().executeWithPorts();
	}

	@Override
	protected void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		getFirstStage().onSignal(signal, inputPort);
	}

	@Override
	protected TerminationStrategy getTerminationStrategy() {
		return getFirstStage().getTerminationStrategy();
	}

	@Override
	protected void terminate() {
		getFirstStage().terminate();
	}

	@Override
	protected boolean shouldBeTerminated() {
		return getFirstStage().shouldBeTerminated();
	}

	@Override
	protected InputPort<?>[] getInputPorts() {
		return getFirstStage().getInputPorts();
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		for (final Stage s : getLastStages()) {
			s.validateOutputPorts(invalidPortConnections);
		}
	}

	@Override
	protected boolean isStarted() {
		boolean isStarted = true;
		for (final Stage s : getLastStages()) {
			isStarted = isStarted && s.isStarted();
		}
		return isStarted;
	}

}
