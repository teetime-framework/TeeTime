package teetime.framework;

import java.io.File;

import org.junit.Test;

import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CountingMapMerger;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.RoundRobinStrategy2;
import teetime.stage.basic.merger.Merger;
import teetime.stage.io.File2SeqOfWords;
import teetime.stage.string.WordCounter;
import teetime.stage.util.CountingMap;

public class TraversorTest {

	private final Traversor traversor = new Traversor(new IntraStageVisitor());

	@Test
	public void traverse() {
		TestConfiguration tc = new TestConfiguration();
		traversor.traverse(tc.init, tc.init.getOutputPort().getPipe());
		System.out.println(traversor.getVisitedStage());
	}

	private class TestConfiguration extends AnalysisConfiguration {

		public final CountingMapMerger<String> result = new CountingMapMerger<String>();
		public final InitialElementProducer<File> init;

		public TestConfiguration() {
			int threads = 4;
			init = new InitialElementProducer<File>(new File(""));
			// final File2Lines f2b = new File2Lines();
			final File2SeqOfWords f2b = new File2SeqOfWords("UTF-8", 512);
			Distributor<String> distributor = new Distributor<String>(new RoundRobinStrategy2());

			// last part
			final Merger<CountingMap<String>> merger = new Merger<CountingMap<String>>();
			// CountingMapMerger (already as field)

			// PipeFactory instaces for intra- and inter-thread communication
			final IPipeFactory interFact = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);
			final IPipeFactory intraFact = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);

			// Connecting the stages of the first part of the config
			intraFact.create(init.getOutputPort(), f2b.getInputPort());
			intraFact.create(f2b.getOutputPort(), distributor.getInputPort());

			// Middle part... multiple instances of WordCounter are created and connected to the merger and distrubuter stages
			for (int i = 0; i < threads; i++) {
				// final InputPortSizePrinter<String> inputPortSizePrinter = new InputPortSizePrinter<String>();
				final WordCounter wc = new WordCounter();
				// intraFact.create(inputPortSizePrinter.getOutputPort(), wc.getInputPort());
				final WordCounter threadableStage = wc;

				final IPipe distributorPipe = interFact.create(distributor.getNewOutputPort(), threadableStage.getInputPort(), 10000);
				final IPipe mergerPipe = interFact.create(wc.getOutputPort(), merger.getNewInputPort());
				// Add WordCounter as a threadable stage, so it runs in its own thread
				addThreadableStage(threadableStage);

			}

			// Connect the stages of the last part
			intraFact.create(merger.getOutputPort(), result.getInputPort());

			// Add the first and last part to the threadable stages
			addThreadableStage(init);
			addThreadableStage(merger);
		}

	}

}
