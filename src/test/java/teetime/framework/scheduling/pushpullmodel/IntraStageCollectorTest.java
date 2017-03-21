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

import org.junit.Test;

import teetime.framework.*;

import teetime.framework.scheduling.pushpullmodel.IntraStageCollector;

public class IntraStageCollectorTest {

	@Test
	public void testVisitedStages() {
		TestConfiguration config = new TestConfiguration();

		Traverser traversor = new Traverser(new IntraStageCollector(config.init));
		traversor.traverse(config.init);

		assertThat(traversor.getVisitedStages(), containsInAnyOrder(config.init, config.f2b, config.distributor));
	}
}
