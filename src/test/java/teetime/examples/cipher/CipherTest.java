package teetime.examples.cipher;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;

import com.google.common.io.Files;

/**
 * Executes stages which modify the given file and compares the results
 * Procedure: read > compress > encrypt > decrypt > decompress > write
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class CipherTest {

	public CipherTest() {}

	@Test
	public void executeTest() throws IOException {
		final String inputFile = "src/test/resources/data/input.txt";
		final String outputFile = "src/test/resources/data/output.txt";
		final String password = "Password";

		final AnalysisConfiguration configuration = new CipherConfiguration(inputFile, outputFile, password);
		final Analysis analysis = new Analysis(configuration);
		analysis.init();
		analysis.start();

		Assert.assertTrue(Files.equal(new File(inputFile), new File(outputFile)));
	}

}
