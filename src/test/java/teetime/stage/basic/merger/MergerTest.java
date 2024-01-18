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
package teetime.stage.basic.merger;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.stage.basic.merger.strategy.NonBlockingFiniteRoundRobinStrategy;

/**
 * @author Nils Christian Ehmke
 *
 * @since 1.0
 */
public class MergerTest {

	// @Before
	// public void before() {
	// System.out.println("-------- Starting new test...");
	// }

	@Test
	public void roundRobinShouldWork() {
		Merger<Integer> mergerUnderTest = new Merger<Integer>(new NonBlockingFiniteRoundRobinStrategy());

		List<Integer> mergedElements = new ArrayList<Integer>();

		test(mergerUnderTest).and().send(1, 2, 3).to(mergerUnderTest.getNewInputPort()).and().send(4, 5, 6)
				.to(mergerUnderTest.getNewInputPort()).and().receive(mergedElements)
				.from(mergerUnderTest.getOutputPort()).start();

		assertThat(mergedElements, containsInAnyOrder(1, 2, 3, 4, 5, 6));
	}

	@Test
	public void roundRobinWithSingleProducerShouldWork() {
		Merger<Integer> mergerUnderTest = new Merger<Integer>(new NonBlockingFiniteRoundRobinStrategy());

		List<Integer> mergedElements = new ArrayList<Integer>();

		test(mergerUnderTest).and().send(1, 2, 3).to(mergerUnderTest.getNewInputPort()).and().receive(mergedElements)
				.from(mergerUnderTest.getOutputPort()).start();

		assertThat(mergedElements, contains(1, 2, 3));
	}

}
