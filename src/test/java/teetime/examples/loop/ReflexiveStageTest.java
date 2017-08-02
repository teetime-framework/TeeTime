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
package teetime.examples.loop;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import teetime.stage.StatelessCounter;

public class ReflexiveStageTest {

	private StatelessCounter<Integer> reflexiveStage;

	@Before
	public void before() {
		reflexiveStage = new StatelessCounter<Integer>();
	}

	@Test(timeout = 200)
	@Ignore("requires loop detection")
	public void reflexiveStageShouldExecute() throws Exception {
		final List<Integer> INPUT_ELEMENTS = Arrays.asList(1, 2, 3, 4, 5);
		final List<Integer> EXPECTED_OUTPUT_ELEMENTS = new ArrayList<Integer>(INPUT_ELEMENTS);

		List<Integer> outputElements = new ArrayList<Integer>();

		test(reflexiveStage).and()
				.send(INPUT_ELEMENTS).to(reflexiveStage.getInputPort()).and()
				.receive(outputElements).from(reflexiveStage.getOutputPort()).and()
				.start();

		assertThat(outputElements, is(EXPECTED_OUTPUT_ELEMENTS));
	}
}
