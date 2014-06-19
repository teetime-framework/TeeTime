package teetime.variant.explicitScheduling.framework.core;


public class OutputPortImpl<S extends IStage, T> extends AbstractPort<S, T> implements IOutputPort<S, T> {

	public OutputPortImpl(final S owningStage) {
		this.setOwningStage(owningStage);
	}

}
