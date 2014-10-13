package teetime.examples.cipher;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;

import com.google.common.io.Files;

public class CipherTest {

	static String inputFile = "src/test/resources/data/input.txt";
	static String outputFile = "src/test/resources/data/output.txt";
	static String password = "Password";
	static long start;
	long stop;

	static AnalysisConfiguration configuration = new CipherConfiguration(inputFile, outputFile, password);

	final static Analysis analysis = new Analysis(configuration);

	@Test
	public void executeTest() throws IOException {
		analysis.init();
		start = System.currentTimeMillis();
		analysis.start();
		Assert.assertTrue(Files.equal(new File(inputFile), new File(outputFile)));
	}

}
