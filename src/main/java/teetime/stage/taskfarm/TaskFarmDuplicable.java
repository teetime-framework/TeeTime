package teetime.stage.taskfarm;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public interface TaskFarmDuplicable<I, O> {

	public TaskFarmDuplicable<I, O> duplicate();

	public InputPort<I> getInputPort();

	public OutputPort<O> getOutputPort();
}
