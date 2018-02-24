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
				// .send(4,5,6).to(port)
				.receive(outputElements).from(producer.getOutputPort())
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
