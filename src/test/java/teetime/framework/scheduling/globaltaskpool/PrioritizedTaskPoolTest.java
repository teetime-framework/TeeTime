/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework.scheduling.globaltaskpool;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teetime.framework.AbstractStage;
import teetime.framework.StageFacade;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;

public class PrioritizedTaskPoolTest {

	private PrioritizedTaskPool threadPool;
	private InitialElementProducer<Object> producer;
	private Counter<Object> counter1;
	private Counter<Object> counter2;
	private Counter<Object> counter3;
	private Counter<Object> counter4;

	@Before
	public void setUp() throws Exception {
		threadPool = new PrioritizedTaskPool(5);

		producer = new InitialElementProducer<>();
		StageFacade.INSTANCE.setLevelIndex(producer, 0);
		counter1 = new Counter<>();
		StageFacade.INSTANCE.setLevelIndex(counter1, 1);
		counter2 = new Counter<>();
		StageFacade.INSTANCE.setLevelIndex(counter2, 2);
		counter3 = new Counter<>();
		StageFacade.INSTANCE.setLevelIndex(counter3, 3);
		counter4 = new Counter<>();
		StageFacade.INSTANCE.setLevelIndex(counter4, 4);
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void nextStageIsShallowStageLevel() throws Exception {
		boolean scheduled = threadPool.scheduleStage(producer);
		assertThat(scheduled, is(true));

		AbstractStage nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(producer));
	}

	@Test
	public void nextStageIsDeepStageLevel() throws Exception {
		threadPool.scheduleStage(producer);
		boolean scheduled = threadPool.scheduleStage(counter4);
		assertThat(scheduled, is(true));

		AbstractStage nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter4));
	}

	@Test
	public void nextStageIsDeepStageLevelExplicit() throws Exception {
		threadPool.scheduleStage(producer);
		threadPool.scheduleStage(counter4);

		AbstractStage nextStage = threadPool.removeNextStage(4);
		assertThat(nextStage, is(counter4));
	}

	@Test
	public void skipDeepestStageLevel() throws Exception {
		threadPool.scheduleStage(producer);
		threadPool.scheduleStage(counter4);

		AbstractStage nextStage = threadPool.removeNextStage(3);
		assertThat(nextStage, is(producer));
	}

	@Test
	public void removeInCorrectOrder() throws Exception {
		// add to pool in an arbitrary order
		threadPool.scheduleStage(counter3);
		threadPool.scheduleStage(producer);
		threadPool.scheduleStage(counter2);
		threadPool.scheduleStage(counter4);
		threadPool.scheduleStage(counter1);

		// remove from the pool in a sorted order
		AbstractStage nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter4));
		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter3));
		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter2));
		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter1));
		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(producer));
	}

	@Test
	public void removeInCorrectOrderWhileAddingInBetween() throws Exception {
		// add to pool in an arbitrary order
		threadPool.scheduleStage(counter3);
		threadPool.scheduleStage(producer);
		threadPool.scheduleStage(counter2);
		threadPool.scheduleStage(counter4);
		threadPool.scheduleStage(counter1);

		// remove from the pool in a sorted order
		AbstractStage nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter4));
		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter3));
		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter2));

		threadPool.scheduleStage(counter4);

		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter4));
		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter1));

		threadPool.scheduleStage(counter3);
		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(counter3));

		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(producer));
	}

	@Test
	public void insertTwice() throws Exception {
		boolean scheduled = threadPool.scheduleStage(producer);
		assertThat(scheduled, is(true));
		scheduled = threadPool.scheduleStage(producer);
		assertThat(scheduled, is(true));

		AbstractStage nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(producer));
		nextStage = threadPool.removeNextStage();
		// assertThat(nextStage, is(producer));
		assertThat(nextStage, is(nullValue()));
	}

	@Test
	public void removeTooOften() throws Exception {
		boolean scheduled = threadPool.scheduleStage(producer);
		assertThat(scheduled, is(true));

		AbstractStage nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(producer));
		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(nullValue()));
	}

}
