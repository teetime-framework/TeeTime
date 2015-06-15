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
package teetime.examples.tokenizer;

import java.io.File;

import teetime.framework.AnalysisConfiguration;
import teetime.stage.ByteArray2String;
import teetime.stage.CipherStage;
import teetime.stage.CipherStage.CipherMode;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;
import teetime.stage.ZipByteArray;
import teetime.stage.ZipByteArray.ZipMode;
import teetime.stage.io.File2ByteArray;
import teetime.stage.string.Tokenizer;

public class TokenizerConfiguration extends AnalysisConfiguration {

	private final Counter<String> counter;

	public TokenizerConfiguration(final String inputFile, final String password) {
		final File input = new File(inputFile);

		final InitialElementProducer<File> init = new InitialElementProducer<File>(input);
		final File2ByteArray f2b = new File2ByteArray();
		final ZipByteArray decomp = new ZipByteArray(ZipMode.DECOMP);
		final CipherStage decrypt = new CipherStage(password, CipherMode.DECRYPT);
		final ByteArray2String b2s = new ByteArray2String();
		final Tokenizer tokenizer = new Tokenizer(" ");
		this.counter = new Counter<String>();

		connectPorts(init.getOutputPort(), f2b.getInputPort());
		connectPorts(f2b.getOutputPort(), decomp.getInputPort());
		connectPorts(decomp.getOutputPort(), decrypt.getInputPort());
		connectPorts(decrypt.getOutputPort(), b2s.getInputPort());
		connectPorts(b2s.getOutputPort(), tokenizer.getInputPort());
		connectPorts(tokenizer.getOutputPort(), this.counter.getInputPort());

		this.addThreadableStage(init);
	}

	public int getTokenCount() {
		return this.counter.getNumElementsPassed();
	}

}
