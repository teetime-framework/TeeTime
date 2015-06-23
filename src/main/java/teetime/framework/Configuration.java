package teetime.framework;

/**
 *
 *
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 * @since 2.0
 *
 */
public abstract class Configuration extends AbstractCompositeStage {

	public Configuration() {
		super(new ConfigurationContext());
	}

}
