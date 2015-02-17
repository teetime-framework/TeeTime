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

public class WordCountingConfiguration extends AnalysisConfiguration {

	private final CountingMapMerger<String> result = new CountingMapMerger<String>();

	public WordCountingConfiguration(final File input, final int threads) {
		final InitialElementProducer<File> init = new InitialElementProducer<File>(input);
		final File2ByteArray f2b = new File2ByteArray();
		final ByteArray2String b2s = new ByteArray2String();
		final Distributor<String> dist = new Distributor<String>();

		final Merger<CountingMap<String>> merger = new Merger<CountingMap<String>>();
		// result
		IPipeFactory interFact = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);
		IPipeFactory intraFact = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);

		intraFact.create(init.getOutputPort(), f2b.getInputPort());
		intraFact.create(f2b.getOutputPort(), b2s.getInputPort());
		intraFact.create(b2s.getOutputPort(), dist.getInputPort());

		// scale
		WordCounter wc;
		for (int i = 0; i < threads; i++) {
			wc = new WordCounter();
			interFact.create(dist.getNewOutputPort(), wc.getInputPort());
			interFact.create(wc.getOutputPort(), merger.getNewInputPort());
			addThreadableStage(wc);
		}

		intraFact.create(merger.getOutputPort(), result.getInputPort());

		addThreadableStage(init);

		addThreadableStage(merger);
	}

	public CountingMap<String> getResult() {
		return result.getResult();
	}

}
