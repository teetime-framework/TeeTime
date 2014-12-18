package teetime.framework;

import java.util.List;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

@Deprecated
public class OldPipeline<FirstStage extends Stage, LastStage extends Stage> extends Stage {

	protected FirstStage firstStage;
	protected LastStage lastStage;

	public FirstStage getFirstStage() {
		return this.firstStage;
	}

	public void setFirstStage(final FirstStage firstStage) {
		this.firstStage = firstStage;
	}

	public LastStage getLastStage() {
		return this.lastStage;
	}

	public void setLastStage(final LastStage lastStage) {
		this.lastStage = lastStage;
	}

	@Override
	public void executeWithPorts() {
		this.firstStage.executeWithPorts();
	}

	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		this.firstStage.onSignal(signal, inputPort);
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		this.lastStage.validateOutputPorts(invalidPortConnections);
	}

	@Override
	public TerminationStrategy getTerminationStrategy() {
		return firstStage.getTerminationStrategy();
	}

	@Override
	public void terminate() {
		firstStage.terminate();
	}

	@Override
	public boolean shouldBeTerminated() {
		return firstStage.shouldBeTerminated();
	}

	@Override
	protected InputPort<?>[] getInputPorts() {
		return firstStage.getInputPorts();
	}

	@Override
	public void setOwningThread(final Thread owningThread) {
		firstStage.setOwningThread(owningThread);
	}

	@Override
	public Thread getOwningThread() {
		return firstStage.getOwningThread();
	}

}
