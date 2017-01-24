package teetime.framework;

import java.io.File;

import teetime.stage.CountingMapMerger;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.NonBlockingRoundRobinStrategy;
import teetime.stage.basic.merger.Merger;
import teetime.stage.io.File2SeqOfWords;
import teetime.stage.string.WordCounter;
import teetime.stage.util.CountingMap;

/**
 * This configuration is used by several tests.
 *
 * @author Christian Wulf
 */
// WordCounterConfiguration
class TestConfiguration extends Configuration {

	public final InitialElementProducer<File> init;
	public final File2SeqOfWords f2b;
	public final Distributor<String> distributor;

	public TestConfiguration() {
		int threads = 2;
		init = new InitialElementProducer<File>(new File(""));
		f2b = new File2SeqOfWords("UTF-8", 512);
		distributor = new Distributor<String>(new NonBlockingRoundRobinStrategy());
		final Merger<CountingMap<String>> merger = new Merger<CountingMap<String>>();
		CountingMapMerger<String> result = new CountingMapMerger<String>();

		// Connecting the stages of the first part of the config
		connectPorts(init.getOutputPort(), f2b.getInputPort());
		connectPorts(f2b.getOutputPort(), distributor.getInputPort());

		// Middle part... multiple instances of WordCounter are created and connected to the merger and distrubuter stages
		for (int i = 0; i < threads; i++) {
			// final InputPortSizePrinter<String> inputPortSizePrinter = new InputPortSizePrinter<String>();
			final WordCounter wc = new WordCounter();
			// intraFact.create(inputPortSizePrinter.getOutputPort(), wc.getInputPort());

			connectPorts(distributor.getNewOutputPort(), wc.getInputPort());
			connectPorts(wc.getOutputPort(), merger.getNewInputPort());
			// Add WordCounter as a threadable stage, so it runs in its own thread
			wc.getInputPort().getOwningStage().declareActive();
		}

		// Connect the stages of the last part
		connectPorts(merger.getOutputPort(), result.getInputPort());

		// Add the first and last part to the threadable stages
		merger.declareActive();
	}

}
