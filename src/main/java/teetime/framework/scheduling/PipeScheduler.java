package teetime.framework.scheduling;

import teetime.framework.pipe.AbstractSynchedPipe;
import teetime.framework.pipe.AbstractUnsynchedPipe;

public interface PipeScheduler {

	/**
	 * This event is invoked by the given <b>unsynchronized</b> pipe whenever a new element was added to it.
	 *
	 * @param pipe
	 */
	void onElementAdded(AbstractUnsynchedPipe<?> pipe);

	/**
	 * This event is invoked by the given <b>synchronized</b> pipe whenever a new element was added to it.
	 *
	 * @param pipe
	 */
	void onElementAdded(AbstractSynchedPipe<?> pipe);

}
