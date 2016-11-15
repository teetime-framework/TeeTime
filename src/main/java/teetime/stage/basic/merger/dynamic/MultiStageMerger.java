package teetime.stage.basic.merger.dynamic;

import teetime.stage.basic.merger.strategy.IMergerStrategy;
import teetime.util.framework.port.PortAction;

public class MultiStageMerger<T> extends DynamicMerger<T> {

	public MultiStageMerger(final IMergerStrategy strategy) {
		super(strategy);
	}

	@Override
	public void onTerminating() throws Exception {
		// foreach on portActions is not implemented, so we iterate by ourselves
		PortAction<DynamicMerger<T>> portAction = portActions.poll();
		while (portAction != null) {
			portAction.execute(this);
			portAction = portActions.poll();
		}

		super.onTerminating();
	}

}
