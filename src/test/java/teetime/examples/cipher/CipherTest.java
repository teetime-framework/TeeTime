package teetime.examples.cipher;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;

import com.google.common.io.Files;

public class CipherTest {

	@Test
	public void executeTest() throws IOException {
		final String inputFile = "src/test/resources/data/input.txt";
		final String outputFile = "src/test/resources/data/output.txt";
		final String password = "Password";

		AnalysisConfiguration configuration = new CipherConfiguration(inputFile, outputFile, password);
		Analysis analysis = new Analysis(configuration);
		analysis.init();
		analysis.start();

		Assert.assertTrue(Files.equal(new File(inputFile), new File(outputFile)));
	}

}
