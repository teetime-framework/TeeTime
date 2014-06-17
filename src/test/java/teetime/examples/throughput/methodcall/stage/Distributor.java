package teetime.examples.throughput.methodcall.stage;

import java.util.ArrayList;
import java.util.List;

import teetime.examples.throughput.methodcall.ConsumerStage;
import teetime.examples.throughput.methodcall.OutputPort;
import teetime.util.concurrent.spsc.Pow2;
import teetime.util.list.CommittableQueue;

public class Distributor<T> extends ConsumerStage<T, T> {

	private final List<OutputPort<T>> outputPortList = new ArrayList<OutputPort<T>>();

	private OutputPort<T>[] outputPorts;
	private int nextOutputPortIndex;

	private int size;

	private int mask;

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
		OutputPort<T> outputPort = this.outputPorts[this.nextOutputPortIndex % this.size];
		this.nextOutputPortIndex++;
		outputPort.send(element);
	}

	@Override
	public void onIsPipelineHead() {
		for (OutputPort op : this.outputPorts) {
			op.send(END_SIGNAL);
			System.out.println("End signal sent, size: " + op.pipe.size() + ", end signal:" + (op.pipe.readLast() == END_SIGNAL));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onStart() {
		this.size = this.outputPortList.size();
		// this.mask = this.size - 1;

		int sizeInPow2 = Pow2.findNextPositivePowerOfTwo(this.size); // is not necessary so far
		this.outputPorts = this.outputPortList.toArray(new OutputPort[sizeInPow2]);
		System.out.println("outputPorts: " + this.outputPorts);
	}

	public OutputPort<T> getNewOutputPort() {
		OutputPort<T> outputPort = new OutputPort<T>();
		this.outputPortList.add(outputPort);
		return outputPort;
	}

}
