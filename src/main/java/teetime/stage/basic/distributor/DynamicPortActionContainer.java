package teetime.stage.basic.distributor;

import teetime.framework.DynamicActuator;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.framework.signal.InitializingSignal;
import teetime.framework.signal.StartingSignal;

public class DynamicPortActionContainer<T> {

	private static final SpScPipeFactory INTER_THREAD_PIPE_FACTORY = new SpScPipeFactory();

	private final DynamicActuator dynamicActuator = new DynamicActuator();

	public enum DynamicPortAction {
		CREATE, REMOVE;
	}

	private final DynamicPortAction dynamicPortAction;
	private final InputPort<T> inputPort;

	public DynamicPortActionContainer(final DynamicPortAction dynamicPortAction, final InputPort<T> inputPort) {
		super();
		this.dynamicPortAction = dynamicPortAction;
		this.inputPort = inputPort;
	}

	public DynamicPortAction getDynamicPortAction() {
		return dynamicPortAction;
	}

	public InputPort<T> getInputPort() {
		return inputPort;
	}

	public void execute(final OutputPort<T> newOutputPort) {
		INTER_THREAD_PIPE_FACTORY.create(newOutputPort, inputPort);

		Runnable runnable = dynamicActuator.wrap(inputPort.getOwningStage());
		Thread thread = new Thread(runnable);
		thread.start();

		newOutputPort.sendSignal(new InitializingSignal());
		newOutputPort.sendSignal(new StartingSignal());

		// FIXME pass the new thread to the analysis so that it can terminate the thread at the end
	}

}
