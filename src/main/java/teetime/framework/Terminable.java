package teetime.framework;

interface Terminable {

	TerminationStrategy getTerminationStrategy();

	void terminate();

	boolean shouldBeTerminated();

}
