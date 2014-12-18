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
	public void initializeRecordSimplificator() throws Exception {
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
