package teetime.framework;

public abstract class Configuration extends AbstractCompositeStage {

	public Configuration() {
		super(new ConfigurationContext());
	}

}
