package teetime.framework;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class IntraStageCollectorTest {

	@Test
	public void testVisitedStages() {
		TestConfiguration config = new TestConfiguration();

		Traverser traversor = new Traverser(new IntraStageCollector(config.init));
		traversor.traverse(config.init);

		assertThat(traversor.getVisitedStages(), containsInAnyOrder(config.init, config.f2b, config.distributor));
	}
}
