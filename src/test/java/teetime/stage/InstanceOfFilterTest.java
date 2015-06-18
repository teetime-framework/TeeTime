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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisContext;
import teetime.framework.AnalysisException;
import teetime.util.Pair;

/**
 * @author Nils Christian Ehmke
 */
public class InstanceOfFilterTest {

	private InstanceOfFilter<Object, Clazz> filter;

	@Before
	public void initializeFilter() {
		filter = new InstanceOfFilter<Object, Clazz>(Clazz.class);
	}

	@Test
	public void filterShouldForwardCorrectTypes() {
		final List<Clazz> results = new ArrayList<InstanceOfFilterTest.Clazz>();
		final Object clazz = new Clazz();

		test(filter)
				.and().send(clazz).to(filter.getInputPort())
				.and().receive(results).from(filter.getMatchedOutputPort())
				.start();

		assertThat(results, contains(clazz));
	}

	@Test
	public void filterShouldForwardSubTypes() {
		final List<Clazz> results = new ArrayList<InstanceOfFilterTest.Clazz>();
		final Object clazz = new SubClazz();

		test(filter)
				.and().send(clazz).to(filter.getInputPort())
				.and().receive(results).from(filter.getMatchedOutputPort())
				.start();

		assertThat(results, contains(clazz));
	}

	@Test
	public void filterShouldDropInvalidTypes() {
		final List<Clazz> results = new ArrayList<InstanceOfFilterTest.Clazz>();
		final Object object = new Object();

		test(filter)
				.and().send(object).to(filter.getInputPort())
				.and().receive(results).from(filter.getMatchedOutputPort())
				.start();

		assertThat(results, is(empty()));
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

		test(filter)
				.and().send(inputObjects).to(filter.getInputPort())
				.and().receive(results).from(filter.getMatchedOutputPort())
				.start();

		assertThat(results, hasSize(2));
	}

	private static class Clazz {
	}

	private static class SubClazz extends Clazz {
	}

	@Test
	public void filterShouldSendToBothOutputPorts() throws Exception {
		InstanceOfFilterTestConfig config = new InstanceOfFilterTestConfig();
		Analysis analysis = new Analysis(config);
		try {
			analysis.executeBlocking();
		} catch (AnalysisException e) {
			Collection<Pair<Thread, Throwable>> thrownExceptions = e.getThrownExceptions();
			// TODO: handle exception
		}
	}

	private static class InstanceOfFilterTestConfig extends AnalysisContext {

		public InstanceOfFilterTestConfig() {
			InitialElementProducer<Object> elementProducer = new InitialElementProducer<Object>();
			InstanceOfFilter<Object, Clazz> instanceOfFilter = new InstanceOfFilter<Object, Clazz>(Clazz.class);
			CollectorSink<Clazz> clazzCollector = new CollectorSink<Clazz>();
			CollectorSink<Object> mismatchedCollector = new CollectorSink<Object>();

			connectPorts(elementProducer.getOutputPort(), instanceOfFilter.getInputPort());
			connectPorts(instanceOfFilter.getMatchedOutputPort(), clazzCollector.getInputPort());
			connectPorts(instanceOfFilter.getMismatchedOutputPort(), mismatchedCollector.getInputPort());

			addThreadableStage(elementProducer);
		}
	}

}
