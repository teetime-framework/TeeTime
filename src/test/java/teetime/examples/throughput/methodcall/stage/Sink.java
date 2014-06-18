package teetime.examples.throughput.methodcall.stage;

import teetime.util.list.CommittableQueue;

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
