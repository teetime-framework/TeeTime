package teetime.stage.taskfarm;

import teetime.framework.pipe.IPipe;

public class TaskFarmTriple<I, O, TFS extends TaskFarmDuplicable<I, O>> {

	private final IPipe inputPipe;
	private final IPipe outputPipe;
	private final TFS stage;

	public TaskFarmTriple(final IPipe inputPipe, final IPipe outputPipe, final TFS newStage) {
		this.inputPipe = inputPipe;
		this.outputPipe = outputPipe;
		this.stage = newStage;
	}

	public IPipe getInputPipe() {
		return inputPipe;
	}

	public IPipe getOutputPipe() {
		return outputPipe;
	}

	public TFS getStage() {
		return stage;
	}
}
