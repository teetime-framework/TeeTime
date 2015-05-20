package teetime.stage.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import teetime.framework.test.StageTester;

public class File2SeqOfWordsTest {

	@Test
	public void testExecute() throws Exception {
		File2SeqOfWords stage = new File2SeqOfWords(14);
		List<String> outputList = new ArrayList<String>();
		StageTester.test(stage).send(Arrays.asList(new File("./src/test/resources/data/input.txt"))).to(stage.getInputPort()).and().receive(outputList)
				.from(stage.getOutputPort()).start();
		assertEquals(outputList.get(0), "Lorem ipsum");
	}

}
