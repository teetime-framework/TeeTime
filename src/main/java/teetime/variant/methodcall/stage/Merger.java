package teetime.variant.methodcall.stage;

import java.util.ArrayList;
import java.util.List;

import teetime.util.concurrent.spsc.Pow2;
import teetime.util.list.CommittableQueue;
import teetime.variant.methodcall.framework.core.AbstractStage;
import teetime.variant.methodcall.framework.core.InputPort;

public class Merger<T> extends AbstractStage<T, T> {

	// TODO do not inherit from AbstractStage since it provides the default input port that is unnecessary for the merger

	private final List<InputPort<T>> inputPortList = new ArrayList<InputPort<T>>();
	private int nextInputPortIndex;
	private int size;
	private InputPort<T>[] inputPorts;

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
		this.send(element);
	}

	@Override
	public void executeWithPorts() {
		InputPort<T> inputPort = this.inputPorts[this.nextInputPortIndex % this.size];
		T element = inputPort.receive();
		// if (element == null) {
		// return;
		// }

		this.nextInputPortIndex++;
		InputPort<T> nextInputPort = this.inputPorts[this.nextInputPortIndex % this.size];
		this.setReschedulable(nextInputPort.getPipe().size() > 0);

		this.execute5(element);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onStart() {
		this.size = this.inputPortList.size();
		// this.mask = this.size - 1;

		int sizeInPow2 = Pow2.findNextPositivePowerOfTwo(this.size); // is not necessary so far
		this.inputPorts = this.inputPortList.toArray(new InputPort[sizeInPow2]);
		// System.out.println("inputPorts: " + this.inputPorts);
	}

	@Override
	public InputPort<T> getInputPort() {
		return this.getNewInputPort();
	}

	private InputPort<T> getNewInputPort() {
		InputPort<T> inputPort = new InputPort<T>();
		this.inputPortList.add(inputPort);
		return inputPort;
	}

	@Override
	public void onIsPipelineHead() {
		// TODO Auto-generated method stub

	}

}
