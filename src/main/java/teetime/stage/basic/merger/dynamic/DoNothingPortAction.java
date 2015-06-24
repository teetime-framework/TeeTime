package teetime.stage.basic.merger.dynamic;

import teetime.util.framework.port.PortAction;

public class DoNothingPortAction<T> implements PortAction<DynamicMerger<T>> {

	@Override
	public void execute(final DynamicMerger<T> dynamicDistributor) {
		// do nothing for testing purpose
	}

}
