package teetime.stage.basic.distributor.dynamic;

import teetime.util.framework.port.PortAction;

public class MultiStageDistributor<T> extends DynamicDistributor<T> {

	@Override
	public void onTerminating() throws Exception {
		// foreach on portActions is not implemented, so we iterate by ourselves
		PortAction<DynamicDistributor<T>> portAction = portActions.poll();
		while (portAction != null) {
			portAction.execute(this);
			portAction = portActions.poll();
		}

		super.onTerminating();
	}

}
