package teetime.framework.scheduling.globaltaskpool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jctools.queues.MpmcArrayQueue;

import teetime.framework.AbstractStage;

/**
 * Represents a task pool whose tasks are stages and categorized into levels.
 * A thread searches for the next task in the task pool starting at the deepest level and traversing up to the highest level, i.e., the root.
 * A stage at the deepest level has no output ports.
 */
class PrioritizedTaskPool {

	private static final int CAPACITY = 128;

	/** contains the stages categorized by their levels */
	private final List<MpmcArrayQueue<AbstractStage>> levels;

	public PrioritizedTaskPool(final int numLevels) {
		levels = new ArrayList<>(numLevels);
		for (int i = 0; i < numLevels; i++) {
			levels.add(new MpmcArrayQueue<>(CAPACITY)); // NOPMD (initialization)
		}
	}

	public boolean scheduleStages(final Collection<AbstractStage> stages) {
		boolean allScheduled = false;
		for (AbstractStage stage : stages) {
			boolean scheduled = scheduleStage(stage);
			allScheduled = allScheduled && scheduled;
		}
		return allScheduled;
	}

	public boolean scheduleStage(final AbstractStage stage) {
		MpmcArrayQueue<AbstractStage> stages = levels.get(stage.getLevelIndex());
		return stages.offer(stage);
	}

	/**
	 * @return and removes the next stage from this queue, or <code>null</code> otherwise.
	 */
	public AbstractStage removeNextStage() {
		// TODO requires O(n) so far. Try to improve.
		// => find non-empty lowest level in O(1)
		// corresponding ticket: https://build.se.informatik.uni-kiel.de/teetime/teetime/issues/343
		for (int i = levels.size() - 1; i >= 0; i--) {
			MpmcArrayQueue<AbstractStage> stages = levels.get(i);

			// AbstractStage stage = stages.peek();
			AbstractStage stage = stages.poll();

			// (only) read next stage with work
			if (null != stage) {
				// TODO possible alternative implementation: AbstractStage.getExecutingThread().compareAndSet()
				// ensure no other thread is executing the stage at this moment (this is our lock condition)
				// boolean notAlreadyContained = executingStages.add(stage);
				// if (/* stage.isStateless() || */ notAlreadyContained) {
				// return stages.poll(); // NOPMD (two returns in method)
				// }
				return stage;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		int sumSizes = 0;
		for (int i = levels.size() - 1; i >= 0; i--) {
			MpmcArrayQueue<AbstractStage> stages = levels.get(i);
			sumSizes += stages.size();
		}
		return super.toString() + "[" + "size=" + sumSizes + "]";
	}
}
