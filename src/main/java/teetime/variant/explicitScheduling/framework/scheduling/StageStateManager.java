package teetime.variant.explicitScheduling.framework.scheduling;

import java.util.Arrays;

import teetime.variant.explicitScheduling.framework.core.IInputPort;
import teetime.variant.explicitScheduling.framework.core.IPipeline;
import teetime.variant.explicitScheduling.framework.core.IPortListener;
import teetime.variant.explicitScheduling.framework.core.IStage;
import teetime.variant.explicitScheduling.framework.scheduling.StageStateContainer.StageState;

public final class StageStateManager implements IPortListener {

	private final StageStateContainer[] stageStateContainers;

	public StageStateManager(final IPipeline pipeline) {
		this.stageStateContainers = this.initStageStateContainers(pipeline);
		this.registerAtAllInputPorts(pipeline);
	}

	private void registerAtAllInputPorts(final IPipeline pipeline) {
		for (IStage stage : pipeline.getStages()) {
			for (IInputPort<IStage, ?> inputPort : stage.getInputPorts()) {
				inputPort.setPortListener(this);
			}
		}
	}

	private StageStateContainer[] initStageStateContainers(final IPipeline pipeline) {
		StageStateContainer[] stageStateContainers = new StageStateContainer[pipeline.getStages().size()];
		for (IStage stage : pipeline.getStages()) {
			StageStateContainer stageStateContainer;
			if (this.isConnectedWithAnotherThread(stage)) {
				stageStateContainer = new SynchronizedStageStateContainer(stage);
			} else {
				stageStateContainer = new UnsynchronizedStageStateContainer(stage);
			}
			stageStateContainers[stage.getSchedulingIndex()] = stageStateContainer;
		}
		return stageStateContainers;
	}

	private boolean isConnectedWithAnotherThread(final IStage stage) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onPortIsClosed(final IInputPort<?, ?> inputPort) {
		StageStateContainer stageStateContainer = this.getStageStateContainer(inputPort.getOwningStage());

		int newNumOpenedPorts = stageStateContainer.decNumOpenedPorts();
		if (newNumOpenedPorts == 0) {
			stageStateContainer.stageState = StageState.ALL_INPUT_PORTS_CLOSED;
//			System.out.println("Closed stage: " + stageStateContainer.stage);
		} else if (newNumOpenedPorts < 0) {
			// TODO log warning
			// this.logger.warning("Closed port more than once: portIndex=" + inputPort.getIndex() + " for stage " + this);
		}
	}

	public boolean areAllInputPortsClosed(final IStage stage) {
		StageStateContainer stageStateContainer = this.getStageStateContainer(stage);
		return stageStateContainer.stageState == StageState.ALL_INPUT_PORTS_CLOSED;
	}

	public boolean isStageEnabled(final IStage stage) {
		StageStateContainer stageStateContainer = this.getStageStateContainer(stage);
		return stageStateContainer.stageState == StageState.ENABLED;
	}

	public void disable(final IStage stage) {
		StageStateContainer stageStateContainer = this.getStageStateContainer(stage);
		stageStateContainer.stageState = StageState.DISABLED;
	}

	private StageStateContainer getStageStateContainer(final IStage stage) {
		int schedulingIndex = stage.getSchedulingIndex();
		StageStateContainer stageStateContainer = this.stageStateContainers[schedulingIndex];
		if (stageStateContainer == null) {
			throw new NullPointerException("No container found for index ="+schedulingIndex+"\n=> stageStateContainers="+Arrays.asList(this.stageStateContainers));
		}
		return stageStateContainer;
	}
}
