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
