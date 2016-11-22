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
package teetime.stage;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import teetime.stage.util.CountingMap;

/**
 * @author Nils Christian Ehmke
 */
public class MappingCounterTest {

	private MappingCounter<Integer> counter;

	@Before
	public void initializeCounter() {
		counter = new MappingCounter<Integer>();
	}

	@Test
	public void countingMapForNoInputShouldBeEmpty() {
		List<CountingMap<Integer>> results = new ArrayList<CountingMap<Integer>>();

		test(counter).and().send(Collections.<Integer> emptyList()).to(counter.getInputPort()).and().receive(results).from(counter.getOutputPort()).start();

		assertThat(results, hasSize(1));

		final CountingMap<Integer> map = results.get(0);
		assertThat(map.size(), is(0));
	}

	@Test
	public void countingMapShouldBeCorrect() {
		List<CountingMap<Integer>> results = new ArrayList<CountingMap<Integer>>();

		test(counter).and().send(1, 4, 3, 4, 1).to(counter.getInputPort()).and().receive(results).from(counter.getOutputPort()).start();

		assertThat(results, hasSize(1));

		final CountingMap<Integer> map = results.get(0);
		assertThat(map.size(), is(3));
		assertThat(map.get(1), is(2));
		assertThat(map.get(3), is(1));
		assertThat(map.get(4), is(2));
	}
}
