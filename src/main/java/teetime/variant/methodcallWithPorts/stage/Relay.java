package teetime.variant.methodcallWithPorts.stage;

import teetime.util.list.CommittableQueue;
import teetime.variant.methodcallWithPorts.framework.core.AbstractStage;

public class Relay<T> extends AbstractStage<T, T> {

	public Relay() {
		this.setReschedulable(true);
	}

	@Override
	public void executeWithPorts() {
		T element = this.getInputPort().receive();
		if (null == element) {
			if (this.getInputPort().getPipe().isClosed()) {
				this.setReschedulable(false);
				assert 0 == this.getInputPort().getPipe().size();
			}
			Thread.yield();
			return;
		}
		this.send(element);
	}

	@Override
	public void onIsPipelineHead() {
		if (this.getInputPort().getPipe().isClosed()) {
			this.setReschedulable(false);
		}
	}

	@Override
	protected void execute4(final CommittableQueue<T> elements) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void execute5(final T element) {
		// TODO Auto-generated method stub

	}

}
