package teetime.examples.tokenizer;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.Analysis;

public class TokenizerTest {

	@Test
	public void executeTest() {
		// Encrypted lorem ipsum
		String inputFile = "src/test/resources/data/cipherInput.txt";
		String password = "Password";

		TokenizerConfiguration configuration = new TokenizerConfiguration(inputFile, password);
		Analysis analysis = new Analysis(configuration);
		analysis.init();
		analysis.start();

		Assert.assertEquals(970, configuration.getTokenCount());
	}

}
