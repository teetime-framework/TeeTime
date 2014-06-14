package teetime.examples.throughput.methodcall;

public interface Stage<I, O> {

	public static final Object END_SIGNAL = new Object();

	void execute2();

	InputPort<I> getInputPort();

	OutputPort<O> getOutputPort();
}
