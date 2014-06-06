package teetime.framework.scheduling;

import teetime.framework.core.IStage;

public abstract class StageStateContainer {

	public static enum StageState {
		ENABLED, ALL_INPUT_PORTS_CLOSED, DISABLED
	}

	protected final IStage stage;
	protected volatile StageState stageState;

	public StageStateContainer(final IStage stage) {
		this.stage = stage;
		this.stageState = stage.getInputPorts().size() > 0 ? StageState.ENABLED : StageState.ALL_INPUT_PORTS_CLOSED;
//		System.out.println("stage=" + stage + ", stageState=" + this.stageState);
	}

	/**
	 *
	 * @return the new value
	 */
	public abstract int decNumOpenedPorts();
}
