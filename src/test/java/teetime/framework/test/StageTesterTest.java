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
package teetime.framework.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import org.junit.Test;

import teetime.framework.test.InvalidTestCaseSetupException;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;

public class StageTesterTest {

	@Test
	public void testProducer() throws Exception {
		InitialElementProducer<Integer> producer = new InitialElementProducer<>(1, 2, 3);

		test(producer).start();

		assertThat(producer.getOutputPort(), produces(1, 2, 3));
	}

	@Test(expected = InvalidTestCaseSetupException.class)
	public void testProducerAlreadyStarted() throws Exception {
		InitialElementProducer<Integer> producer = new InitialElementProducer<>(1, 2, 3);

		// let the producer be used once before actually testing it
		test(producer).start();

		test(producer).start();

		assertThat(producer.getOutputPort(), produces(1, 2, 3));
	}

	@Test
	public void testConsumer() throws Exception {
		Counter<Integer> consumer = new Counter<>();

		test(consumer).and()
				.send(1, 2, 3).to(consumer.getInputPort()).and()
				.start();

		assertThat(consumer.getOutputPort(), produces(1, 2, 3));
		assertThat(consumer.getNumElementsPassed(), is(3));
	}

	@Test
	public void testSink() throws Exception {
		CollectorSink<Integer> consumer = new CollectorSink<>();

		test(consumer).and()
				.send(1, 2, 3).to(consumer.getInputPort()).and()
				.start();

		assertThat(consumer.getElements(), contains(1, 2, 3));
	}
}
