package teetime.framework.scheduling.globaltaskqueue;

import java.util.List;

import org.jctools.queues.MpmcArrayQueue;

import teetime.framework.*;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;

public class TeeTimeTaskQueueThreadChw extends Thread {

	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;

	private static final AbstractStage TERM_STAGE = new AbstractStage() {
		@Override
		protected void execute() throws Exception {// do nothing
		}
	};

	private final GlobalTaskQueueScheduling scheduling;
	private final MpmcArrayQueue<AbstractStage> taskQueue;

	public TeeTimeTaskQueueThreadChw(final GlobalTaskQueueScheduling scheduling) {
		super();
		this.scheduling = scheduling;
		this.taskQueue = scheduling.getTaskQueue();
	}

	@Override
	public void run() {
		while (true) {
			AbstractStage stage = taskQueue.poll();
			if (stage == TERM_STAGE) {
				// TODO terminate thread
			} else if (stage != null) {
				executeStage(stage);
			} else {
				// boolean finiteProducerStagesRunning = taskQueue.addAll(scheduling.getFiniteProducerStages());

				// if (finiteProducerStagesRunning) {
				// taskQueue.addAll(scheduling.getInfiniteProducerStages());
				// } else {
				// break;
				// }
			}
		}
	}

	private void executeStage(final AbstractStage stage) {
		int numOfExecutions = 1;

		STAGE_FACADE.runStage(stage, numOfExecutions);

		// Add all successor stages so that they will be executed afterwards.
		// TODO evaluate whether adding workless stages is faster than adding stages within pipes.
		List<OutputPort<?>> outputPorts = STAGE_FACADE.getOutputPorts(stage);
		for (OutputPort<?> outputPort : outputPorts) {
			AbstractStage targetStage = outputPort.getPipe().getTargetPort().getOwningStage();
			if (!STAGE_FACADE.shouldBeTerminated(targetStage) && targetStage.getCurrentState() != StageState.TERMINATED) {
				taskQueue.add(targetStage);
			}
		}

		if (STAGE_FACADE.shouldBeTerminated(stage)) {
			afterStageExecution(stage);
		}
	}

	private void afterStageExecution(final AbstractStage stage) {
		if (stage instanceof AbstractProducerStage) {
			stage.onSignal(new TerminatingSignal(), null);
		} else if (stage instanceof AbstractConsumerStage) {
			final ISignal signal = new TerminatingSignal(); // NOPMD DU caused by loop
			for (InputPort<?> inputPort : STAGE_FACADE.getInputPorts(stage)) {
				stage.onSignal(signal, inputPort);
			}
		}
	}
}
