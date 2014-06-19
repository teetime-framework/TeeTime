package teetime.variant.methodcallWithPorts.stage;

import teetime.util.list.CommittableQueue;
import teetime.variant.methodcallWithPorts.framework.core.AbstractStage;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;

public class Delay<I> extends AbstractStage<I, I> {

	private final InputPort<Long> timestampTriggerInputPort = new InputPort<Long>();

	public Delay() {
		// this.setReschedulable(true);
	}

	@Override
	public void executeWithPorts() {
		Long timestampTrigger = this.timestampTriggerInputPort.receive();
		if (null == timestampTrigger) {
			return;
		}
		// System.out.println("got timestamp; #elements: " + this.getInputPort().pipe.size());

		// System.out.println("#elements: " + this.getInputPort().pipe.size());
		// TODO implement receiveAll() and sendMultiple()
		while (!this.getInputPort().getPipe().isEmpty()) {
			I element = this.getInputPort().receive();
			this.send(element);
		}

		// this.setReschedulable(this.getInputPort().pipe.size() > 0);
		this.setReschedulable(false);
		// System.out.println("delay: " + this.getInputPort().pipe.size());
	}

	@Override
	public void onIsPipelineHead() {
		this.setReschedulable(true);
	}

	@Override
	protected void execute4(final CommittableQueue<I> elements) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void execute5(final I element) {
		// TODO Auto-generated method stub

	}

	public InputPort<Long> getTimestampTriggerInputPort() {
		return this.timestampTriggerInputPort;
	}

}
