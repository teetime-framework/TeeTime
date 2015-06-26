package teetime.stage.basic;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public interface ITransformation<I, O> {

	public abstract InputPort<I> getInputPort();

	public abstract OutputPort<O> getOutputPort();

}
