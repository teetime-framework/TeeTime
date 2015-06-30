package teetime.stage.taskfarm;

import java.util.HashMap;
import java.util.Map;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.exceptionHandling.TaskFarmInvalidPipeException;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;
import teetime.stage.NoopFilter;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

@SuppressWarnings("deprecation")
public class TaskFarmStage<I, O, TFS extends TaskFarmDuplicable<I, O>> extends AbstractCompositeStage {

	private final Distributor<I> distributor = new Distributor<I>();
	private final Merger<O> merger = new Merger<O>();
	private final NoopFilter<I> noopI = new NoopFilter<I>();

	private final Map<Integer, TaskFarmTriple<I, O, TFS>> triples = new HashMap<Integer, TaskFarmTriple<I, O, TFS>>();
	private final TFS workerStage;

	public TaskFarmStage(final TFS workerStage) {
		super(null); // FIXME add context
		this.workerStage = workerStage;

		init(workerStage);
	}

	public InputPort<I> getInputPort() {
		return this.noopI.getInputPort();
	}

	public OutputPort<O> getOutputPort() {
		return this.merger.getOutputPort();
	}

	private void init(final TFS includedStage) {
		connectPorts(noopI.getOutputPort(), distributor.getInputPort());

		InputPort<I> stageInputPort = includedStage.getInputPort();
		connectPorts(this.distributor.getNewOutputPort(), stageInputPort);

		OutputPort<O> stageOutputPort = includedStage.getOutputPort();
		connectPorts(stageOutputPort, this.merger.getNewInputPort());

		checkIfPipeIsMonitorable(stageInputPort.getPipe());
		checkIfPipeIsMonitorable(stageOutputPort.getPipe());
		this.triples.put(0, new TaskFarmTriple<I, O, TFS>(
				(IMonitorablePipe) stageInputPort.getPipe(),
				(IMonitorablePipe) stageOutputPort.getPipe(),
				includedStage));

		addThreadableStage(merger);
		addThreadableStage(workerStage.getInputPort().getOwningStage());
	}

	private void checkIfPipeIsMonitorable(final IPipe pipe) {
		if (!(pipe instanceof IMonitorablePipe)) {
			throw new TaskFarmInvalidPipeException("Pipe is not monitorable, which is required for a Task Farm.");
		}
	}

}
