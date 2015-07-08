package teetime.stage.basic.distributor.dynamic;

import teetime.framework.DynamicOutputPort;

public interface PortActionListener<T> {

	void onOutputPortCreated(DynamicDistributor<T> distributor, DynamicOutputPort<T> port);
}
