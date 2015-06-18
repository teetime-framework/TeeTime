package teetime.stage.basic.distributor;

import teetime.framework.InputPort;

public class DynamicPortActionContainer<T> {

	public enum DynamicPortAction {
		CREATE, REMOVE;
	}

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
