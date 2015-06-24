package teetime.stage.taskfarm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.RunnableConsumerStage;
import teetime.framework.Stage;
import teetime.framework.exceptionHandling.TaskFarmInvalidPipeException;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.NoopFilter;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

@SuppressWarnings("deprecation")
public class TaskFarmStage<I, O, TFS extends Stage & TaskFarmDuplicable<I, O>> extends AbstractCompositeStage {

	// creates SpScPipes (monitorable)
	private static final IPipeFactory INTER_PIPE_FACTORY = PipeFactoryRegistry.INSTANCE
			.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);

	private static final IPipeFactory INTRA_PIPE_FACTORY = PipeFactoryRegistry.INSTANCE
			.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);

	private final Distributor<I> distributor = new Distributor<I>();
	private final Merger<O> merger = new Merger<O>();
	private final NoopFilter<I> noopI = new NoopFilter<I>();

	private final Map<Integer, TaskFarmTriple<?, ?, ?>> triples = new HashMap<Integer, TaskFarmTriple<?, ?, ?>>();
	private final List<Thread> threads = new LinkedList<Thread>();
	private final TFS workerStage;

	public TaskFarmStage(final TFS workerStage) {
		this.workerStage = workerStage;
		this.lastStages.add(this.merger);

		init(workerStage);
	}

	@Override
	public void terminate() {
		super.terminate();
		try {
			for (Thread thread : this.threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
			for (Thread thread : this.threads) {
				thread.interrupt();
			}
		}
	}

	public InputPort<I> getInputPort() {
		return this.noopI.getInputPort();
	}

	public OutputPort<O> getOutputPort() {
		return this.merger.getOutputPort();
	}

	protected <T> IPipe connectPortsWithReturnValue(final OutputPort<? extends T> out, final InputPort<T> in, final IPipeFactory factory) {
		IPipe pipe = factory.create(out, in);
		containingStages.add(out.getOwningStage());
		containingStages.add(in.getOwningStage());
		return pipe;
	}

	@Override
	protected <T> void connectPorts(final OutputPort<? extends T> out, final InputPort<T> in) {
		connectPortsWithReturnValue(out, in, INTRA_PIPE_FACTORY);
	}

	@Override
	protected Stage getFirstStage() {
		return this.noopI;
	}

	private void init(final TFS includedStage) {
		// checkIfValidAsIncludedStage(includedStage);

		connectPortsWithReturnValue(noopI.getOutputPort(), distributor.getInputPort(), INTRA_PIPE_FACTORY);

		InputPort<I> stageInputPort = includedStage.getInputPort();
		IPipe inputPipe = connectPortsWithReturnValue(this.distributor.getNewOutputPort(), stageInputPort, INTER_PIPE_FACTORY);

		OutputPort<O> stageOutputPort = includedStage.getOutputPort();
		IPipe outputPipe = connectPortsWithReturnValue(stageOutputPort, this.merger.getNewInputPort(), INTER_PIPE_FACTORY);

		checkIfPipeIsMonitorable(inputPipe);
		checkIfPipeIsMonitorable(outputPipe);
		this.triples.put(0, new TaskFarmTriple<I, O, TFS>((IMonitorablePipe) inputPipe, (IMonitorablePipe) outputPipe, includedStage));

		startThread(merger);
		startThread(includedStage);
	}

	private void startThread(final Stage stage) {
		RunnableConsumerStage runnableTaskFarmStage = new RunnableConsumerStage(stage);
		Thread thread = new Thread(runnableTaskFarmStage);
		threads.add(thread);
		thread.start();
	}

	// private void checkIfValidAsIncludedStage(final AbstractCompositeStage includedStage) {
	// checkInputPorts(includedStage);
	// checkOutputPorts(includedStage);
	// }

	private void checkIfPipeIsMonitorable(final IPipe pipe) {
		if (!(pipe instanceof IMonitorablePipe)) {
			throw new TaskFarmInvalidPipeException("Pipe is not monitorable, which is required for a Task Farm.");
		}
	}

	// private void checkInputPorts(final AbstractCompositeStage includedStage) {
	// InputPort<?>[] stageInputPorts = includedStage.getInputPorts();
	//
	// if (stageInputPorts.length > 1) {
	// throw new TaskFarmInvalidStageException("Included stage has more than one input port.");
	// }
	// if (stageInputPorts.length < 1) {
	// throw new TaskFarmInvalidStageException("Included stage has no input ports.");
	// }
	// }
	//
	// private void checkOutputPorts(final AbstractCompositeStage includedStage) {
	// OutputPort<?>[] stageOutputPorts = includedStage.getOutputPorts();
	//
	// if (stageOutputPorts.length > 1) {
	// throw new TaskFarmInvalidStageException("Included stage has more than one output port.");
	// }
	// if (stageOutputPorts.length < 1) {
	// throw new TaskFarmInvalidStageException("Included stage has no output ports.");
	// }
	// }

}
