package teetime.framework.scheduling.globaltaskqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jctools.queues.MpmcArrayQueue;

import teetime.framework.AbstractStage;

/**
 * Represents a queue whose tasks are categorized into levels.
 */
class TaskQueue {

	private static final int CAPACITY = 128;

	/** contains the stages categorized by their levels */
	private final List<MpmcArrayQueue<AbstractStage>> levels;
	/**
	 * Holds all stages that are currently executed by a thread.
	 * <br>
	 * <i>(synchronized map)</i>
	 */
	private final ConcurrentMap<AbstractStage, Thread> executingStages = new ConcurrentHashMap<>();

	// private final AtomicInteger lowestLevelPointer = new AtomicInteger(0);

	public TaskQueue(final int numLevels) {
		levels = new ArrayList<>(numLevels);
		for (int i = 0; i < numLevels; i++) {
			levels.add(new MpmcArrayQueue<>(CAPACITY)); // NOPMD (initialization)
		}
	}

	public void scheduleStages(final List<AbstractStage> stages) {
		for (AbstractStage stage : stages) {
			scheduleStage(stage);
		}
	}

	public void scheduleStage(final AbstractStage stage) {
		MpmcArrayQueue<AbstractStage> stages = levels.get(stage.getLevelIndex());
		stages.add(stage);
	}

	/**
	 * @return and removes the next stage from this queue, or <code>null</code> otherwise.
	 */
	public AbstractStage removeNextStage() {
		// TODO requires O(n) so far. Try to improve.
		// => find non-empty lowest level in O(1)
		for (int i = levels.size() - 1; i >= 0; i--) {
			MpmcArrayQueue<AbstractStage> stages = levels.get(i);

			AbstractStage stage = stages.peek();

			// (only) read next stage with work
			if (null != stage) {
				Thread thisThread = Thread.currentThread();
				// TODO possible alternative implementation: AbstractStage.getExecutingThread().compareAndSet()
				Thread executingThread = executingStages.putIfAbsent(stage, thisThread);
				// ensure no other thread is executing the stage at this moment (this is our lock condition)
				if (executingThread == thisThread) { // NOPMD (== is correct)
					return stages.poll(); // NOPMD (two returns in method)
				}
			}
		}
		return null;
	}

	public void releaseStage(final AbstractStage stage) {
		executingStages.remove(stage);
	}
}
