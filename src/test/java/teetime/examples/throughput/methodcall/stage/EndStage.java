package teetime.examples.throughput.methodcall.stage;

import teetime.examples.throughput.methodcall.InputPort;
import teetime.examples.throughput.methodcall.OnDisableListener;
import teetime.examples.throughput.methodcall.OutputPort;
import teetime.examples.throughput.methodcall.SchedulingInformation;
import teetime.examples.throughput.methodcall.Stage;
import teetime.examples.throughput.methodcall.StageWithPort;
import teetime.util.list.CommittableQueue;

public class EndStage<T> implements StageWithPort<T, T> {

	@Override
	public Object executeRecursively(final Object element) {
		return this.execute(element);
	}

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
	public SchedulingInformation getSchedulingInformation() {
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
	public void setListener(final OnDisableListener listener) {
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
		// TODO Auto-generated method stub

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
