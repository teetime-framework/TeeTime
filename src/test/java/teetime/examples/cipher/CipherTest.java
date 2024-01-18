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
package teetime.examples.cipher;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Files;

import teetime.framework.Configuration;
import teetime.framework.ConfigurationBuilder;
import teetime.framework.Execution;
import teetime.stage.CipherStage;
import teetime.stage.CipherStage.CipherMode;
import teetime.stage.InitialElementProducer;
import teetime.stage.ZipByteArray;
import teetime.stage.ZipByteArray.ZipMode;
import teetime.stage.io.ByteArrayFileWriter;
import teetime.stage.io.File2ByteArray;

/**
 * Executes stages which modify the given file and compares the results
 * Procedure: read > compress > encrypt > decrypt > decompress > write
 *
 * @author Nelson Tavares de Sousa, Sören Henning
 *
 */
public class CipherTest {

	private static final String INPUT_FILE = "src/test/resources/data/input.txt";
	private static final String OUTPUT_FILE = "src/test/resources/data/output.txt";
	private static final String PASSWORD = "Password";

	public CipherTest() {
		// empty constructor
	}

	@Test
	public void executeTestWithDefaultConfiguration() throws IOException {
		final CipherConfiguration configuration = new CipherConfiguration(INPUT_FILE, OUTPUT_FILE, PASSWORD);
		final Execution<CipherConfiguration> execution = new Execution<CipherConfiguration>(configuration);
		execution.executeBlocking();

		Assert.assertTrue(Files.equal(new File(INPUT_FILE), new File(OUTPUT_FILE)));
	}

	@Test
	public void executeTestWithBuilderBasedConfiguration() throws IOException {
		final CipherConfigurationFromBuilder configuration = new CipherConfigurationFromBuilder(INPUT_FILE, OUTPUT_FILE,
				PASSWORD);
		final Execution<CipherConfigurationFromBuilder> execution = new Execution<CipherConfigurationFromBuilder>(
				configuration);
		execution.executeBlocking();

		Assert.assertTrue(Files.equal(new File(INPUT_FILE), new File(OUTPUT_FILE)));
	}

	@Test
	public void executeTestWithConfigurationCreatedByBuilder() throws IOException {
		final Configuration configuration = ConfigurationBuilder
				.from(new InitialElementProducer<File>(new File(INPUT_FILE))).to(new File2ByteArray())
				.to(new CipherStage(PASSWORD, CipherMode.ENCRYPT)).to(new ZipByteArray(ZipMode.COMP))
				.to(new ZipByteArray(ZipMode.DECOMP)).to(new CipherStage(PASSWORD, CipherMode.DECRYPT))
				.end(new ByteArrayFileWriter(new File(OUTPUT_FILE)));
		final Execution<Configuration> execution = new Execution<Configuration>(configuration);
		execution.executeBlocking();

		Assert.assertTrue(Files.equal(new File(INPUT_FILE), new File(OUTPUT_FILE)));
	}

	public static void main(final String[] args) {
		final CipherConfiguration configuration = new CipherConfiguration(INPUT_FILE, OUTPUT_FILE, PASSWORD);
		final Execution<CipherConfiguration> execution = new Execution<CipherConfiguration>(configuration);
		execution.executeBlocking();
	}

}
