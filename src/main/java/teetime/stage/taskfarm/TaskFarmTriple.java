package teetime.stage.taskfarm;

import teetime.framework.pipe.IMonitorablePipe;

public class TaskFarmTriple<I, O, TFS extends TaskFarmDuplicable<I, O>> {

	private final IMonitorablePipe inputPipe;
	private final IMonitorablePipe outputPipe;
	private final TFS stage;

	public TaskFarmTriple(final IMonitorablePipe inputPipe, final IMonitorablePipe outputPipe, final TFS stage) {
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

	public TFS getStage() {
		return stage;
	}
}
