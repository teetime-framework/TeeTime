package teetime.framework.termination;

import teetime.framework.AbstractStage;
import teetime.framework.StageState;

public class StageHasTerminated extends TerminationCondition {

	private final AbstractStage stage;

	public StageHasTerminated(final AbstractStage stage) {
		this.stage = stage;
	}

	@Override
	public boolean isMet() {
		return stage.getCurrentState() == StageState.TERMINATED;
	}

}
