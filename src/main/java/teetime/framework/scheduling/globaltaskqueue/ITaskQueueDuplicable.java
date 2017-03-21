package teetime.framework.scheduling.globaltaskqueue;

import teetime.framework.AbstractStage;

/**
 * Created by nilsziermann on 29.12.16.
 */
public interface ITaskQueueDuplicable extends ITaskQueueInformation {
	public AbstractStage duplicate();
}
