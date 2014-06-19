package teetime.variant.methodcallWithPorts.stage;

import teetime.util.list.CommittableQueue;
import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;

public class Sink<T> extends ConsumerStage<T, T> {

	@Override
	protected void execute4(final CommittableQueue<T> elements) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void execute5(final T element) {
		// do nothing
	}

}
