package teetime.framework;

import static org.junit.Assert.assertEquals;

import java.lang.Thread.State;

import org.junit.Test;

public class RunnableConsumerStageTest {

	@Test
	public void testWaitingInfinitely() throws Exception {
		WaitStrategyConfiguration waitStrategyConfiguration = new WaitStrategyConfiguration(300, 42);

		final Analysis analysis = new Analysis(waitStrategyConfiguration);
		analysis.init();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				start(analysis); // FIXME react on exceptions
			}
		});
		thread.start();

		Thread.sleep(200);

		assertEquals(State.WAITING, thread.getState());
		assertEquals(0, waitStrategyConfiguration.getCollectorSink().getElements().size());
	}

	@Test
	public void testWaitingFinitely() throws Exception {
		WaitStrategyConfiguration waitStrategyConfiguration = new WaitStrategyConfiguration(300, 42);

		final Analysis analysis = new Analysis(waitStrategyConfiguration);
		analysis.init();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				start(analysis); // FIXME react on exceptions
			}
		});
		thread.start();

		Thread.sleep(400);

		assertEquals(State.TERMINATED, thread.getState());
		assertEquals(42, waitStrategyConfiguration.getCollectorSink().getElements().get(0));
		assertEquals(1, waitStrategyConfiguration.getCollectorSink().getElements().size());
	}

	@Test
	public void testYieldRun() throws Exception {
		YieldStrategyConfiguration waitStrategyConfiguration = new YieldStrategyConfiguration(42);

		final Analysis analysis = new Analysis(waitStrategyConfiguration);
		analysis.init();

		start(analysis);

		assertEquals(42, waitStrategyConfiguration.getCollectorSink().getElements().get(0));
		assertEquals(1, waitStrategyConfiguration.getCollectorSink().getElements().size());
	}

	private void start(final Analysis analysis) {
		analysis.start();
		assertEquals(0, 0);
	}
}
