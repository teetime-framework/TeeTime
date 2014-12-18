package teetime.framework;

import teetime.framework.idle.IdleStrategy;
import teetime.framework.idle.YieldStrategy;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;

public final class RunnableConsumerStage extends RunnableStage {

	private final IdleStrategy idleStrategy;

	public RunnableConsumerStage(final Stage stage) {
		this(stage, new YieldStrategy());
	}

	public RunnableConsumerStage(final Stage stage, final IdleStrategy idleStrategy) {
		super(stage);
		this.idleStrategy = idleStrategy;
	}

	@Override
	protected void beforeStageExecution() {
		// TODO wait for starting signal
	}

	@Override
	protected void executeStage() {
		try {
			this.stage.executeWithPorts();
		} catch (NotEnoughInputException e) {
			checkforSignals(); // check for termination
			executeIdleStrategy();
		}
	}

	private void executeIdleStrategy() {
		try {
			idleStrategy.execute();
		} catch (InterruptedException e) {
			checkforSignals(); // check for termination
		}
	}

	private void checkforSignals() {
		// FIXME should getInputPorts() really be defined in Stage?
		InputPort<?>[] inputPorts = stage.getInputPorts();
		for (InputPort<?> inputPort : inputPorts) {
			IPipe pipe = inputPort.getPipe();
			if (pipe instanceof AbstractInterThreadPipe) {
				AbstractInterThreadPipe intraThreadPipe = (AbstractInterThreadPipe) pipe;
				ISignal signal = intraThreadPipe.getSignal();
				if (null != signal) {
					stage.onSignal(signal, inputPort);
				}
			}
		}
	}

	@Override
	protected void afterStageExecution() {
		// do nothing
	}

}
