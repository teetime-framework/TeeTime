package teetime.examples.tokenizer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.Analysis;

import com.google.common.io.Files;

/**
 * Reads in a compressed and encrypted file and counts the containing words
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class TokenizerTest {

	public TokenizerTest() {}

	@Test
	public void executeTest() throws IOException {
		// Encrypted lorem ipsum
		final String inputFile = "src/test/resources/data/cipherInput.txt";
		final String password = "Password";

		final TokenizerConfiguration configuration = new TokenizerConfiguration(inputFile, password);
		final Analysis analysis = new Analysis(configuration);
		analysis.init();
		analysis.start();

		final String string = Files.toString(new File("src/test/resources/data/input.txt"), Charset.forName("UTF-8"));

		Assert.assertEquals(string.split(" ").length, configuration.getTokenCount());
	}

}
