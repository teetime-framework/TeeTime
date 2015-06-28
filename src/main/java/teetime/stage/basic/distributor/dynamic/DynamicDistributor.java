package teetime.stage.basic.distributor.dynamic;

import java.util.concurrent.BlockingQueue;

import teetime.framework.DynamicOutputPort;
import teetime.framework.OutputPort;
import teetime.framework.OutputPortRemovedListener;
import teetime.framework.Stage;
import teetime.framework.signal.TerminatingSignal;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.IDistributorStrategy;
import teetime.stage.basic.distributor.strategy.RoundRobinStrategy2;
import teetime.util.framework.port.PortAction;
import teetime.util.framework.port.PortActionHelper;

public class DynamicDistributor<T> extends Distributor<T> implements OutputPortRemovedListener {

	protected final BlockingQueue<PortAction<DynamicDistributor<T>>> portActions;

	/**
	 * Uses {@link RoundRobinStrategy2} as default distributor strategy.
	 */
	public DynamicDistributor() {
		this(new RoundRobinStrategy2());
	}

	public DynamicDistributor(final IDistributorStrategy strategy) {
		super(strategy);
		this.portActions = PortActionHelper.createPortActionQueue();
		addOutputPortRemovedListener(this);
	}

	@Override
	protected void execute(final T element) {
		checkForPendingPortActionRequest();

		super.execute(element);
	}

	protected void checkForPendingPortActionRequest() {
		PortActionHelper.checkForPendingPortActionRequest(this, portActions);
	}

	@Override
	public void removeDynamicPort(final DynamicOutputPort<?> dynamicOutputPort) { // make public
		super.removeDynamicPort(dynamicOutputPort);
	}

	public boolean addPortActionRequest(final PortAction<DynamicDistributor<T>> newPortActionRequest) {
		return portActions.offer(newPortActionRequest);
	}

	@Override
	public void onOutputPortRemoved(final Stage stage, final OutputPort<?> removedOutputPort) {
		removedOutputPort.sendSignal(new TerminatingSignal());
	}
}
