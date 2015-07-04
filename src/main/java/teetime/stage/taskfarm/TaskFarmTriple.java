package teetime.stage.taskfarm;

import teetime.framework.pipe.IPipe;

/**
 * This class contains an instance of an enclosed stage as well as its
 * surrounding pipes.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of enclosed stage
 */
public class TaskFarmTriple<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final IPipe inputPipe;
	private final IPipe outputPipe;
	private final T stage;

	/**
	 * Constructor.
	 *
	 * @param inputPipe
	 *            pipe from Distributor to enclosed stage
	 * @param outputPipe
	 *            pipe from enclosed stage to Merger
	 * @param newStage
	 *            instance of enclosed stage
	 */
	public TaskFarmTriple(final IPipe inputPipe, final IPipe outputPipe, final T newStage) {
		this.inputPipe = inputPipe;
		this.outputPipe = outputPipe;
		this.stage = newStage;
	}

	public IPipe getInputPipe() {
		return this.inputPipe;
	}

	public IPipe getOutputPipe() {
		return this.outputPipe;
	}

	public T getStage() {
		return this.stage;
	}
}
