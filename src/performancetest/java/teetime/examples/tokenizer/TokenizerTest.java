package teetime.examples.tokenizer;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.Analysis;

public class TokenizerTest {

	// Encrypted lorem ipsum
	private static String inputFile = "src/test/resources/data/cipherInput.txt";
	private static String password = "Password";

	private static TokenizerConfiguration configuration = new TokenizerConfiguration(inputFile, password);

	private final static Analysis analysis = new Analysis(configuration);

	@Test
	public void executeTest() {
		analysis.init();
		analysis.start();
		Assert.assertEquals(970, configuration.getTokenCount());
	}

}
