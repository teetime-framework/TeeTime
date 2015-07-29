package teetime.framework;

import org.junit.Test;

import teetime.stage.InitialElementProducer;

public class TerminationTest {

	@Test(timeout = 2000)
	public void doesNotGetStuckInAdd() throws InterruptedException {
		Execution<TerminationConfig> execution = new Execution<TerminationConfig>(new TerminationConfig());
		execution.executeNonBlocking();
		Thread.sleep(1000);
		execution.abortEventually();
	}

	private class TerminationConfig extends Configuration {
		InitialElementProducer<Integer> init = new InitialElementProducer<Integer>(1, 2, 3, 4, 5, 6);
		DoesNotRetrieveElements sinkStage = new DoesNotRetrieveElements();

		public TerminationConfig() {
			connectPorts(init.getOutputPort(), sinkStage.getInputPort(), 1);
			addThreadableStage(sinkStage);
		}

	}

	private class DoesNotRetrieveElements extends AbstractConsumerStage<Integer> {

		@Override
		protected void execute(final Integer element) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// Will happen in this test
			}

		}

		@Override
		protected void terminate() {
			Thread.currentThread().interrupt();
			System.out.println("TADA " + this.shouldBeTerminated());
		}

	}

}
