package teetime.stage.taskfarm.monitoring.extraction;

import teetime.framework.InputPort;
import teetime.framework.Stage;
import teetime.framework.pipe.IPipe;
import teetime.stage.NoopFilter;

class ExtractionTestInputPort<T> extends InputPort<T> {

	private final IPipe<T> inputPipe;

	public ExtractionTestInputPort(final IPipe<T> inputPipe) {
		this.inputPipe = inputPipe;
	}

	@Override
	public IPipe<T> getPipe() {
		return inputPipe;
	}

	@Override
	public Stage getOwningStage() {
		return new NoopFilter<T>();
	}
}
