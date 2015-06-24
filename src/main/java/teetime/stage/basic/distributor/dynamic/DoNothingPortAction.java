package teetime.stage.basic.distributor.dynamic;

import teetime.util.framework.port.PortAction;

public class DoNothingPortAction<T> implements PortAction<DynamicDistributor<T>> {

	@Override
	public void execute(final DynamicDistributor<T> dynamicDistributor) {
		// do nothing for testing purpose
	}

}
