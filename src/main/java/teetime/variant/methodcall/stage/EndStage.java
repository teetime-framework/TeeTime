package teetime.variant.methodcall.stage;

import java.util.LinkedList;
import java.util.List;

import teetime.util.ConstructorClosure;
import teetime.util.list.CommittableQueue;
import teetime.variant.methodcall.framework.core.InputPort;
import teetime.variant.methodcall.framework.core.OutputPort;
import teetime.variant.methodcall.framework.core.Stage;
import teetime.variant.methodcall.framework.core.StageWithPort;

public class EndStage<T> implements StageWithPort<T, T> {

	@Override
	public Object executeRecursively(final Object element) {
		return this.execute(element);
	}

	public int count;
	public ConstructorClosure<?> closure;
	public List<Object> list = new LinkedList<Object>();

	@Override
	public T execute(final Object element) {
		return (T) element;
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
	public void setParentStage(final Stage parentStage, final int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public Stage next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSuccessor(final Stage<?, ?> successor) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReschedulable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputPort<T> getInputPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputPort<T> getOutputPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeWithPorts() {
		// this.getInputPort().receive(); // just consume
		// do nothing
		// this.count++;
		// Object r = this.closure.execute(null);
		// this.list.add(r);
	}

	@Override
	public void setSuccessor(final StageWithPort<?, ?> successor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

}
