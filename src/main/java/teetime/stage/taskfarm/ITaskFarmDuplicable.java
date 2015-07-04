package teetime.stage.taskfarm;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

/**
 * Any Stage or AbstractCompositeStage implementing this interface
 * can be used by a Task Farm as an enclosed stage. The enclosed
 * stage may not have more than one input or output port each.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output Type of Task Farm
 */
public interface ITaskFarmDuplicable<I, O> {

	/**
	 * Creates a new instance of the enclosed stage.
	 *
	 * @return new instance
	 */
	public ITaskFarmDuplicable<I, O> duplicate();

	/**
	 * @return single input port of the enclosed stage
	 */
	public InputPort<I> getInputPort();

	/**
	 * @return single output port of the enclosed stage
	 */
	public OutputPort<O> getOutputPort();
}
