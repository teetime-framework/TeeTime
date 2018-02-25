package teetime.framework.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

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

		// let the producer be used once before testing it actually
		test(producer).receive(new ArrayList<Integer>()).from(producer.getOutputPort()).start();

		List<Integer> outputElements = new ArrayList<>();

		test(producer)
				.receive(outputElements).from(producer.getOutputPort())
				.start();

		assertThat(outputElements, contains(1, 2, 3));
	}

	@Test(expected = InvalidTestCaseSetupException.class)
	public void testProducerWithoutReceive() throws Exception {
		InitialElementProducer<Integer> producer = new InitialElementProducer<>(1, 2, 3);

		List<Integer> outputElements = new ArrayList<>();

		test(producer)
				.start();

		assertThat(outputElements, contains(1, 2, 3));
	}

	@Test
	public void testConsumer() throws Exception {

	}

	@Test
	public void testSink() throws Exception {

	}
}
