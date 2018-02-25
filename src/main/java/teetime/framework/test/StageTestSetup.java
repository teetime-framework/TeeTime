package teetime.framework.test;

import java.util.Collection;
import java.util.List;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.framework.ExecutionException;

public class StageTestSetup extends MinimalStageTestSetup {

	private final StageTester stageTester;

	/* default */ StageTestSetup(final StageTester stageTester) {
		this.stageTester = stageTester;
	}

	public StageTestSetup and() {
		return this;
	}

	/**
	 * This method will start the test and block until it is finished.
	 *
	 * @return
	 *
	 * @throws ExecutionException
	 *             if at least one exception in one thread has occurred within the analysis.
	 *             The exception contains the pairs of thread and throwable.
	 *
	 */
	public StageTestResult start() {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		final Configuration configuration = new TestConfiguration(stageTester.getInputHolders(), stageTester.getStage(), stageTester.getOutputHolders());
		final Execution<Configuration> analysis = new Execution<Configuration>(configuration);
		analysis.executeBlocking();

		StageTestResult result = new StageTestResult();
		for (OutputHolder<?> outputHolder : stageTester.getOutputHolders()) {
			result.add(outputHolder.getPort(), outputHolder.getOutputElements());
		}
		return result;
	}

	@Override
	public <I> InputHolder<I> send(final Collection<I> elements) {
		return stageTester.send(elements);
	}

	@Override
	public <O> OutputHolder<O> receive(final List<O> actualElements) {
		return stageTester.receive(actualElements);
	}
}
