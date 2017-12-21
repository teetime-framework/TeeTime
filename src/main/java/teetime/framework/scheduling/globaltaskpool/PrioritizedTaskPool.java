package teetime.framework.scheduling.globaltaskpool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jctools.queues.MpmcArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.AbstractStage;

/**
 * Represents a task pool whose tasks are stages and categorized into levels.
 * A thread searches for the next task in the task pool starting at the deepest level and traversing up to the highest level, i.e., the root.
 * A stage at the deepest level has no output ports.
 */
class PrioritizedTaskPool implements ScheduleQueue {

	private static final int CAPACITY = 128;
	private static final Logger LOGGER = LoggerFactory.getLogger(PrioritizedTaskPool.class);

	/** contains the stages categorized by their levels */
	private final List<MpmcArrayQueue<AbstractStage>> levels;
	/** ensures that each stage is not added more than once */
	private final Set<AbstractStage> addedStages = ConcurrentHashMap.newKeySet();

	/**
	 * Creates a task pool with a default capacity of {@value #CAPACITY} for each level.
	 *
	 * @param numLevels
	 *            number of levels
	 */
	public PrioritizedTaskPool(final int numLevels) {
		this(numLevels, CAPACITY);
	}

	/**
	 *
	 * @param numLevels
	 *            number of levels
	 * @param capacity
	 *            of each level
	 */
	public PrioritizedTaskPool(final int numLevels, final int capacity) {
		levels = new ArrayList<>(numLevels);
		for (int i = 0; i < numLevels; i++) {
			levels.add(new MpmcArrayQueue<>(capacity)); // NOPMD (initialization)
		}
	}

	@Override
	public boolean scheduleStage(final AbstractStage stage) {
		if (!addedStages.add(stage)) {
			return true;
		}

		MpmcArrayQueue<AbstractStage> stages = levels.get(stage.getLevelIndex());

		boolean offered = stages.offer(stage);
		if (!offered) {
			Object peekElement = stages.peek();
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("(scheduleStage) Full level {} (size={}) with first element {}", stage.getLevelIndex(), stages.size(), peekElement);
			}
		}

		return offered;
	}

	@Override
	public AbstractStage removeNextStage() {
		return removeNextStage(levels.size() - 1);
	}

	public AbstractStage removeNextStage(final int deepestStartLevel) {
		// TODO requires O(n) so far. Try to improve.
		// => find non-empty lowest level in O(1)
		// corresponding ticket: https://build.se.informatik.uni-kiel.de/teetime/teetime/issues/343
		for (int i = deepestStartLevel; i >= 0; i--) {
			MpmcArrayQueue<AbstractStage> stages = levels.get(i);
			AbstractStage stage = stages.poll();
			// (only) read next stage with work
			if (null != stage) {
				// TODO possible alternative implementation: AbstractStage.getExecutingThread().compareAndSet()
				// ensure no other thread is executing the stage at this moment (this is our lock condition)
				// boolean notAlreadyContained = executingStages.add(stage);
				// if (/* stage.isStateless() || */ notAlreadyContained) {
				// return stages.poll(); // NOPMD (two returns in method)
				// }

				// AtomicInteger numSchedules = numSchedulesByStage.get(stage);
				// numSchedules.decrementAndGet();

				// the stage cannot be re-added to this pool until the following call 'remove' has finished
				addedStages.remove(stage);

				return stage;
			}
		}
		return null;
	}

	public int getNumLevels() {
		return levels.size();
	}

	@Override
	public String toString() { // IMPORTANT: do not manipulate the level queues in this method
		int sumSizes = 0;
		for (int i = levels.size() - 1; i >= 0; i--) {
			MpmcArrayQueue<AbstractStage> stages = levels.get(i);
			sumSizes += stages.size();
		}
		return super.toString() + "[" + "size=" + sumSizes + "]";
	}
}
