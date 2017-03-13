/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.examples.tokenizer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Files;

import teetime.framework.*;
import teetime.stage.*;
import teetime.stage.CipherStage.CipherMode;
import teetime.stage.ZipByteArray.ZipMode;
import teetime.stage.io.File2ByteArray;
import teetime.stage.string.Tokenizer;

/**
 * Reads in a compressed and encrypted file and counts the containing words
 *
 * @author Nelson Tavares de Sousa, Sören Henning
 *
 */
public class TokenizerTest {

	private static final String INPUT_FILE = "src/test/resources/data/cipherInput.txt"; // Encrypted lorem ipsum
	private static final String PASSWORD = "Password";
	private static final File INPUT = new File("src/test/resources/data/input.txt");

	public TokenizerTest() {}

	@Test
	public void executeTestWithDefaultConfiguration() throws IOException {
		final TokenizerConfiguration configuration = new TokenizerConfiguration(INPUT_FILE, PASSWORD);
		final Execution<TokenizerConfiguration> execution = new Execution<>(configuration);
		execution.executeBlocking();

		String string = Files.toString(INPUT, Charset.forName("UTF-8"));
		Assert.assertEquals(string.split(" ").length, configuration.getTokenCount());
	}

	@Test
	public void executeTestWithBuilderBasedConfiguration() throws IOException {
		final TokenizerConfigurationFromBuilder configuration = new TokenizerConfigurationFromBuilder(INPUT_FILE, PASSWORD);
		final Execution<TokenizerConfigurationFromBuilder> execution = new Execution<>(configuration);
		execution.executeBlocking();

		String string = Files.toString(INPUT, Charset.forName("UTF-8"));
		Assert.assertEquals(string.split(" ").length, configuration.getTokenCount());
	}

	@Test
	public void executeTestWithConfigurationCreatedByBuilder() throws IOException {
		final Counter<String> counterStage = new Counter<String>();

		final Configuration configuration = ConfigurationBuilder
				.from(new InitialElementProducer<File>(new File(INPUT_FILE)))
				.to(new File2ByteArray())
				.to(new ZipByteArray(ZipMode.DECOMP))
				.to(new CipherStage(PASSWORD, CipherMode.DECRYPT))
				.to(new ByteArray2String())
				.to(new Tokenizer(" "))
				.end(counterStage);

		final Execution<Configuration> execution = new Execution<>(configuration);
		execution.executeBlocking();

		String string = Files.toString(INPUT, Charset.forName("UTF-8"));
		Assert.assertEquals(string.split(" ").length, counterStage.getNumElementsPassed());
	}

}
