package teetime.stage.basic.distributor;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.stage.basic.distributor.DynamicPortActionContainer.DynamicPortAction;

public class DynamicDistributor<T> extends Distributor<T> {

	private static final SpScPipeFactory spScPipeFactory = new SpScPipeFactory();

	@SuppressWarnings("rawtypes")
	private final InputPort<DynamicPortActionContainer> dynamicPortActionInputPort = createInputPort(DynamicPortActionContainer.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void execute(final T element) {
		DynamicPortActionContainer<T> dynamicPortAction = dynamicPortActionInputPort.receive();
		switch (dynamicPortAction.getDynamicPortAction()) {
		case CREATE:
			OutputPort<T> newOutputPort = createOutputPort();
			InputPort<T> newInputPort = dynamicPortAction.getInputPort();
			spScPipeFactory.create(newOutputPort, newInputPort);
			break;
		case REMOVE:
			// TODO implement "remove port at runtime"
			break;
		default:
			if (logger.isWarnEnabled()) {
				logger.warn("Unhandled switch case of " + DynamicPortAction.class.getName() + ": " + dynamicPortAction.getDynamicPortAction());
			}
			break;
		}

		super.execute(element);
	}
}
