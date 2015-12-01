/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.stage.basic.distributor;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import teetime.stage.basic.distributor.strategy.CloneStrategy;
import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.strategy.BlockingRoundRobinStrategy;
import teetime.stage.basic.distributor.strategy.NonBlockingRoundRobinStrategy;

/**
 * @author Nils Christian Ehmke
 *
 * @since 1.0
 */
public class DistributorTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private Distributor<Integer> distributor;
	private List<Integer> firstIntegers;
	private List<Integer> secondIntegers;

	@Before
	public void initializeDistributor() throws Exception {
		this.distributor = new Distributor<Integer>();
		this.firstIntegers = new ArrayList<Integer>();
		this.secondIntegers = new ArrayList<Integer>();
	}

	@Test
	public void roundRobinShouldWork() {
		distributor.setStrategy(new BlockingRoundRobinStrategy());

		test(distributor).and().send(1, 2, 3, 4, 5).to(distributor.getInputPort()).and().receive(firstIntegers).from(distributor.getNewOutputPort()).and()
				.receive(secondIntegers).from(distributor.getNewOutputPort()).start();

		assertThat(this.firstIntegers, contains(1, 3, 5));
		assertThat(this.secondIntegers, contains(2, 4));
	}

	@Test
	public void singleElementRoundRobinShouldWork() {
		distributor.setStrategy(new BlockingRoundRobinStrategy());

		test(distributor).and().send(1).to(distributor.getInputPort()).and().receive(firstIntegers).from(distributor.getNewOutputPort()).and()
				.receive(secondIntegers)
				.from(distributor.getNewOutputPort()).start();

		assertThat(this.firstIntegers, contains(1));
		assertThat(this.secondIntegers, is(empty()));
	}

	@Test
	public void roundRobin2ShouldWork() {
		distributor.setStrategy(new NonBlockingRoundRobinStrategy());

		test(distributor).and().send(1, 2, 3, 4, 5).to(distributor.getInputPort()).and().receive(firstIntegers).from(distributor.getNewOutputPort()).and()
				.receive(secondIntegers).from(distributor.getNewOutputPort()).start();

		assertThat(this.firstIntegers, contains(1, 3, 5));
		assertThat(this.secondIntegers, contains(2, 4));
	}

	@Test
	public void singleElementRoundRobin2ShouldWork() {
		distributor.setStrategy(new NonBlockingRoundRobinStrategy());

		test(distributor).and().send(1).to(distributor.getInputPort()).and().receive(firstIntegers).from(distributor.getNewOutputPort()).and()
				.receive(secondIntegers)
				.from(distributor.getNewOutputPort()).start();

		assertThat(this.firstIntegers, contains(1));
		assertThat(this.secondIntegers, is(empty()));
	}

	@Test
	public void copyByReferenceShouldWork() {
		distributor.setStrategy(new CopyByReferenceStrategy());

		test(distributor).and().send(1, 2, 3, 4, 5).to(distributor.getInputPort()).and().receive(firstIntegers).from(distributor.getNewOutputPort()).and()
				.receive(secondIntegers).from(distributor.getNewOutputPort()).start();

		assertThat(this.firstIntegers, contains(1, 2, 3, 4, 5));
		assertThat(this.secondIntegers, contains(1, 2, 3, 4, 5));
	}

	@Test
	public void singleElementCopyByReferenceShouldWork() {
		distributor.setStrategy(new CopyByReferenceStrategy());

		test(distributor).and().send(1).to(distributor.getInputPort()).and().receive(firstIntegers).from(distributor.getNewOutputPort()).and()
				.receive(secondIntegers)
				.from(distributor.getNewOutputPort()).start();

		assertThat(this.firstIntegers, contains(1));
		assertThat(this.secondIntegers, contains(1));
	}

	@Test
	public void cloneForIntegerShouldNotWork() throws Exception {
		this.distributor.setStrategy(new CloneStrategy());
		this.distributor.getNewOutputPort();
		this.distributor.onStarting();

		expectedException.expect(IllegalStateException.class);
		this.distributor.execute(1);
	}

	@Test
	public void cloneForSimpleBeanShouldWork() throws Exception {
		final Distributor<SimpleBean> distributor = new Distributor<SimpleBean>(new CloneStrategy());
		final List<SimpleBean> results = new ArrayList<SimpleBean>();
		final SimpleBean originalBean = new SimpleBean(42);

		test(distributor).and()
				.send(originalBean).to(distributor.getInputPort()).and()
				.receive(results).from(distributor.getNewOutputPort()).start();

		final SimpleBean clonedBean = results.get(0);
		assertThat(originalBean, is(not(clonedBean)));
		assertThat(originalBean.getValue(), is(clonedBean.getValue()));
	}

	// set to "public" to instantiate it via reflection
	public static class SimpleBean {

		private int value;

		public SimpleBean() {
			// necessary to instantiate it via reflection
		}

		public SimpleBean(final int value) {
			this.setValue(value);
		}

		public int getValue() {
			return value;
		}

		public void setValue(final int value) {
			this.value = value;
		}

	}

}
