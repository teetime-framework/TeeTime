package teetime.framework.scheduling.globaltaskpool;

import java.util.Collection;

import teetime.framework.AbstractStage;

/**
 * Represents a queue which can be used by the scheduler.
 * By definition, this data structure is ordered and potentially prioritized.
 *
 * @author Christian Wulf (chw)
 *
 * @since 3.0
 */
public interface ScheduleQueue {

	/**
	 * @return and removes the next stage from this queue, or <code>null</code> otherwise.
	 */
	AbstractStage removeNextStage();

	/**
	 * @param stage
	 *            to be scheduled
	 * @return <code>true</code> iff the given stage could be scheduled, otherwise <code>false</code>.
	 */
	boolean scheduleStage(final AbstractStage stage);

	/**
	 * @param stages
	 *            to be scheduled
	 * @return <code>true</code> iff all of the given stages could be scheduled, otherwise <code>false</code>.
	 */
	default boolean scheduleStages(final Collection<? extends AbstractStage> stages) {
		boolean scheduledAllStages = true;
		for (AbstractStage stage : stages) {
			boolean scheduledStage = scheduleStage(stage);
			scheduledAllStages = scheduledAllStages && scheduledStage;
		}
		return scheduledAllStages;
	}

}
