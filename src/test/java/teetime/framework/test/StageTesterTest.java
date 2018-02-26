package teetime.framework.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import org.junit.Test;

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
