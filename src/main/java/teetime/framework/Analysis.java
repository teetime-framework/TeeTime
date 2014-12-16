package teetime.framework;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.util.Pair;

public class Analysis implements UncaughtExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(Analysis.class);

	private final AnalysisConfiguration configuration;

	private final List<Thread> consumerThreads = new LinkedList<Thread>();
	private final List<Thread> finiteProducerThreads = new LinkedList<Thread>();
	private final List<Thread> infiniteProducerThreads = new LinkedList<Thread>();

	private final Collection<Pair<Thread, Throwable>> exceptions = new ConcurrentLinkedQueue<Pair<Thread, Throwable>>();

	public Analysis(final AnalysisConfiguration configuration) {
		this.configuration = configuration;
	}

	public void init() {
		final List<Stage> threadableStageJobs = this.configuration.getThreadableStageJobs();
		for (Stage stage : threadableStageJobs) {
			final Thread thread = new Thread(new RunnableStage(stage));
			switch (stage.getTerminationStrategy()) {
			case BY_SIGNAL:
				this.consumerThreads.add(thread);
				break;
			case BY_SELF_DECISION:
				this.finiteProducerThreads.add(thread);
				break;
			case BY_INTERRUPT:
				this.infiniteProducerThreads.add(thread);
				break;
			default:
				break;
			}
		}

	}

	/**
	 *
	 * @return a collection of thread/throwable pairs
	 */
	public Collection<Pair<Thread, Throwable>> start() {
		// start analysis
		startThreads(this.consumerThreads);
		startThreads(this.finiteProducerThreads);
		startThreads(this.infiniteProducerThreads);

		// wait for the analysis to complete
		try {
			for (Thread thread : this.finiteProducerThreads) {
				thread.join();
			}

			for (Thread thread : this.consumerThreads) {
				thread.join();
			}
		} catch (InterruptedException e) {
			LOGGER.error("Analysis has stopped unexpectedly", e);

			for (Thread thread : this.finiteProducerThreads) {
				thread.interrupt();
			}

			for (Thread thread : this.consumerThreads) {
				thread.interrupt();
			}
		}

		for (Thread thread : this.infiniteProducerThreads) {
			thread.interrupt();
		}

		return this.exceptions;
	}

	private void startThreads(final Iterable<Thread> threads) {
		for (Thread thread : threads) {
			thread.setUncaughtExceptionHandler(this);
			thread.start();
		}
	}

	public AnalysisConfiguration getConfiguration() {
		return this.configuration;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable throwable) {
		this.exceptions.add(Pair.of(thread, throwable));
	}
}
