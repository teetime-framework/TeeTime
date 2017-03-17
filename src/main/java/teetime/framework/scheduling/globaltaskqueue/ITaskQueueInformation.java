package teetime.framework.scheduling.globaltaskqueue;

import teetime.framework.InputPort;

/**
 * Created by nilsziermann on 04.01.17.
 */
public interface ITaskQueueInformation {
	public int numElementsToDrainPerExecute(InputPort inputPort);
}
