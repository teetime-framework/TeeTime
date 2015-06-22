package teetime.stage.basic.distributor.dynamic;

import teetime.framework.DynamicActuator;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.framework.signal.InitializingSignal;
import teetime.framework.signal.StartingSignal;

public class CreatePortAction<T> implements PortAction<T> {

	private static final SpScPipeFactory INTER_THREAD_PIPE_FACTORY = new SpScPipeFactory();
	private static final DynamicActuator DYNAMIC_ACTUATOR = new DynamicActuator();

	private final InputPort<T> inputPort;

	public CreatePortAction(final InputPort<T> inputPort) {
		super();
		this.inputPort = inputPort;
	}

	public InputPort<T> getInputPort() {
		return inputPort;
	}

	@Override
	public void execute(final DynamicDistributor<T> dynamicDistributor) {
		System.out.println("Creating...");
		OutputPort<? extends T> newOutputPort = dynamicDistributor.getNewOutputPort();

		INTER_THREAD_PIPE_FACTORY.create(newOutputPort, inputPort);

		Runnable runnable = DYNAMIC_ACTUATOR.wrap(inputPort.getOwningStage());
		Thread thread = new Thread(runnable);
		thread.start();

		newOutputPort.sendSignal(new InitializingSignal());
		newOutputPort.sendSignal(new StartingSignal());

		// FIXME pass the new thread to the analysis so that it can terminate the thread at the end
		System.out.println("Created.");
	}
}
