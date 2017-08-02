package teetime.framework.termination;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.StageFacade;
import teetime.framework.Traverser;

public class NextActiveStageShouldTerminate extends TerminationCondition {

	private final AbstractStage stage;
	private final ActiveConsumerStageFinder visitor;
	private final AlwaysFalseCondition alwaysFalseCondition;

	public NextActiveStageShouldTerminate(final AbstractStage stage) {
		this.stage = stage;
		this.visitor = new ActiveConsumerStageFinder();
		this.alwaysFalseCondition = new AlwaysFalseCondition();
	}

	@Override
	public boolean isMet() {
		// traverse at runtime (!) until another active (consumer) stage was found
		Traverser traverser = new Traverser(visitor, alwaysFalseCondition);
		traverser.traverse(stage);
		InputPort<?> inputPortOfActiveStage = visitor.getActiveConsumerStageInputPort();

		AbstractStage activeStage = inputPortOfActiveStage.getOwningStage();
		for (InputPort<?> inputPort : StageFacade.INSTANCE.getInputPorts(activeStage)) {
			if (inputPort != inputPortOfActiveStage && !inputPort.isClosed()) {
				return false;
			}
		}
		return true;
	}

}
