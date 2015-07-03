package teetime.stage.basic.merger.dynamic;

import java.util.concurrent.Semaphore;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.util.framework.port.PortAction;

public class CreatePortActionMerger<T> implements PortAction<DynamicMerger<T>> {

	private static final SpScPipeFactory INTER_THREAD_PIPE_FACTORY = new SpScPipeFactory();

	private final OutputPort<T> outputPort;

	private final Semaphore actionCompleted = new Semaphore(0);

	public CreatePortActionMerger(final OutputPort<T> outputPort) {
		this.outputPort = outputPort;
	}

	@Override
	public void execute(final DynamicMerger<T> dynamicDistributor) {
		InputPort<T> newInputPort = dynamicDistributor.getNewInputPort();

		onInputPortCreated(newInputPort);
	}

	private void onInputPortCreated(final InputPort<T> newInputPort) {
		INTER_THREAD_PIPE_FACTORY.create(outputPort, newInputPort);

		actionCompleted.release();
	}

	public void waitForCompletion() {
		try {
			actionCompleted.acquire();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
