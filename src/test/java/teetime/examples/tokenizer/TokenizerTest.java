/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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
		analysis.start();

		final String string = Files.toString(new File("src/test/resources/data/input.txt"), Charset.forName("UTF-8"));

		Assert.assertEquals(string.split(" ").length, configuration.getTokenCount());
	}

}
