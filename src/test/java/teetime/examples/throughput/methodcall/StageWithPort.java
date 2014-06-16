package teetime.examples.throughput.methodcall;

public interface StageWithPort<I, O> extends Stage<I, O> {

	InputPort<I> getInputPort();

	OutputPort<O> getOutputPort();

	void executeWithPorts();
}
