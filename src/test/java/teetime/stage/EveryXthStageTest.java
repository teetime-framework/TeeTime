/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Nils Christian Ehmke
 */
public class EveryXthStageTest {

	private EveryXthStage<Integer> stage;
	private List<Integer> results;

	@Before
	public void initializeStage() {
		this.stage = new EveryXthStage<Integer>(5);
		this.results = new ArrayList<Integer>();
	}

	@Test
	public void notEnoughInputShouldResultInEmptyOutput() {
		test(this.stage).and().send(1, 5, 9, 10).to(this.stage.getInputPort()).and().receive(this.results).from(this.stage.getOutputPort()).start();

		assertThat(this.results, is(empty()));
	}

	@Test
	public void enoughInputShouldResultInCounterValuesBeingSend() {
		test(this.stage).and().send(1, 5, 9, 10, 8).to(this.stage.getInputPort()).and().receive(this.results).from(this.stage.getOutputPort()).start();

		assertThat(this.results, contains(5));
	}

}
