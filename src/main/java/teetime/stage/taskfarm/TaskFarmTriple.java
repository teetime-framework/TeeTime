package teetime.stage.taskfarm;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.pipe.IMonitorablePipe;

@SuppressWarnings("deprecation")
public class TaskFarmTriple {

	private final IMonitorablePipe inputPipe;
	private final IMonitorablePipe outputPipe;
	private final AbstractCompositeStage stage;

	public TaskFarmTriple(final IMonitorablePipe inputPipe, final IMonitorablePipe outputPipe, final AbstractCompositeStage stage) {
		this.inputPipe = inputPipe;
		this.outputPipe = outputPipe;
		this.stage = stage;
	}

	public IMonitorablePipe getInputPipe() {
		return inputPipe;
	}

	public IMonitorablePipe getOutputPipe() {
		return outputPipe;
	}

	public AbstractCompositeStage getStage() {
		return stage;
	}
}
