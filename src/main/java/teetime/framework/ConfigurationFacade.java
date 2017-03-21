package teetime.framework;

import java.util.Collection;

import teetime.framework.exceptionHandling.AbstractExceptionListenerFactory;

/**
 * Used to access the package-private methods of {@link Configuration}.
 *
 * @author Christian Wulf (chw)
 *
 */
public final class ConfigurationFacade {

	public static final ConfigurationFacade INSTANCE = new ConfigurationFacade();

	private ConfigurationFacade() {
		// singleton class
	}

	public AbstractExceptionListenerFactory<?> getFactory(final Configuration configuration) {
		return configuration.getFactory();
	}

	public ConfigurationContext getContext(final Configuration configuration) {
		return configuration.getContext();
	}

	public Collection<AbstractStage> getStartStages(final Configuration configuration) {
		return configuration.getStartStages();
	}

}
