package teetime.stage.taskfarm;

import java.util.HashMap;
import java.util.Map;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.framework.exceptionHandling.TaskFarmInvalidPipeException;
import teetime.framework.exceptionHandling.TaskFarmInvalidStageException;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

@SuppressWarnings("deprecation")
public class TaskFarmStage<I, O> extends AbstractCompositeStage {

	// creates SpScPipes (monitorable)
	private static final IPipeFactory INTER_PIPE_FACTORY = PipeFactoryRegistry.INSTANCE
			.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);

	private final Distributor<I> distributor = new Distributor<I>();
	private final Merger<O> merger = new Merger<O>();

	private final Map<Integer, AbstractCompositeStage> includedStages = new HashMap<Integer, AbstractCompositeStage>();
	private final Map<Integer, IMonitorablePipe> inputPipes = new HashMap<Integer, IMonitorablePipe>();
	private final Map<Integer, IMonitorablePipe> outputPipes = new HashMap<Integer, IMonitorablePipe>();

	public TaskFarmStage(final AbstractCompositeStage includedStage) {
		this.includedStages.put(0, includedStage);
		this.lastStages.add(this.merger);

		checkIfValidAsIncludedStage(includedStage);

		@SuppressWarnings("unchecked")
		InputPort<I> stageInputPort = (InputPort<I>) includedStage.getInputPorts()[0];
		IPipe inputPipe = connectPortsWithReturnValue(this.distributor.getNewOutputPort(), stageInputPort);
		checkIfPipeIsMonitorable(inputPipe);
		this.inputPipes.put(0, (IMonitorablePipe) inputPipe);

		@SuppressWarnings("unchecked")
		OutputPort<O> stageOutputPort = (OutputPort<O>) includedStage.getOutputPorts()[0];
		IPipe outputPipe = connectPortsWithReturnValue(stageOutputPort, this.merger.getNewInputPort());
		checkIfPipeIsMonitorable(outputPipe);
		this.outputPipes.put(0, (IMonitorablePipe) outputPipe);
	}

	private void checkIfPipeIsMonitorable(final IPipe pipe) {
		if (!(pipe instanceof IMonitorablePipe)) {
			throw new TaskFarmInvalidPipeException("Pipe is not monitorable, which is required for a Task Farm.");
		}
	}

	private void checkIfValidAsIncludedStage(final AbstractCompositeStage includedStage) {
		checkInputPorts(includedStage);
		checkOutputPorts(includedStage);
	}

	private void checkOutputPorts(final AbstractCompositeStage includedStage) {
		OutputPort<?>[] stageOutputPorts = includedStage.getOutputPorts();

		if (stageOutputPorts.length > 1) {
			throw new TaskFarmInvalidStageException("Included stage has more than one output port.");
		}
		if (stageOutputPorts.length < 1) {
			throw new TaskFarmInvalidStageException("Included stage has no output ports.");
		}

		try {
			@SuppressWarnings("all")
			OutputPort<O> _ = (OutputPort<O>) stageOutputPorts[0];
		} catch (Exception _) {
			throw new TaskFarmInvalidStageException("Output port of included stage does not have the same type as the Task Farm.");
		}
	}

	private void checkInputPorts(final AbstractCompositeStage includedStage) {
		InputPort<?>[] stageInputPorts = includedStage.getInputPorts();

		if (stageInputPorts.length > 1) {
			throw new TaskFarmInvalidStageException("Included stage has more than one input port.");
		}
		if (stageInputPorts.length < 1) {
			throw new TaskFarmInvalidStageException("Included stage has no input ports.");
		}

		try {
			@SuppressWarnings("all")
			InputPort<I> _ = (InputPort<I>) stageInputPorts[0];
		} catch (Exception _) {
			throw new TaskFarmInvalidStageException("Input port of included stage does not have the same type as the Task Farm.");
		}
	}

	@Override
	protected Stage getFirstStage() {
		return this.distributor;
	}

	@Override
	protected <T> void connectPorts(final OutputPort<? extends T> out, final InputPort<T> in) {
		throw new RuntimeException("\"connectPorts\" should not be called inside a Task Farm. Use \"connectPortsWithReturnValue\" instead.");
	}

	protected <T> IPipe connectPortsWithReturnValue(final OutputPort<? extends T> out, final InputPort<T> in) {
		IPipe pipe = INTER_PIPE_FACTORY.create(out, in);
		containingStages.add(out.getOwningStage());
		containingStages.add(in.getOwningStage());
		return pipe;
	}

	public InputPort<I> getInputPort() {
		return this.distributor.getInputPort();
	}

	public OutputPort<O> getOutputPort() {
		return this.merger.getOutputPort();
	}

}
