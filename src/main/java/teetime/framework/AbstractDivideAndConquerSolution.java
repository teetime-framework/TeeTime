package teetime.framework;

import teetime.util.divideAndConquer.Identifiable;


public abstract class AbstractDivideAndConquerSolution<S> extends Identifiable {

	protected AbstractDivideAndConquerSolution(final int id) {
		super(id);
	}

	/**
	 * Method to join the given inputs together and send to the output port.
	 *
	 * @param eLeft
	 *            First half of the resulting element.
	 * @param eRight
	 *            Second half of the resulting element.
	 */
	protected abstract S combine(S s1);

}
