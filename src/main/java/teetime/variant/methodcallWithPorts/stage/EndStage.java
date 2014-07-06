package teetime.variant.methodcallWithPorts.stage;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import teetime.util.ConstructorClosure;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.Signal;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;

public class EndStage<T> implements StageWithPort<T, T> {

	private final InputPort<T> inputPort = new InputPort<T>(this);

	public int count;
	public ConstructorClosure<?> closure;
	public List<Object> list = new LinkedList<Object>();

	private final String id;

	public EndStage() {
		this.id = UUID.randomUUID().toString(); // the id should only be represented by a UUID, not additionally by the class name
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
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
		this.getInputPort().receive(); // just consume
		// do nothing
		// this.count++;
		// Object r = this.closure.execute(null);
		// this.list.add(r);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSignal(final Signal signal, final InputPort<?> inputPort) {
		// do nothing
	}

	@Override
	public String getId() {
		return this.id;
	}

}
