package teetime.stage.string;

import java.util.ArrayList;
import java.util.Collection;

import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.MappingCounter;
import teetime.stage.util.CountingMap;

/**
 * Intermediate stage, which receives texts and counts the occurring words.
 * The result is passed on upon termination.
 *
 * @since 1.1
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class WordCounter extends CompositeStage {

	private final Tokenizer tokenizer = new Tokenizer(" ");
	private final MappingCounter<String> mapCounter = new MappingCounter<String>();
	private final ArrayList<Stage> lastStages = new ArrayList<Stage>();

	public WordCounter() {
		lastStages.add(mapCounter);

		IPipeFactory pipeFact = PipeFactoryRegistry.INSTANCE.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);
		ToLowerCase toLowerCase = new ToLowerCase();
		pipeFact.create(tokenizer.getOutputPort(), toLowerCase.getInputPort());
		pipeFact.create(toLowerCase.getOutputPort(), mapCounter.getInputPort());
	}

	@Override
	protected Stage getFirstStage() {
		return tokenizer;
	}

	@Override
	protected Collection<? extends Stage> getLastStages() {
		return lastStages;
	}

	public InputPort<String> getInputPort() {
		return tokenizer.getInputPort();
	}

	public OutputPort<CountingMap<String>> getOutputPort() {
		return mapCounter.getOutputPort();
	}

}
