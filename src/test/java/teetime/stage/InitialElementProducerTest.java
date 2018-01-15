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

import static org.hamcrest.collection.IsEmptyCollection.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.*;
import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Nils Christian Ehmke
 */
public class InitialElementProducerTest {

	@Test
	public void producerShouldSendNothingByDefault() {
		InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>();
		List<Integer> results = new ArrayList<Integer>();

		test(producer).and().receive(results).from(producer.getOutputPort()).start();
		assertThat(results, is(empty()));
	}

	// @Test
	// public void testSetIterArray() {
	// InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>(new Integer[] { 1, 2, 3 });
	//
	// List<Integer> results = new ArrayList<Integer>();
	//
	// test(producer).and().receive(results).from(producer.getOutputPort()).start();
	// assertThat(results, contains(1, 2, 3));
	// }
	//
	// @Test
	// public void testSetIterVarargs() {
	// InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>(1, 2, 3);
	// List<Integer> results = new ArrayList<Integer>();
	//
	// test(producer).and().receive(results).from(producer.getOutputPort()).start();
	// assertThat(results, contains(1, 2, 3));
	// }

	@Test
	public void instantiateWithArray() {
		InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>(new Integer[] { 1, 2, 3 });
		List<Integer> results = new ArrayList<Integer>();

		/* StageTestResult testResult = */
		test(producer).and().receive(results).from(producer.getOutputPort()).start();
		assertThat(results, contains(1, 2, 3));
		// assertThat(testResult.getElementsFrom(producer.getOutputPort()), contains(1, 2, 3));
		// assertThat(producer.getOutputPort(), hasProduced(1, 2, 3));
		// assertThat(testResult, contains(1, 2, 3).for(producer.getOutputPort()));
	}

	@Test
	public void instantiateWithVarargs() {
		InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>(1, 2, 3);
		List<Integer> results = new ArrayList<Integer>();

		test(producer).and().receive(results).from(producer.getOutputPort()).start();
		assertThat(results, contains(1, 2, 3));
	}

	@Test
	public void instantiateWithCollection() {
		List<Integer> testIntegers = new ArrayList<Integer>();
		testIntegers.add(1);
		testIntegers.add(2);
		testIntegers.add(3);
		InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>(testIntegers);
		List<Integer> results = new ArrayList<Integer>();

		test(producer).and().receive(results).from(producer.getOutputPort()).start();
		assertThat(results, contains(1, 2, 3));
	}

	@Test
	public void instantiateWithIterableAsSingleObject() {
		// Path is an Iterable<Path>, but should be handled as single object
		Path path = Paths.get(".", "conf", "quality-config");
		InitialElementProducer<Path> producer = new InitialElementProducer<Path>(path);
		List<Path> results = new ArrayList<Path>();

		test(producer).and().receive(results).from(producer.getOutputPort()).start();
		assertThat(results, contains(path));
	}
}
