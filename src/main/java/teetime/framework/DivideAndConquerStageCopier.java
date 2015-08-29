package teetime.framework;

import teetime.framework.divideAndConquer.AbstractDivideAndConquerProblem;
import teetime.framework.divideAndConquer.AbstractDivideAndConquerSolution;
import teetime.framework.signal.StartingSignal;

/**
 * This is not a real factory but rather a synchronized class to create new divide and conquer stages
 *
 * @author Robin Mohr
 *
 */
public class DivideAndConquerStageCopier {
	private DivideAndConquerStageCopier() {}

	private static class LazyInitialization {
		private static final DivideAndConquerStageCopier INSTANCE = new DivideAndConquerStageCopier();
	}

	public static DivideAndConquerStageCopier getInstance() {
		return LazyInitialization.INSTANCE;
	}

	protected synchronized <P extends AbstractDivideAndConquerProblem<P, S>, S extends AbstractDivideAndConquerSolution<S>> void makeCopy(
			final OutputPort<P> outputPort,
			final InputPort<S> inputPort, final DivideAndConquerStage<P, S> callingStage) {
		if (callingStage.isThresholdReached()) {
			new DivideAndConquerRecursivePipe<P, S>(outputPort, inputPort);
		} else {
			DivideAndConquerStage<P, S> newStage = callingStage.duplicate();
			DynamicConfigurationContext.INSTANCE.connectPorts(outputPort, newStage.getInputPort());
			DynamicConfigurationContext.INSTANCE.connectPorts(newStage.getOutputPort(), inputPort);
			outputPort.sendSignal(new StartingSignal());
			RuntimeServiceFacade.INSTANCE.startWithinNewThread(callingStage, newStage);
		}
	}
}
