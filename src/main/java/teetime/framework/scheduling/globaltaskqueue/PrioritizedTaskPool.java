package teetime.framework.scheduling.globaltaskqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
	/**
	 * Holds all stages that are currently executed by a thread.
	 * <br>
	 * <i>(synchronized map)</i>
	 */
	private final ConcurrentMap<AbstractStage, Thread> executingStages = new ConcurrentHashMap<>();

	// private final AtomicInteger lowestLevelPointer = new AtomicInteger(0);

	public PrioritizedTaskPool(final int numLevels) {
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
		while (!stages.offer(stage)) {
			// wait for the queue to become non-full
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * @return and removes the next stage from this queue, or <code>null</code> otherwise.
	 */
	public AbstractStage removeNextStage() {
		// TODO requires O(n) so far. Try to improve.
		// => find non-empty lowest level in O(1)
		// corresponding ticket: https://build.se.informatik.uni-kiel.de/teetime/teetime/issues/336
		for (int i = levels.size() - 1; i >= 0; i--) {
			MpmcArrayQueue<AbstractStage> stages = levels.get(i);

			AbstractStage stage = stages.peek();

			// (only) read next stage with work
			if (null != stage) {
				Thread thisThread = Thread.currentThread();
				// TODO possible alternative implementation: AbstractStage.getExecutingThread().compareAndSet()
				Thread executingThread = executingStages.putIfAbsent(stage, thisThread);
				// ensure no other thread is executing the stage at this moment (this is our lock condition)
				if (stage.isStateless() || executingThread == thisThread) { // NOPMD (== is correct)
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
