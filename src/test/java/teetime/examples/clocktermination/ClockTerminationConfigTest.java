package teetime.examples.clocktermination;

import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.Execution;

public class ClockTerminationConfigTest {

	// @Test(timeout = 500)
	@Test
	public void executeWithoutTimeout() throws Exception {
		ClockTerminationConfig configuration = new ClockTerminationConfig();
		Execution<Configuration> execution = new Execution<Configuration>(configuration);
		execution.executeBlocking();
	}
}
