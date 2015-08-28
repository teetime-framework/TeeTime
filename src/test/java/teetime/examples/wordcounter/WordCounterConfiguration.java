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
package teetime.examples.wordcounter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import teetime.framework.AbstractPort;
import teetime.framework.Configuration;
import teetime.framework.MonitoringThread;
import teetime.stage.CountingMapMerger;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.RoundRobinStrategy2;
import teetime.stage.basic.merger.Merger;
import teetime.stage.io.File2SeqOfWords;
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
public class WordCounterConfiguration extends Configuration {

	// Last stage is saved as field, to retrieve the result after execution.
	private final CountingMapMerger<String> result = new CountingMapMerger<String>();

	private final List<AbstractPort<?>> distributorPorts = new ArrayList<AbstractPort<?>>();
	private final List<AbstractPort<?>> mergerPorts = new ArrayList<AbstractPort<?>>();

	private final MonitoringThread monitoringThread;

	private final Distributor<String> distributor;

	public WordCounterConfiguration(final int threads, final File... input) {
		// First part of the config
		final InitialElementProducer<File> init = new InitialElementProducer<File>(input);
		// final File2Lines f2b = new File2Lines();
		final File2SeqOfWords f2b = new File2SeqOfWords("UTF-8", 512);
		distributor = new Distributor<String>(new RoundRobinStrategy2());

		// last part
		final Merger<CountingMap<String>> merger = new Merger<CountingMap<String>>();
		// CountingMapMerger (already as field)

		// Connecting the stages of the first part of the config
		connectPorts(init.getOutputPort(), f2b.getInputPort());
		connectPorts(f2b.getOutputPort(), distributor.getInputPort());

		monitoringThread = new MonitoringThread();

		// Middle part... multiple instances of WordCounter are created and connected to the merger and distrubuter stages
		for (int i = 0; i < threads; i++) {
			// final InputPortSizePrinter<String> inputPortSizePrinter = new InputPortSizePrinter<String>();
			final WordCounter wc = new WordCounter();
			// intraFact.create(inputPortSizePrinter.getOutputPort(), wc.getInputPort());
			final WordCounter threadableStage = wc;

			connectPorts(distributor.getNewOutputPort(), threadableStage.getInputPort(), 1000);
			connectPorts(wc.getOutputPort(), merger.getNewInputPort());
			// Add WordCounter as a threadable stage, so it runs in its own thread
			declareActive(threadableStage.getInputPort().getOwningStage());

			distributorPorts.add(threadableStage.getInputPort());
			mergerPorts.add(wc.getOutputPort());

			monitoringThread.addPort(threadableStage.getInputPort());
		}

		// Connect the stages of the last part
		connectPorts(merger.getOutputPort(), result.getInputPort());

		// Add the first and last part to the threadable stages
		declareActive(init);
		declareActive(merger);
	}

	public MonitoringThread getMonitoringThread() {
		return monitoringThread;
	}

	// Further methods are allowed. For e.g. it is possible to read data from certain stages.
	public CountingMap<String> getResult() {
		return result.getResult();
	}

	public List<AbstractPort<?>> getDistributorPorts() {
		return distributorPorts;
	}

	public List<AbstractPort<?>> getMergerPorts() {
		return mergerPorts;
	}

	public Distributor<String> getDistributor() {
		return distributor;
	}

}
