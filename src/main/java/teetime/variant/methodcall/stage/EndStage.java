package teetime.variant.methodcall.stage;

import java.util.LinkedList;
import java.util.List;

import teetime.util.ConstructorClosure;
import teetime.util.list.CommittableQueue;
import teetime.variant.methodcall.framework.core.Stage;

public class EndStage<T> implements Stage<T, T> {

	public int count;
	public ConstructorClosure<?> closure;
	public List<Object> list = new LinkedList<Object>();

	@Override
	public T execute(final Object element) {
		throw new IllegalStateException();
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

	@Override
	public CommittableQueue<T> execute2(final CommittableQueue<T> elements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stage getParentStage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentStage(final Stage<?, ?> parentStage, final int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public Stage next() {
		return null;
	}

	@Override
	public void setSuccessor(final Stage<? super T, ?> successor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object executeRecursively(final Object element) {
		return element;
	}

	@Override
	public boolean isReschedulable() {
		return false;
	}

}
