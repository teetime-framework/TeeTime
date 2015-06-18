package teetime.stage.basic.distributor;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.SpScPipeFactory;

public class DynamicDistributor<T> extends Distributor<T> {

	private static final SpScPipeFactory spScPipeFactory = new SpScPipeFactory();

	public enum DynamicPortAction {
		CREATE, REMOVE;
	}

	public static class DynamicPortActionContainer<T> {
		private final DynamicPortAction dynamicPortAction;
		private final InputPort<T> inputPort;

		public DynamicPortActionContainer(final DynamicPortAction dynamicPortAction, final InputPort<T> inputPort) {
			super();
			this.dynamicPortAction = dynamicPortAction;
			this.inputPort = inputPort;
		}

		public DynamicPortAction getDynamicPortAction() {
			return dynamicPortAction;
		}

		public InputPort<T> getInputPort() {
			return inputPort;
		}

	}

	@SuppressWarnings("rawtypes")
	private final InputPort<DynamicPortActionContainer> dynamicPortActionInputPort = createInputPort(DynamicPortActionContainer.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void execute(final T element) {
		DynamicPortActionContainer<T> dynamicPortAction = dynamicPortActionInputPort.receive();
		switch (dynamicPortAction.dynamicPortAction) {
		case CREATE:
			OutputPort<T> newOutputPort = createOutputPort();
			InputPort<T> newInputPort = dynamicPortAction.inputPort;
			spScPipeFactory.create(newOutputPort, newInputPort);
			break;
		case REMOVE:
			// TODO implement "remove port at runtime"
			break;
		default:
			if (logger.isWarnEnabled()) {
				logger.warn("Unhandled switch case of " + DynamicPortAction.class.getName() + ": " + dynamicPortAction.dynamicPortAction);
			}
			break;
		}

		super.execute(element);
	}
}
