package teetime.framework;

/**
 * Represents a counter which does not need to be boxed and unboxed each time it is accessed by a stage.
 *
 * @author Christian Wulf
 *
 */
class CounterContainer {

	private long counter;

	public String inc() {
		counter++;
		return "";
	}

	public long getCounter() {
		return counter;
	}

	public void reset() {
		counter = 0;
	}
}
