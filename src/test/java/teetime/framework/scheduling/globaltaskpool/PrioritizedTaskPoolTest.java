package teetime.framework.scheduling.globaltaskpool;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teetime.framework.AbstractStage;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;

public class PrioritizedTaskPoolTest {

	private PrioritizedTaskPool threadPool;
	private InitialElementProducer producer;
	private Counter counter1;
	private Counter counter2;
	private Counter counter3;
	private Counter counter4;

	@Before
	public void setUp() throws Exception {
		threadPool = new PrioritizedTaskPool(5);

		producer = new InitialElementProducer<>();
		producer.setLevelIndex(0);
		counter1 = new Counter<>();
		counter1.setLevelIndex(1);
		counter2 = new Counter<>();
		counter2.setLevelIndex(2);
		counter3 = new Counter<>();
		counter3.setLevelIndex(3);
		counter4 = new Counter<>();
		counter4.setLevelIndex(4);
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
	public void insertTwice() throws Exception {
		boolean scheduled = threadPool.scheduleStage(producer);
		assertThat(scheduled, is(true));
		scheduled = threadPool.scheduleStage(producer);
		assertThat(scheduled, is(true));

		AbstractStage nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(producer));
		nextStage = threadPool.removeNextStage();
		assertThat(nextStage, is(producer));
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
