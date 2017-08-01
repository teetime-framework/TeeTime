package teetime.framework.scheduling.pushpullmodel;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import teetime.framework.AbstractStage;
import teetime.framework.ConfigurationFacade;
import teetime.framework.TestConfiguration;
import teetime.framework.Traverser;

public class A1ThreadableStageCollectorTest {

	@Test
	public void testVisit() throws Exception {
		TestConfiguration config = new TestConfiguration();
		Collection<AbstractStage> startStages = ConfigurationFacade.INSTANCE.getStartStages(config);

		A1ThreadableStageCollector stageCollector = new A1ThreadableStageCollector();
		Traverser traversor = new Traverser(stageCollector);
		for (AbstractStage startStage : startStages) {
			traversor.traverse(startStage);
		}

		Set<AbstractStage> newThreadableStages = stageCollector.getThreadableStages();
		assertThat(newThreadableStages, hasSize(4));
	}
}
