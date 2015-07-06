package teetime.stage.basic.distributor.dynamic;

import teetime.framework.DynamicActuator;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.framework.signal.InitializingSignal;
import teetime.framework.signal.StartingSignal;
import teetime.util.framework.port.PortAction;

public class CreatePortActionDistributor<T> implements PortAction<DynamicDistributor<T>> {

	private static final SpScPipeFactory INTER_THREAD_PIPE_FACTORY = new SpScPipeFactory();
	private static final DynamicActuator DYNAMIC_ACTUATOR = new DynamicActuator();

	private final InputPort<T> inputPort;

	public CreatePortActionDistributor(final InputPort<T> inputPort) {
		this.inputPort = inputPort;
	}

	@Override
	public void execute(final DynamicDistributor<T> dynamicDistributor) {
		OutputPort<? extends T> newOutputPort = dynamicDistributor.getNewOutputPort();

		onOutputPortCreated(newOutputPort);
	}

	private void onOutputPortCreated(final OutputPort<? extends T> newOutputPort) {
		INTER_THREAD_PIPE_FACTORY.create(newOutputPort, inputPort);

		DYNAMIC_ACTUATOR.startWithinNewThread(inputPort.getOwningStage());

		newOutputPort.sendSignal(new InitializingSignal());
		newOutputPort.sendSignal(new StartingSignal());

		// FIXME pass the new thread to the analysis so that it can terminate the thread at the end
	}

	InputPort<T> getInputPort() { // for testing purposes only
		return inputPort;
	}
}
