package teetime.framework.test;

import java.util.List;

import teetime.framework.OutputPort;
import teetime.framework.Stage;

public final class OutputHolder<O> {

	private final StageTester stageTester;
	private final List<Object> outputElements;

	private OutputPort<Object> port;

	@SuppressWarnings("unchecked")
	OutputHolder(final StageTester stageTester, final Stage stage, final List<O> outputList) {
		this.stageTester = stageTester;
		this.outputElements = (List<Object>) outputList;
	}

	@SuppressWarnings("unchecked")
	public StageTester from(final OutputPort<O> port) {
		this.port = (OutputPort<Object>) port;

		return stageTester;
	}

	public List<Object> getOutputElements() {
		return outputElements;
	}

	public OutputPort<Object> getPort() {
		return port;
	}

}
