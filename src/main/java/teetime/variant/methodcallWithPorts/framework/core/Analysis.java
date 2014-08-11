package teetime.variant.methodcallWithPorts.framework.core;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Analysis {

	private static final Logger LOGGER = LoggerFactory.getLogger(Analysis.class);

	private final Configuration configuration;

	private final List<Thread> consumerThreads = new LinkedList<Thread>();
	private final List<Thread> finiteProducerThreads = new LinkedList<Thread>();
	private final List<Thread> infiniteProducerThreads = new LinkedList<Thread>();

	public Analysis(final Configuration configuration) {
		this.configuration = configuration;
	}

	public void init() {
		for (StageWithPort stage : this.configuration.getConsumerStages()) {
			Thread thread = new Thread(new RunnableStage(stage));
			this.consumerThreads.add(thread);
		}

		for (StageWithPort stage : this.configuration.getFiniteProducerStages()) {
			Thread thread = new Thread(new RunnableStage(stage));
			this.finiteProducerThreads.add(thread);
		}

		for (StageWithPort stage : this.configuration.getInfiniteProducerStages()) {
			Thread thread = new Thread(new RunnableStage(stage));
			this.infiniteProducerThreads.add(thread);
		}
	}

	public void start() {
		// start analysis
		for (Thread thread : this.consumerThreads) {
			thread.start();
		}

		for (Thread thread : this.finiteProducerThreads) {
			thread.start();
		}

		for (Thread thread : this.infiniteProducerThreads) {
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
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}
}
