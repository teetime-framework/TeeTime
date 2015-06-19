package teetime.stage.basic.distributor.dynamic;

public class ControlledDynamicDistributor<T> extends DynamicDistributor<T> {

	@Override
	protected PortAction<T> getPortAction() throws InterruptedException {
		return portActions.take();
	}

}
