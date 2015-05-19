/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import teetime.framework.OutputPort;
import teetime.framework.test.StageTester;

/**
 * @author Nils Christian Ehmke
 */
public class MultipleInstanceOfFilterTest {

	@Test
	@SuppressWarnings("unchecked")
	public void filteringForSingleTypeShouldWork() {
		final MultipleInstanceOfFilter<Object> filter = new MultipleInstanceOfFilter<Object>();
		final List<Object> inputObjects = new ArrayList<Object>(Arrays.asList("1", 1.5f, "2", 2.5f, "3", 3.5f));
		final List<String> receivedStrings = new ArrayList<String>();

		StageTester.test(filter).and().send(inputObjects).to(filter.getInputPort()).and().receive(receivedStrings).from(filter.getOutputPortForType(String.class)).start();

		assertThat(receivedStrings, is(not(empty())));
		assertThat(receivedStrings, contains("1", "2", "3"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void filteringForMultipleTypesShouldWork() {
		final MultipleInstanceOfFilter<Number> filter = new MultipleInstanceOfFilter<Number>();
		final List<Number> inputObjects = new ArrayList<Number>(Arrays.asList(1, 1.5f, 2, 2.5f, 3, 3.5f));
		final List<Integer> integers = new ArrayList<Integer>();
		final List<Float> floats = new ArrayList<Float>();

		StageTester.test(filter).and().send(inputObjects).to(filter.getInputPort()).and().receive(integers).from(filter.getOutputPortForType(Integer.class)).and()
				.receive(floats).from(filter.getOutputPortForType(Float.class)).start();

		assertThat(integers, contains(1, 2, 3));
		assertThat(floats, contains(1.5f, 2.5f, 3.5f));
	}

	@Test
	public void outputPortForSameTypeShouldBeCached() {
		final MultipleInstanceOfFilter<Number> filter = new MultipleInstanceOfFilter<Number>();

		final OutputPort<Float> firstPort = filter.getOutputPortForType(Float.class);
		final OutputPort<Float> secondPort = filter.getOutputPortForType(Float.class);

		assertThat(firstPort, is(secondPort));
	}

}
