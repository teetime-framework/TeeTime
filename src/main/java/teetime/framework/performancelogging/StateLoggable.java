package teetime.framework.performancelogging;

import java.util.List;

public interface StateLoggable {

	/**
	 * This method is used to collect the List of States
	 *
	 * @return List of states this stage saved during its run.
	 */
	public List<ActivationState> getStates();

	/**
	 * This method is called by Pipes if the sending of the next element needs to be delayed because of full Queue.
	 */
	public void sendingFailed();

	/**
	 * This method is called when the element is successfully added to the Pipe.
	 */
	public void sendingSucceeded();

	/**
	 * This method is called when the Thread returns to a Stage that send an element before.
	 */
	public void sendingReturned();

}
