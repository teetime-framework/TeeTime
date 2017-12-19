package teetime.stage;

import static org.hamcrest.collection.IsIterableContainingInOrder.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamProducerTest {

	@Test
	public void testIntStream() throws Exception {
		IntStream inputElements = IntStream.iterate(1, i -> i + 1).limit(3);
		StreamProducer<Integer> producer = new StreamProducer<>(inputElements);

		final List<Integer> actualElements = new ArrayList<>();
		test(producer).and().receive(actualElements).from(producer.getOutputPort()).start();

		assertThat(actualElements, contains(1, 2, 3));
	}

	@Test
	public void testObjectStream() throws Exception {
		Stream<String> inputElements = Stream.iterate("a", s -> "b").limit(3);
		StreamProducer<String> producer = new StreamProducer<>(inputElements);

		final List<String> actualElements = new ArrayList<>();
		test(producer).and().receive(actualElements).from(producer.getOutputPort()).start();

		assertThat(actualElements, contains("a", "b", "b"));
	}

}
