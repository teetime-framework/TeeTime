package teetime.variant.methodcallWithPorts.stage;

import java.util.ArrayList;
import java.util.List;

import teetime.util.concurrent.spsc.Pow2;
import teetime.util.list.CommittableQueue;
import teetime.variant.methodcallWithPorts.framework.core.AbstractStage;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public final class Distributor<T> extends AbstractStage<T, T> {

	// TODO do not inherit from AbstractStage since it provides the default output port that is unnecessary for the distributor

	private final List<OutputPort<T>> outputPortList = new ArrayList<OutputPort<T>>();

	private OutputPort<T>[] outputPorts;
	private int nextOutputPortIndex;

	private int size;

	// private int mask;

	@Override
	protected void execute4(final CommittableQueue<T> elements) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void execute5(final T element) {
		OutputPort<T> outputPort = this.outputPorts[this.nextOutputPortIndex % this.size];
		this.nextOutputPortIndex++;
		outputPort.send(element);
	}

	@Override
	public void onIsPipelineHead() {
		for (OutputPort<?> op : this.outputPorts) {
			op.getPipe().close();
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("End signal sent, size: " + op.getPipe().size());
			}
		}

		// for (OutputPort<?> op : this.outputPorts) {
		// op.pipe = null;
		// }
		// this.outputPorts = null;
		// this.outputPortList.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onStart() {
		this.size = this.outputPortList.size();
		// this.mask = this.size - 1;

		int sizeInPow2 = Pow2.findNextPositivePowerOfTwo(this.size); // is not necessary so far
		this.outputPorts = this.outputPortList.toArray(new OutputPort[sizeInPow2]);
		// System.out.println("outputPorts: " + this.outputPorts);
	}

	@Override
	public OutputPort<T> getOutputPort() {
		return this.getNewOutputPort();
	}

	private OutputPort<T> getNewOutputPort() {
		OutputPort<T> outputPort = new OutputPort<T>();
		this.outputPortList.add(outputPort);
		return outputPort;
	}

	@Override
	public void executeWithPorts() {
		T element = this.getInputPort().receive();

		this.setReschedulable(this.getInputPort().getPipe().size() > 0);

		this.execute5(element);
	}

}
