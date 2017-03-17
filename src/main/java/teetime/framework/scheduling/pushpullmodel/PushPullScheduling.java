package teetime.framework.scheduling.pushpullmodel;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.*;
import teetime.framework.Traverser.Direction;
import teetime.framework.signal.ValidatingSignal;
import teetime.framework.validation.AnalysisNotValidException;
import teetime.util.framework.concurrent.SignalingCounter;

public class PushPullScheduling implements TeeTimeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PushPullScheduling.class);

	private static final StageFacade SCHEDULING_FACADE = StageFacade.INSTANCE;

	private final List<Thread> consumerThreads = Collections.synchronizedList(new LinkedList<Thread>());
	private final List<Thread> finiteProducerThreads = Collections.synchronizedList(new LinkedList<Thread>());
	private final List<Thread> infiniteProducerThreads = Collections.synchronizedList(new LinkedList<Thread>());

	private final SignalingCounter runnableCounter = new SignalingCounter();
	private final Set<AbstractStage> threadableStages = Collections.synchronizedSet(new HashSet<AbstractStage>());

	private final Configuration configuration;

	public PushPullScheduling(final Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void onInitialize() {
		Collection<AbstractStage> startStages = configuration.getStartStages();

		Set<AbstractStage> newThreadableStages = initialize(startStages);
		startThreads(newThreadableStages);
	}

	public void startStageAtRuntime(final AbstractStage newStage) {
		newStage.declareActive();
		List<AbstractStage> newStages = Arrays.asList(newStage);

		Set<AbstractStage> newThreadableStages = initialize(newStages);
		startThreads(newThreadableStages);

		sendStartingSignal(newThreadableStages);
	}

	// extracted for runtime use
	private Set<AbstractStage> initialize(final Collection<AbstractStage> startStages) {
		if (startStages.isEmpty()) {
			throw new IllegalStateException("The start stage may not be null.");
		}

		A1ThreadableStageCollector stageCollector = new A1ThreadableStageCollector();
		Traverser traversor = new Traverser(stageCollector, Direction.BOTH);
		for (AbstractStage startStage : startStages) {
			traversor.traverse(startStage);
		}

		Set<AbstractStage> newThreadableStages = stageCollector.getThreadableStages();

		threadableStages.addAll(newThreadableStages);
		if (threadableStages.isEmpty()) {
			throw new IllegalStateException("1004 - No threadable stages in this configuration.");
		}

		A2InvalidThreadAssignmentCheck checker = new A2InvalidThreadAssignmentCheck(newThreadableStages);
		checker.check();

		A3PipeInstantiation pipeVisitor = new A3PipeInstantiation();
		traversor = new Traverser(pipeVisitor, Direction.BOTH);
		for (AbstractStage startStage : startStages) {
			traversor.traverse(startStage);
		}

		A4StageAttributeSetter attributeSetter = new A4StageAttributeSetter(configuration, newThreadableStages);
		attributeSetter.setAttributes();

		for (AbstractStage stage : newThreadableStages) {
			categorizeThreadableStage(stage);
			// watchTerminationThread.addConsumerStage(stage);
		}

		return newThreadableStages;
	}

	private void categorizeThreadableStage(final AbstractStage stage) {
		TerminationStrategy terminationStrategy = SCHEDULING_FACADE.getTerminationStrategy(stage);

		switch (terminationStrategy) {
		case BY_INTERRUPT: {
			Thread thread = SCHEDULING_FACADE.getOwningThread(stage);
			infiniteProducerThreads.add(thread);
			break;
		}
		case BY_SELF_DECISION: {
			Thread thread = SCHEDULING_FACADE.getOwningThread(stage);
			finiteProducerThreads.add(thread);
			break;
		}
		case BY_SIGNAL: {
			Thread thread = SCHEDULING_FACADE.getOwningThread(stage);
			consumerThreads.add(thread);
			break;
		}
		default:
			LOGGER.warn("Unknown termination strategy '{}' in stage {}", terminationStrategy, stage);
			break;
		}
	}

	private void startThreads(final Set<AbstractStage> threadableStages) {
		for (AbstractStage stage : threadableStages) {
			SCHEDULING_FACADE.getOwningThread(stage).start();
		}
	}

	private void sendStartingSignal(final Set<AbstractStage> newThreadableStages) {
		// TODO why synchronized?
		synchronized (newThreadableStages) {
			for (AbstractStage stage : newThreadableStages) {
				((TeeTimeThread) SCHEDULING_FACADE.getOwningThread(stage)).sendStartingSignal();
			}
		}
	}

	@Override
	public void onValidate() {
		// BETTER validate concurrently
		for (AbstractStage stage : threadableStages) {
			final ValidatingSignal validatingSignal = new ValidatingSignal(); // NOPMD we need a new instance every iteration
			stage.onSignal(validatingSignal, null);
			if (validatingSignal.getInvalidPortConnections().size() > 0) {
				throw new AnalysisNotValidException(validatingSignal.getInvalidPortConnections());
			}
		}
	}

	@Override
	public void onExecute() {
		sendStartingSignal(threadableStages);
	}

	@Override
	public void onTerminate() {
		abortStages(threadableStages);
	}

	private void abortStages(final Set<AbstractStage> currentTreadableStages) {
		synchronized (currentTreadableStages) {
			for (AbstractStage stage : currentTreadableStages) {
				SCHEDULING_FACADE.abort(stage);
			}
		}
	}

	@Override
	public void onFinish() {
		try {
			runnableCounter.waitFor(0);
		} catch (InterruptedException e) {
			LOGGER.error("Execution has stopped unexpectedly", e);
			for (Thread thread : this.finiteProducerThreads) {
				thread.interrupt();
			}

			for (Thread thread : this.consumerThreads) {
				thread.interrupt();
			}
		}

		if (!this.infiniteProducerThreads.isEmpty()) {
			LOGGER.debug("Interrupting infiniteProducerThreads...");
			for (Thread thread : this.infiniteProducerThreads) {
				thread.interrupt();
			}
			LOGGER.debug("infiniteProducerThreads have been terminated");
		}

		// List<Exception> exceptions = collectExceptions();
		// if (!exceptions.isEmpty()) {
		// throw new ExecutionException(exceptions);
		// }
	}

	// TODO impl throw exception... see line 175
	// private List<Exception> collectExceptions() {
	// // Collection<ThreadThrowableContainer> exceptions = new ConcurrentLinkedQueue<ThreadThrowableContainer>();
	// List<Exception> exceptions = new ArrayList<Exception>();
	//
	// // for (Stage stage : threadableStages.keySet()) {
	// // List<Exception> stageExceptions = stage.exceptionListener.getExceptions();
	// // exceptions.addAll(stageExceptions);
	// // }
	//
	// return exceptions;
	// }

	// @Override
	// void merge(final ThreadService source) {
	// threadableStages.putAll(source.getThreadableStages());
	// // runnableCounter.inc(source.runnableCounter);
	// }

	public SignalingCounter getRunnableCounter() {
		return runnableCounter;
	}

}
