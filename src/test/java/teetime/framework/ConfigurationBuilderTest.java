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
package teetime.framework;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import teetime.framework.pipe.IPipe;
import teetime.stage.basic.AbstractTransformation;

public class ConfigurationBuilderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFromToConnection() {
		DummyProducerStage producerStage = new DummyProducerStage();
		DummyTransformerStage transformerStage = new DummyTransformerStage();
		DummyConsumerStage consumerStage = new DummyConsumerStage();

		ConfigurationBuilder.from(producerStage).to(transformerStage).end(consumerStage);

		IPipe<?> producerOutPipe = producerStage.getOutputPort().getPipe();
		IPipe<?> transformerInPipe = transformerStage.getInputPort().getPipe();

		Assert.assertSame(producerOutPipe, transformerInPipe);
	}

	@Test
	public void testToToConnection() {
		DummyProducerStage producerStage = new DummyProducerStage();
		DummyTransformerStage firstTransformerStage = new DummyTransformerStage();
		DummyTransformerStage secondTransformerStage = new DummyTransformerStage();
		DummyConsumerStage consumerStage = new DummyConsumerStage();

		ConfigurationBuilder.from(producerStage).to(firstTransformerStage).to(secondTransformerStage)
				.end(consumerStage);

		IPipe<?> firstTransformerOutPipe = firstTransformerStage.getOutputPort().getPipe();
		IPipe<?> secondTransformerInPipe = secondTransformerStage.getInputPort().getPipe();

		Assert.assertSame(firstTransformerOutPipe, secondTransformerInPipe);
	}

	@Test
	public void testToEndConnection() {
		DummyProducerStage producerStage = new DummyProducerStage();
		DummyTransformerStage transformerStage = new DummyTransformerStage();
		DummyConsumerStage consumerStage = new DummyConsumerStage();

		ConfigurationBuilder.from(producerStage).to(transformerStage).end(consumerStage);

		IPipe<?> transformerOutPipe = transformerStage.getOutputPort().getPipe();
		IPipe<?> consumerInPipe = consumerStage.getInputPort().getPipe();

		Assert.assertSame(transformerOutPipe, consumerInPipe);
	}

	@Test
	public void testFromEndConnection() {
		DummyProducerStage producerStage = new DummyProducerStage();
		DummyConsumerStage consumerStage = new DummyConsumerStage();

		ConfigurationBuilder.from(producerStage).end(consumerStage);

		IPipe<?> producerOutPipe = producerStage.getOutputPort().getPipe();
		IPipe<?> consumerInPipe = consumerStage.getInputPort().getPipe();

		Assert.assertSame(producerOutPipe, consumerInPipe);
	}

	@Test
	public void testParamtoOutConnection() {
		DummyProducerStage producerStage = new DummyProducerStage();
		DummyTransformerStage transformerStage = new DummyTransformerStage();
		DummyConsumerStage consumerStage = new DummyConsumerStage();

		ConfigurationBuilder.from(producerStage).to(transformerStage, s -> s.getInputPort(), s -> s.getOutputPort())
				.end(consumerStage);

		IPipe<?> producerOutPipe = producerStage.getOutputPort().getPipe();
		IPipe<?> transformerInPipe = transformerStage.getInputPort().getPipe();

		Assert.assertSame(producerOutPipe, transformerInPipe);
	}

	@Test
	public void testParamtoInConnection() {
		DummyProducerStage producerStage = new DummyProducerStage();
		DummyTransformerStage firstTransformerStage = new DummyTransformerStage();
		DummyTransformerStage secondTransformerStage = new DummyTransformerStage();
		DummyConsumerStage consumerStage = new DummyConsumerStage();

		ConfigurationBuilder.from(producerStage)
				.to(firstTransformerStage, s -> s.getInputPort(), s -> s.getOutputPort()).to(secondTransformerStage)
				.end(consumerStage);

		IPipe<?> firstTransformerOutPipe = firstTransformerStage.getOutputPort().getPipe();
		IPipe<?> secondTransformerInPipe = secondTransformerStage.getInputPort().getPipe();

		Assert.assertSame(firstTransformerOutPipe, secondTransformerInPipe);
	}

	private static class DummyProducerStage extends AbstractProducerStage<Object> {
		@Override
		protected void execute() throws Exception {
		}
	}

	private static class DummyTransformerStage extends AbstractTransformation<Object, Object> {
		@Override
		protected void execute(final Object object) {
		}
	};

	private static class DummyConsumerStage extends AbstractConsumerStage<Object> {
		@Override
		protected void execute(final Object object) {
		}
	};

}
