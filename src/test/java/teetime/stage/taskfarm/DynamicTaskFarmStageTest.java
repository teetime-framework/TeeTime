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
package teetime.stage.taskfarm;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;

public class DynamicTaskFarmStageTest {

	@Test
	@Ignore("declareActive at runtime is required, but not yet implemented/merged")
	public void testDynamicTaskFarmStage() throws Exception {
		final Integer[] elements = { 1, 2, 3 };

		final ElementTrigger<Integer> producer = new ElementTrigger<Integer>(elements);
		final DynamicTaskFarmStage<Integer, Integer, Counter<Integer>> dynamicTaskFarmStage = new DynamicTaskFarmStage<Integer, Integer, Counter<Integer>>(
				new Counter<Integer>(), 1);
		final CollectorSink<Integer> collectorSink = new CollectorSink<Integer>();

		Configuration configuration = new Configuration() {
			{
				connectPorts(producer.getOutputPort(), dynamicTaskFarmStage.getInputPort());
				connectPorts(dynamicTaskFarmStage.getOutputPort(), collectorSink.getInputPort());
			}
		};
		Execution<Configuration> execution = new Execution<Configuration>(configuration);
		execution.executeNonBlocking();

		producer.trigger();
		assertThat(dynamicTaskFarmStage.getMerger().isActive(), is(false));

		dynamicTaskFarmStage.addStageAtRuntime();
		producer.trigger();
		// assertThat(dynamicTaskFarmStage.getMerger().isActive(), is(true)); // TODO uncomment if "declareActive at runtime" is implemented

		dynamicTaskFarmStage.removeStageAtRuntime();
		producer.trigger();
		// assertThat(dynamicTaskFarmStage.getMerger().isActive(), is(false)); // TODO uncomment if "declareActive at runtime" is implemented

		execution.abortEventually();

	}
}
