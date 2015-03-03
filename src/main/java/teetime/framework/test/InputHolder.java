package teetime.framework.test;

import teetime.framework.InputPort;
import teetime.framework.Stage;

public final class InputHolder<I> {

	private final StageTester stageTester;
	private final Stage stage;
	private final Iterable<Object> input;

	private InputPort<Object> port;

	@SuppressWarnings("unchecked")
	InputHolder(final StageTester stageTester, final Stage stage, final Iterable<I> input) {
		this.stageTester = stageTester;
		this.stage = stage;
		this.input = (Iterable<Object>) input;
	}

	@SuppressWarnings("unchecked")
	public StageTester to(final InputPort<? extends I> port) {
		if (port.getOwningStage() != stage) {
			throw new AssertionError();
		}
		this.port = (InputPort<Object>) port;

		return stageTester;
	}

	public Iterable<Object> getInput() {
		return input;
	}

	public InputPort<Object> getPort() {
		return port;
	}

}
