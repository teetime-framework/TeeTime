/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework;

import static org.junit.Assert.assertEquals;

import java.lang.Thread.State;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import teetime.util.Pair;

import com.google.common.base.Joiner;

public class RunnableConsumerStageTest {

	@Test
	public void testWaitingInfinitely() throws Exception {
		RunnableConsumerStageTestConfiguration configuration = new RunnableConsumerStageTestConfiguration();

		final Analysis analysis = new Analysis(configuration);
		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				start(analysis);
			}
		});
		thread.start();

		Thread.sleep(200);

		// assertEquals(State.WAITING, thread.getState());
		assertEquals(State.WAITING, configuration.getConsumerThread().getState());
		assertEquals(0, configuration.getCollectedElements().size());

		// clean up
		configuration.getConsumerThread().interrupt();
		configuration.getConsumerThread().join();
		thread.join();
	}

	// @Test
	// public void testWaitingInfinitely() throws Exception {
	// WaitStrategyConfiguration waitStrategyConfiguration = new WaitStrategyConfiguration(300, 42);
	//
	// final Analysis analysis = new Analysis(waitStrategyConfiguration);
	// Thread thread = new Thread(new Runnable() {
	// @Override
	// public void run() {
	// start(analysis); // FIXME react on exceptions
	// }
	// });
	// thread.start();
	//
	// Thread.sleep(200);
	//
	// assertEquals(State.WAITING, thread.getState());
	// assertEquals(0, waitStrategyConfiguration.getCollectorSink().getElements().size());
	// }

	// @Test
	// public void testWaitingFinitely() throws Exception {
	// WaitStrategyConfiguration waitStrategyConfiguration = new WaitStrategyConfiguration(300, 42);
	//
	// final Analysis analysis = new Analysis(waitStrategyConfiguration);
	// Thread thread = new Thread(new Runnable() {
	// @Override
	// public void run() {
	// start(analysis); // FIXME react on exceptions
	// }
	// });
	// thread.start();
	//
	// Thread.sleep(400);
	//
	// assertEquals(State.TERMINATED, thread.getState());
	// assertEquals(42, waitStrategyConfiguration.getCollectorSink().getElements().get(0));
	// assertEquals(1, waitStrategyConfiguration.getCollectorSink().getElements().size());
	// }

	@Ignore
	@Test
	public void testYieldRun() throws Exception {
		YieldStrategyConfiguration waitStrategyConfiguration = new YieldStrategyConfiguration(42);

		final Analysis analysis = new Analysis(waitStrategyConfiguration);

		start(analysis);

		assertEquals(42, waitStrategyConfiguration.getCollectorSink().getElements().get(0));
		assertEquals(1, waitStrategyConfiguration.getCollectorSink().getElements().size());
	}

	private void start(final Analysis analysis) {
		Collection<Pair<Thread, Throwable>> exceptions = analysis.start();
		for (Pair<Thread, Throwable> pair : exceptions) {
			System.err.println(pair.getSecond());
			System.err.println(Joiner.on("\n").join(pair.getSecond().getStackTrace()));
			throw new RuntimeException(pair.getSecond());
		}
		assertEquals(0, exceptions.size());
	}
}
