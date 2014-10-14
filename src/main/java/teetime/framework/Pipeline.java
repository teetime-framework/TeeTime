package teetime.framework;

import java.util.List;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

public class Pipeline<FirstStage extends Stage, LastStage extends Stage> implements Stage {

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
	public String getId() {
		return this.firstStage.getId();
	}

	@Override
	public void executeWithPorts() {
		this.firstStage.executeWithPorts();
	}

	@Override
	public Stage getParentStage() {
		return this.firstStage.getParentStage();
	}

	@Override
	public void setParentStage(final Stage parentStage, final int index) {
		this.firstStage.setParentStage(parentStage, index);
	}

	@Override
	public void onIsPipelineHead() {
		this.firstStage.onIsPipelineHead();
	}

	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		this.firstStage.onSignal(signal, inputPort);
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		this.lastStage.validateOutputPorts(invalidPortConnections);
	}

}
