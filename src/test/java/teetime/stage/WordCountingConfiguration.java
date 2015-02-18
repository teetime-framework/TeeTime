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

import java.io.File;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;
import teetime.stage.io.File2ByteArray;
import teetime.stage.string.WordCounter;
import teetime.stage.util.CountingMap;

/**
 * A simple configuration, which counts the words of a set of files.
 * The execution of this configuration is demonstrated in {@link WordCountingTest}.
 *
 * This configuration is divided into three parts. The first part reads files and distributes them to different {@link WordCounter} instances.
 * The second part are a certain number of WordCounter instances. On construction of this class the number of concurrent WordCounter instances is specified with the
 * threads parameter.
 * The third and last part collects the results from all WordCounter instances and merges them. This final result can be read afterwards.
 *
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class WordCountingConfiguration extends AnalysisConfiguration {

	// Last stage is saved as field, to retrieve the result after execution.
	private final CountingMapMerger<String> result = new CountingMapMerger<String>();

	public WordCountingConfiguration(final int threads, final File... input) {
		// First part of the config
		final InitialElementProducer<File> init = new InitialElementProducer<File>(input);
		final File2ByteArray f2b = new File2ByteArray();
		final ByteArray2String b2s = new ByteArray2String();
		final Distributor<String> dist = new Distributor<String>();

		// last part
		final Merger<CountingMap<String>> merger = new Merger<CountingMap<String>>();
		// CountingMapMerger (already as field)

		// PipeFactory instaces for intra- and inter-thread communication
		IPipeFactory interFact = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);
		IPipeFactory intraFact = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);

		// Connecting the stages of the first part of the config
		intraFact.create(init.getOutputPort(), f2b.getInputPort());
		intraFact.create(f2b.getOutputPort(), b2s.getInputPort());
		intraFact.create(b2s.getOutputPort(), dist.getInputPort());

		// Middle part... multiple instances of WordCounter are created and connected to the merger and distrubuter stages
		WordCounter wc;
		for (int i = 0; i < threads; i++) {
			wc = new WordCounter();
			interFact.create(dist.getNewOutputPort(), wc.getInputPort());
			interFact.create(wc.getOutputPort(), merger.getNewInputPort());
			// Add WordCounter as a threadable stage, so it runs in its own thread
			addThreadableStage(wc);
		}

		// Connect the stages of the last part
		intraFact.create(merger.getOutputPort(), result.getInputPort());

		// Add the first and last part to the threadable stages
		addThreadableStage(init);
		addThreadableStage(merger);
	}

	// Further methods are allowed. For e.g. it is possible to read data from certain stages.
	public CountingMap<String> getResult() {
		return result.getResult();
	}

}
