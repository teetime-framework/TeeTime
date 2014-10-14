package teetime.examples.tokenizer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.Analysis;

import com.google.common.io.Files;

public class TokenizerTest {

	@Test
	public void executeTest() throws IOException {
		// Encrypted lorem ipsum
		String inputFile = "src/test/resources/data/cipherInput.txt";
		String password = "Password";

		TokenizerConfiguration configuration = new TokenizerConfiguration(inputFile, password);
		Analysis analysis = new Analysis(configuration);
		analysis.init();
		analysis.start();

		String string = Files.toString(new File("src/test/resources/data/input.txt"), Charset.forName("UTF-8"));

		Assert.assertEquals(string.split(" ").length, configuration.getTokenCount());
	}

}
