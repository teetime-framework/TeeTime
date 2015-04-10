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
package teetime.stage.basic.merger;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.util.Pair;

/**
 * @author Nils Christian Ehmke
 *
 * @since 1.0
 */
public class MergerTest {

	private Merger<Integer> mergerUnderTest;
	private CollectorSink<Integer> collector;
	private InitialElementProducer<Integer> fstProducer;
	private InitialElementProducer<Integer> sndProducer;

	@Before
	public void initializeMerger() throws Exception {
		this.mergerUnderTest = new Merger<Integer>();
		this.collector = new CollectorSink<Integer>();
		this.fstProducer = new InitialElementProducer<Integer>(1, 2, 3);
		this.sndProducer = new InitialElementProducer<Integer>(4, 5, 6);

		final IPipeFactory pipeFactory = new SingleElementPipeFactory();
		pipeFactory.create(this.fstProducer.getOutputPort(), this.mergerUnderTest.getNewInputPort());
		pipeFactory.create(this.sndProducer.getOutputPort(), this.mergerUnderTest.getNewInputPort());
		pipeFactory.create(this.mergerUnderTest.getOutputPort(), this.collector.getInputPort());

		mergerUnderTest.onStarting();
	}

	@Test
	public void roundRobinShouldWork() {
		mergerUnderTest.setStrategy(new RoundRobinStrategy());

		this.fstProducer.executeStage();
		this.sndProducer.executeStage();

		assertThat(this.collector.getElements(), contains(1, 2, 3, 4, 5, 6));
	}

	@Test
	public void roundRobinWithSingleProducerShouldWork() {
		mergerUnderTest.setStrategy(new RoundRobinStrategy());

		this.fstProducer.executeStage();

		assertThat(this.collector.getElements(), contains(1, 2, 3));
	}

	@Test
	public void roundRobinShouldWork2() {
		mergerUnderTest = new Merger<Integer>(new RoundRobinStrategy());

		List<Integer> outputList = new ArrayList<Integer>();
		Collection<Pair<Thread, Throwable>> exceptions = test(mergerUnderTest)
				.and().send(1, 2, 3).to(mergerUnderTest.getNewInputPort())
				.and().send(4, 5, 6).to(mergerUnderTest.getNewInputPort())
				.and().receive(outputList).from(mergerUnderTest.getOutputPort())
				.start();

		assertThat(exceptions, is(empty()));
		assertThat(outputList, is(not(empty())));
		assertThat(outputList, contains(1, 4, 2, 5, 3, 6));
	}
}
