package teetime.stage.basic.merger.dynamic;

import java.util.concurrent.BlockingQueue;

import teetime.framework.DynamicInputPort;
import teetime.stage.basic.merger.Merger;
import teetime.util.framework.port.PortAction;
import teetime.util.framework.port.PortActionHelper;

public class DynamicMerger<T> extends Merger<T> {

	protected final BlockingQueue<PortAction<DynamicMerger<T>>> portActions;

	public DynamicMerger() {
		portActions = PortActionHelper.createPortActionQueue();
	}

	@Override
	public void executeStage() {
		checkForPendingPortActionRequest();

		super.executeStage();
	}

	protected void checkForPendingPortActionRequest() {
		PortActionHelper.checkForPendingPortActionRequest(this, portActions);
	}

	@Override
	public void removeDynamicPort(final DynamicInputPort<?> dynamicInputPort) { // make public
		super.removeDynamicPort(dynamicInputPort);
	}

	public boolean addPortActionRequest(final PortAction<DynamicMerger<T>> newPortActionRequest) {
		return portActions.offer(newPortActionRequest);
	}
}
