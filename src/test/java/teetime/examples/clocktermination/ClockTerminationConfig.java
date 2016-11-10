package teetime.examples.clocktermination;

import teetime.framework.Configuration;
import teetime.stage.Clock;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.Sink;

/**
 * This configuration is special because it did not terminate in previous versions of teetime.
 * In these versions, all stages were collected which were reachable from the stage which was connected first by <code>connectPorts()</code>.
 * Since the <code>firstProducer</code> cannot be reached by this stage (here: the clock stage),
 * the <code>firstProducer</code> is not recognized as a stage and especially not as a producer stage.
 * Since the clock is an infinite producer, the execution waits for its termination an infinite amount of time.
 *
 * @author Christian Wulf
 *
 */
public class ClockTerminationConfig extends Configuration {

	public ClockTerminationConfig() {
		InitialElementProducer<Integer> firstProducer = new InitialElementProducer<Integer>(0, 1);
		Clock clock = new Clock();

		Sink<Integer> firstSink = new Sink<Integer>();
		Sink<Long> secondSink = new Sink<Long>();

		connectPorts(firstProducer.getOutputPort(), firstSink.getInputPort());
		connectPorts(clock.getOutputPort(), secondSink.getInputPort());
	}
}
