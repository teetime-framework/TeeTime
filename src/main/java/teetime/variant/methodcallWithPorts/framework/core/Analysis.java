package teetime.variant.methodcallWithPorts.framework.core;

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

	private final Configuration configuration;

	private final List<Thread> consumerThreads = new LinkedList<Thread>();
	private final List<Thread> finiteProducerThreads = new LinkedList<Thread>();
	private final List<Thread> infiniteProducerThreads = new LinkedList<Thread>();

	private final Collection<Pair<Thread, Throwable>> exceptions = new ConcurrentLinkedQueue<Pair<Thread, Throwable>>();

	public Analysis(final Configuration configuration) {
		this.configuration = configuration;
	}

	public void init() {
		for (HeadStage stage : this.configuration.getConsumerStages()) {
			Thread thread = new Thread(new RunnableStage(stage));
			this.consumerThreads.add(thread);
		}

		for (HeadStage stage : this.configuration.getFiniteProducerStages()) {
			Thread thread = new Thread(new RunnableStage(stage));
			this.finiteProducerThreads.add(thread);
		}

		for (HeadStage stage : this.configuration.getInfiniteProducerStages()) {
			Thread thread = new Thread(new RunnableStage(stage));
			this.infiniteProducerThreads.add(thread);
		}
	}

	/**
	 *
	 * @return a map of thread/throwable pair
	 */
	public Collection<Pair<Thread, Throwable>> start() {
		// start analysis
		for (Thread thread : this.consumerThreads) {
			thread.setUncaughtExceptionHandler(this);
			thread.start();
		}

		for (Thread thread : this.finiteProducerThreads) {
			thread.setUncaughtExceptionHandler(this);
			thread.start();
		}

		for (Thread thread : this.infiniteProducerThreads) {
			thread.setUncaughtExceptionHandler(this);
			thread.start();
		}

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

	public Configuration getConfiguration() {
		return this.configuration;
	}

	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		this.exceptions.add(Pair.of(t, e));
	}
}
