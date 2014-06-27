package teetime.variant.methodcallWithPorts.stage;

import java.util.LinkedList;
import java.util.List;

import teetime.util.ConstructorClosure;
import teetime.util.list.CommittableQueue;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;

public class EndStage<T> implements StageWithPort<T, T> {

	private final InputPort<T> inputPort = new InputPort<T>(this);

	public int count;
	public ConstructorClosure<?> closure;
	public List<Object> list = new LinkedList<Object>();

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
	public StageWithPort<?, ?> getParentStage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentStage(final StageWithPort<?, ?> parentStage, final int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReschedulable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputPort<T> getInputPort() {
		return this.inputPort;
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
	public void onStart() {
		// TODO Auto-generated method stub

	}

}
