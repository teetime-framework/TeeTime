package teetime.stage.basic.merger.dynamic;

import teetime.util.framework.port.PortActionHelper;

public class ControlledDynamicMerger<T> extends DynamicMerger<T> {

	@Override
	protected void checkForPendingPortActionRequest() {
		try {
			PortActionHelper.checkBlockingForPendingPortActionRequest(this, portActions);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
