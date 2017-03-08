package teetime.framework.pipe.strategy;

import teetime.framework.StageState;
import teetime.framework.exceptionHandling.TerminateException;
import teetime.framework.pipe.IPipe;

public class SleepIfFullStrategy implements PipeElementInsertionStrategy {

	// statistics
	private int numWaits;

	@Override
	public boolean add(final IPipe<?> pipe, final Object element) {
		while (!pipe.addNonBlocking(element)) {
			// the following sending*-related lines are commented out since they are computationally too expensive
			// this.getSourcePort().getOwningStage().sendingFailed();
			// Thread.yield();
			StageState targetStageState = pipe.getTargetPort().getOwningStage().getCurrentState();
			if (targetStageState == StageState.TERMINATED ||
					Thread.currentThread().isInterrupted()) {
				throw TerminateException.INSTANCE;
			}
			this.numWaits++;
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignore) { // NOPMD can be interrupted w/o any reason
				throw TerminateException.INSTANCE;
			}
		}
		// this.getSourcePort().getOwningStage().sendingSucceeded();
		// this.reportNewElement();
		return true;
	}

	public int getNumWaits() {
		return numWaits;
	}

}
