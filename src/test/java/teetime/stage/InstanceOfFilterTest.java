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
import static org.junit.Assert.assertThat;
import static teetime.framework.test.StageTester.produces;
import static teetime.framework.test.StageTester.producesNothing;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.Execution;

/**
 * @author Nils Christian Ehmke
 */
public class InstanceOfFilterTest { // NOPMD

	private InstanceOfFilter<Object, Clazz> filter;

	@Before
	public void initializeFilter() {
		filter = new InstanceOfFilter<Object, Clazz>(Clazz.class);
	}

	@Test
	public void filterShouldForwardCorrectTypes() {
		final Clazz clazz = new Clazz();

		test(filter).and().send(clazz).to(filter.getInputPort()).start();

		assertThat(filter.getMatchedOutputPort(), produces(clazz));
	}

	@Test
	public void outputMatchedAndMismatchedElements() {
		final Clazz clazz = new Clazz();
		final Integer number = 42; // NOPMD

		test(filter).and().send(clazz, number, clazz).to(filter.getInputPort()).start();

		assertThat(filter.getMatchedOutputPort(), produces(clazz, clazz));
		assertThat(filter.getMismatchedOutputPort(), produces(number));
	}

	@Test
	public void filterShouldForwardSubTypes() {
		final SubClazz clazz = new SubClazz();

		test(filter).and().send(clazz).to(filter.getInputPort()).and().start();

		assertThat(filter.getMatchedOutputPort(), produces(clazz));
	}

	@Test
	public void filterShouldDropInvalidTypes() {
		final Object object = new Object();

		test(filter).and().send(object).to(filter.getInputPort()).start();

		assertThat(filter.getMatchedOutputPort(), producesNothing());
	}

	@Test
	public void filterShouldWorkWithMultipleInput() {
		final List<Clazz> results = new ArrayList<InstanceOfFilterTest.Clazz>();
		final List<Object> inputObjects = new ArrayList<Object>();

		inputObjects.add(new Object());
		inputObjects.add(new Clazz());
		inputObjects.add(new Object());
		inputObjects.add(new SubClazz());
		inputObjects.add(new Object());

		test(filter).and().send(inputObjects).to(filter.getInputPort()).and().receive(results)
				.from(filter.getMatchedOutputPort()).start();

		assertThat(results, hasSize(2));
	}

	private static class Clazz {
	}

	private static class SubClazz extends Clazz {
	}

	@Test
	public void filterShouldSendToBothOutputPorts() {
		InstanceOfFilterTestConfig config = new InstanceOfFilterTestConfig();
		Execution<InstanceOfFilterTestConfig> execution = new Execution<InstanceOfFilterTestConfig>(config);
		execution.executeBlocking();
	}

	private static class InstanceOfFilterTestConfig extends Configuration {

		public InstanceOfFilterTestConfig() {
			InitialElementProducer<Object> elementProducer = new InitialElementProducer<Object>();
			InstanceOfFilter<Object, Clazz> instanceOfFilter = new InstanceOfFilter<Object, Clazz>(Clazz.class);
			CollectorSink<Clazz> clazzCollector = new CollectorSink<Clazz>();
			CollectorSink<Object> mismatchedCollector = new CollectorSink<Object>();

			connectPorts(elementProducer.getOutputPort(), instanceOfFilter.getInputPort());
			connectPorts(instanceOfFilter.getMatchedOutputPort(), clazzCollector.getInputPort());
			connectPorts(instanceOfFilter.getMismatchedOutputPort(), mismatchedCollector.getInputPort());

		}
	}

}
