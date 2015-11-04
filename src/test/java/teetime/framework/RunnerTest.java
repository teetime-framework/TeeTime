package teetime.framework;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RunnerTest {

	@Test
	public void test() {
		assertFalse(RunnerConfig.executed);
		Execution.main(new String[] { "teetime.framework.RunnerConfig" });
		assertTrue(RunnerConfig.executed);
	}

}
