package teetime.framework;

/**
 * Used to access the package-private methods of {@link AbstractStage}.
 *
 * @author Christian Wulf (chw)
 *
 */
public final class StageFacade {

	public static final StageFacade INSTANCE = new StageFacade();

	private StageFacade() {
		// singleton instance
	}

	public void abort(final AbstractStage stage) {
		stage.abort();
	}

	public TerminationStrategy getTerminationStrategy(final AbstractStage stage) {
		return stage.getTerminationStrategy();
	}

	public Thread getOwningThread(final AbstractStage stage) {
		return stage.getOwningThread();
	}

	public ConfigurationContext getOwningContext(final AbstractStage stage) {
		return stage.getOwningContext();
	}

	public void setOwningContext(final AbstractStage stage, final ConfigurationContext context) {
		stage.setOwningContext(context);
	}
}
