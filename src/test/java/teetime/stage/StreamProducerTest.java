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

import static org.hamcrest.collection.IsIterableContainingInOrder.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamProducerTest {

	@Test
	public void testIntStream() throws Exception {
		IntStream inputElements = IntStream.iterate(1, i -> i + 1).limit(3);
		StreamProducer<Integer> producer = new StreamProducer<>(inputElements);

		final List<Integer> actualElements = new ArrayList<>();
		test(producer).and().receive(actualElements).from(producer.getOutputPort()).start();

		assertThat(actualElements, contains(1, 2, 3));
	}

	@Test
	public void testObjectStream() throws Exception {
		Stream<String> inputElements = Stream.iterate("a", s -> "b").limit(3);
		StreamProducer<String> producer = new StreamProducer<>(inputElements);

		final List<String> actualElements = new ArrayList<>();
		test(producer).and().receive(actualElements).from(producer.getOutputPort()).start();

		assertThat(actualElements, contains("a", "b", "b"));
	}

}
