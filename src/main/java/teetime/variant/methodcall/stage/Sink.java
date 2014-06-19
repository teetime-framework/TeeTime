package teetime.variant.methodcall.stage;

import teetime.util.list.CommittableQueue;
import teetime.variant.methodcall.framework.core.ConsumerStage;

public class Sink<T> extends ConsumerStage<T, T> {

	@Override
	public T execute(final Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void execute4(final CommittableQueue<T> elements) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void execute5(final T element) {
		// do nothing
	}

}
