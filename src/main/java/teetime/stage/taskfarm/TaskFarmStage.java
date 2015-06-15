package teetime.stage.taskfarm;

import java.util.HashMap;
import java.util.Map;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

@SuppressWarnings("deprecation")
public class TaskFarmStage<T> extends AbstractCompositeStage {

	// creates SpScPipes (monitorable)
	private static final IPipeFactory INTER_PIPE_FACTORY = PipeFactoryRegistry.INSTANCE
			.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);

	private final Distributor<T> distributor = new Distributor<T>();
	private final Merger<T> merger = new Merger<T>();
	private final AbstractCompositeStage includedStage;

	private final Map<Integer, IMonitorablePipe> inputPipes = new HashMap<Integer, IMonitorablePipe>();
	private final Map<Integer, IMonitorablePipe> outputPipes = new HashMap<Integer, IMonitorablePipe>();

	public TaskFarmStage(final AbstractCompositeStage includedStage) {
		this.includedStage = includedStage;
		this.lastStages.add(this.merger);

		InputPort<T> stageInputPort = (InputPort<T>) this.includedStage.getInputPorts()[0];
		IPipe inputPipe = connectPortsWithReturnValue(this.distributor.getNewOutputPort(), stageInputPort);
		inputPipes.put(0, (IMonitorablePipe) inputPipe);

		OutputPort<T> stageOutputPort = (OutputPort<T>) this.includedStage.getOutputPorts()[0];
		IPipe outputPipe = connectPortsWithReturnValue(stageOutputPort, this.merger.getNewInputPort());
		outputPipes.put(0, (IMonitorablePipe) outputPipe);

		// TODO Check if getInputPorts returns InputPorts of T (same for OutputPorts)
		// TODO Check if there is only one input and output port each of the included stage (may not have more than one last stages)
		// TODO Check if input and output pipe are indeed of IMonitorablePipe
		// TODO Do simple test analysis...
	}

	@Override
	protected Stage getFirstStage() {
		return this.distributor;
	}

	protected IPipe connectPortsWithReturnValue(final OutputPort<? extends T> out, final InputPort<T> in) {
		// TODO return exception if connectPorts is called within TaskFarmStage
		IPipe pipe = INTER_PIPE_FACTORY.create(out, in);
		containingStages.add(out.getOwningStage());
		containingStages.add(in.getOwningStage());
		return pipe;
	}

}
