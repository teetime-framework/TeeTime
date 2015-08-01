package teetime.framework;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import teetime.stage.basic.Sink;

public class TerminationTest {

	@Test(timeout = 1000)
	public void correctAbort() throws InterruptedException {
		TerminationConfig configuration = new TerminationConfig(10);
		Execution<TerminationConfig> execution = new Execution<TerminationConfig>(configuration);
		execution.executeNonBlocking();
		execution.abortEventually();
	}

	@Test(timeout = 3000)
	public void doesNotGetStuckInAdd() throws InterruptedException {
		TerminationConfig configuration = new TerminationConfig(1);
		Execution<TerminationConfig> execution = new Execution<TerminationConfig>(configuration);
		execution.executeNonBlocking();
		Thread.sleep(100);
		execution.abortEventually();
		assertThat(configuration.sinkStage.time - 450, is(greaterThan(configuration.init.time)));
	}

	private class TerminationConfig extends Configuration {
		InitialElementProducer<Integer> init = new InitialElementProducer<Integer>(1, 2, 3, 4, 5, 6);
		DoesNotRetrieveElements sinkStage = new DoesNotRetrieveElements();

		public TerminationConfig(final int capacity) {
			if (capacity == 1) {
				connectPorts(init.getOutputPort(), sinkStage.getInputPort(), capacity);
				addThreadableStage(sinkStage);
			} else {
				Sink<Integer> sink = new Sink<Integer>();
				connectPorts(init.getOutputPort(), sink.getInputPort(), capacity);
				addThreadableStage(sink);
			}
		}

	}

	private final class InitialElementProducer<T> extends AbstractProducerStage<T> {

		private final Iterable<T> elements;
		public long time;

		public InitialElementProducer(final T... elements) {
			this.elements = Arrays.asList(elements);
		}

		@Override
		protected void execute() {
			for (final T element : this.elements) {
				this.outputPort.send(element);
			}
			this.terminate();
		}

		@Override
		protected void terminate() {
			time = System.currentTimeMillis();
			super.terminate();
		}

	}

	private class DoesNotRetrieveElements extends AbstractConsumerStage<Integer> {

		public long time;

		@Override
		protected void execute(final Integer element) {
			int i = 0;
			while (true) {
				i++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// First sleep will throw this
				}
				if (i > 1) {
					Thread.currentThread().interrupt();
					time = System.currentTimeMillis();
					break;
				}
			}

		}

		@Override
		protected void terminate() {}

	}

}
