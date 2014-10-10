package teetime.examples.tokenizer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;

public class TokenizerTest {

	static String inputFile = "testdata/dependencies.html";
	static String password = "Password";
	static long start;
	long stop;

	static AnalysisConfiguration configuration = new TokenizerConfiguration(inputFile, password);

	final static Analysis analysis = new Analysis(configuration);

	@BeforeClass
	public static void beforeClass() {
		analysis.init();
		start = System.currentTimeMillis();
	}

	@Test
	public void executeTest() {
		analysis.start();
	}

	@AfterClass
	public static void afterClass() {
		System.out.println("It took " + (System.currentTimeMillis() - start) + " Milliseconds");
		// boolean bool = false;
		// try {
		// bool = Files.equal(new File(inputFile), new File(outputFile));
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// Assert.assertTrue(bool);
	}
}
