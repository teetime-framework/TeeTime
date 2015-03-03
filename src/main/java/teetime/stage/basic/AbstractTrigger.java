package teetime.stage.basic;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

abstract class AbstractTrigger<I, T, O> extends AbstractStage {

	private final InputPort<I> inputPort = createInputPort();
	private final InputPort<T> triggerInputPort = createInputPort();
	private final OutputPort<O> outputPort = createOutputPort();

	protected AbstractTrigger() {
		super();
	}

	@Override
	protected void executeWithPorts() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTerminating() throws Exception {
		// TODO Auto-generated method stub
		super.onTerminating();
	}

}
