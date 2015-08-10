package teetime.framework;

public interface IDivideAndConquerSolution<S> {
	/**
	 * Method to join the given inputs together and send to the output port.
	 *
	 * @param eLeft
	 *            First half of the resulting element.
	 * @param eRight
	 *            Second half of the resulting element.
	 */
	S combine(S s1);
}
