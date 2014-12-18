package teetime.stage.basic.merger;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

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

		this.fstProducer.executeWithPorts();
		this.sndProducer.executeWithPorts();

		assertThat(this.collector.getElements(), contains(1, 2, 3, 4, 5, 6));
	}

	@Test
	public void roundRobinWithSingleProducerShouldWork() {
		mergerUnderTest.setStrategy(new RoundRobinStrategy());

		this.fstProducer.executeWithPorts();

		assertThat(this.collector.getElements(), contains(1, 2, 3));
	}

}
