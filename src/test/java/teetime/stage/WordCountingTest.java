package teetime.stage;

import java.io.File;

import org.junit.Test;

import teetime.framework.Analysis;

public class WordCountingTest {

	@Test
	public void test1() {
		WordCountingConfiguration wcc = new WordCountingConfiguration(new File("src/test/resources/data/output.txt"));
		Analysis analysis = new Analysis(wcc);
		analysis.start();
		System.out.println(wcc.getResult().size());
	}
}
