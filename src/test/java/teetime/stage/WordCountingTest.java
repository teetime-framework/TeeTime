package teetime.stage;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import teetime.framework.Analysis;
import teetime.stage.util.CountingMap;

public class WordCountingTest {

	@Test
	public void test1() {
		WordCountingConfiguration wcc = new WordCountingConfiguration(new File("src/test/resources/data/output.txt"));
		Analysis analysis = new Analysis(wcc);
		analysis.start();
		CountingMap<String> map = wcc.getResult();
		for (Map.Entry<String, Integer> entry : map.entrySet())
		{
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
}
