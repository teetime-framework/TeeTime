package teetime.framework;

import org.junit.Test;

import teetime.stage.InitialElementProducer;

public class TerminationTest {

	@Test(timeout = 2000)
	public void doesNotGetStuckInAdd() throws InterruptedException {
		Execution<TerminationConfig> execution = new Execution<TerminationConfig>(new TerminationConfig(1));
		execution.executeNonBlocking();
		Thread.sleep(500);
		execution.abortEventually();
	}

	private class TerminationConfig extends Configuration {
		InitialElementProducer<Integer> init = new InitialElementProducer<Integer>(1, 2, 3, 4, 5, 6);
		DoesNotRetrieveElements sinkStage = new DoesNotRetrieveElements();

		public TerminationConfig(final int capacity) {
			connectPorts(init.getOutputPort(), sinkStage.getInputPort(), capacity);
			addThreadableStage(sinkStage);
		}

	}

	private class DoesNotRetrieveElements extends AbstractConsumerStage<Integer> {

		@Override
		protected void execute(final Integer element) {
			int i = 0;
			while (true) {
				i++;
				if (i > 1) {
					Thread.currentThread().interrupt();
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}

		}

		@Override
		protected void terminate() {}

	}

}
