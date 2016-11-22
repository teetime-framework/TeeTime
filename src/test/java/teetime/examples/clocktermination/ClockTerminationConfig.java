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
