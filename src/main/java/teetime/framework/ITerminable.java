package teetime.framework;

interface ITerminable {

	TerminationStrategy getTerminationStrategy();

	void terminate();

	boolean shouldBeTerminated();

}
