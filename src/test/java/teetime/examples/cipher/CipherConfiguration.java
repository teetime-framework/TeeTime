/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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

import teetime.framework.Configuration;
import teetime.stage.CipherStage;
import teetime.stage.CipherStage.CipherMode;
import teetime.stage.InitialElementProducer;
import teetime.stage.ZipByteArray;
import teetime.stage.ZipByteArray.ZipMode;
import teetime.stage.io.ByteArrayFileWriter;
import teetime.stage.io.File2ByteArray;

public class CipherConfiguration extends Configuration {

	public CipherConfiguration(final String inputFile, final String outputFile, final String password) {
		final File input = new File(inputFile);
		final File output = new File(outputFile);

		final InitialElementProducer<File> init = new InitialElementProducer<File>(input);
		final File2ByteArray f2b = new File2ByteArray();
		final CipherStage enc = new CipherStage(password, CipherMode.ENCRYPT);
		final ZipByteArray comp = new ZipByteArray(ZipMode.COMP);
		final ZipByteArray decomp = new ZipByteArray(ZipMode.DECOMP);
		final CipherStage decrypt = new CipherStage(password, CipherMode.DECRYPT);
		final ByteArrayFileWriter writer = new ByteArrayFileWriter(output);

		connectPorts(init.getOutputPort(), f2b.getInputPort());
		connectPorts(f2b.getOutputPort(), enc.getInputPort());
		connectPorts(enc.getOutputPort(), comp.getInputPort());
		connectPorts(comp.getOutputPort(), decomp.getInputPort());
		connectPorts(decomp.getOutputPort(), decrypt.getInputPort());
		connectPorts(decrypt.getOutputPort(), writer.getInputPort());
	}
}
