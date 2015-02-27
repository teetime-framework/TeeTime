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
package teetime.stage;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CipherByteArray.CipherMode;

/**
 * @author Nils Christian Ehmke
 */
public class CipherByteArrayTest {

	@Test
	public void decryptShouldInvertEncryption() {
		final byte[] input = new byte[] { 1, 2, 3, 4, 5 };
		final List<byte[]> output = new ArrayList<byte[]>();

		final Configuration configuration = new Configuration(input, output);
		final Analysis analysis = new Analysis(configuration);
		analysis.start();

		assertThat(output, contains(input));
	}

	private class Configuration extends AnalysisConfiguration {

		public Configuration(final byte[] input, final List<byte[]> output) {
			final IPipeFactory pipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);

			final InitialElementProducer<byte[]> producer = new InitialElementProducer<byte[]>(input);
			final CipherByteArray encryptStage = new CipherByteArray("somePassword", CipherMode.ENCRYPT);
			final CipherByteArray decryptStage = new CipherByteArray("somePassword", CipherMode.DECRYPT);
			final CollectorSink<byte[]> sink = new CollectorSink<byte[]>(output);

			pipeFactory.create(producer.getOutputPort(), encryptStage.getInputPort());
			pipeFactory.create(encryptStage.getOutputPort(), decryptStage.getInputPort());
			pipeFactory.create(decryptStage.getOutputPort(), sink.getInputPort());

			super.addThreadableStage(producer);
		}

	}

}
