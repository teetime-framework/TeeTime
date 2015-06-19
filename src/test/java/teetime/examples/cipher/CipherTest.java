/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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
package teetime.examples.cipher;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.ConfigurationContext;

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

		final ConfigurationContext configuration = new CipherConfiguration(inputFile, outputFile, password);
		final Analysis analysis = new Analysis(configuration);
		analysis.executeBlocking();

		Assert.assertTrue(Files.equal(new File(inputFile), new File(outputFile)));
	}

}
