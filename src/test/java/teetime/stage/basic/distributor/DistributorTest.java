/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.stage.CollectorSink;

/**
 * @author Nils Christian Ehmke
 *
 * @since 1.0
 */
public class DistributorTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private Distributor<Integer> distributorUnderTest;
	private CollectorSink<Integer> fstCollector;
	private CollectorSink<Integer> sndCollector;

	@Before
	public void initializeDistributor() throws Exception {
		this.distributorUnderTest = new Distributor<Integer>();
		this.fstCollector = new CollectorSink<Integer>();
		this.sndCollector = new CollectorSink<Integer>();

		final IPipeFactory pipeFactory = new SingleElementPipeFactory();
		pipeFactory.create(this.distributorUnderTest.getNewOutputPort(), this.fstCollector.getInputPort());
		pipeFactory.create(this.distributorUnderTest.getNewOutputPort(), this.sndCollector.getInputPort());

		distributorUnderTest.onStarting();
	}

	@Test
	public void roundRobinShouldWork() {
		distributorUnderTest.setStrategy(new RoundRobinStrategy());

		this.distributorUnderTest.execute(1);
		this.distributorUnderTest.execute(2);
		this.distributorUnderTest.execute(3);
		this.distributorUnderTest.execute(4);
		this.distributorUnderTest.execute(5);

		assertThat(this.fstCollector.getElements(), contains(1, 3, 5));
		assertThat(this.sndCollector.getElements(), contains(2, 4));
	}

	@Test
	public void singleElementRoundRobinShouldWork() {
		distributorUnderTest.setStrategy(new RoundRobinStrategy());

		this.distributorUnderTest.execute(1);

		assertThat(this.fstCollector.getElements(), contains(1));
		assertThat(this.sndCollector.getElements(), is(empty()));
	}

	@Test
	public void copyByReferenceShouldWork() {
		distributorUnderTest.setStrategy(new CopyByReferenceStrategy());

		this.distributorUnderTest.execute(1);
		this.distributorUnderTest.execute(2);
		this.distributorUnderTest.execute(3);
		this.distributorUnderTest.execute(4);
		this.distributorUnderTest.execute(5);

		assertThat(this.fstCollector.getElements(), contains(1, 2, 3, 4, 5));
		assertThat(this.sndCollector.getElements(), contains(1, 2, 3, 4, 5));
	}

	@Test
	public void singleElementCopyByReferenceShouldWork() {
		distributorUnderTest.setStrategy(new CopyByReferenceStrategy());

		this.distributorUnderTest.execute(1);

		assertThat(this.fstCollector.getElements(), contains(1));
		assertThat(this.sndCollector.getElements(), contains(1));
	}

	@Test
	public void cloneShouldNotWork() {
		distributorUnderTest.setStrategy(new CloneStrategy());

		expectedException.expect(UnsupportedOperationException.class);
		this.distributorUnderTest.execute(1);
	}

}
