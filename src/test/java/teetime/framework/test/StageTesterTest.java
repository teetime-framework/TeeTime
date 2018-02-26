package teetime.framework.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;

public class StageTesterTest {

	@Test
	public void testProducer() throws Exception {
		InitialElementProducer<Integer> producer = new InitialElementProducer<>(1, 2, 3);

		List<Integer> outputElements = new ArrayList<>();

		test(producer)
				.receive(outputElements).from(producer.getOutputPort())
				.start();

		assertThat(outputElements, contains(1, 2, 3));
	}

	@Test(expected = InvalidTestCaseSetupException.class)
	public void testProducerAlreadyStarted() throws Exception {
		InitialElementProducer<Integer> producer = new InitialElementProducer<>(1, 2, 3);

		// let the producer be used once before actually testing it
		test(producer).receive(new ArrayList<Integer>()).from(producer.getOutputPort()).start();

		List<Integer> outputElements = new ArrayList<>();

		test(producer)
				.receive(outputElements).from(producer.getOutputPort())
				.start();

		assertThat(outputElements, contains(1, 2, 3));
	}

	@Test
	public void testConsumer() throws Exception {
		Counter<Integer> consumer = new Counter<>();

		List<Integer> outputElements = new ArrayList<>();

		test(consumer).and()
				.send(1, 2, 3).to(consumer.getInputPort()).and()
				.receive(outputElements).from(consumer.getOutputPort())
				.start();

		assertThat(outputElements, contains(1, 2, 3));
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
