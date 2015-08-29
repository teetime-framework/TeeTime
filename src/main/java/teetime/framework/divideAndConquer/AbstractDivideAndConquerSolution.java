package teetime.framework.divideAndConquer;

public abstract class AbstractDivideAndConquerSolution<S> extends Identifiable {

	protected AbstractDivideAndConquerSolution() {
		super();
	}

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
	public abstract S combine(S s1);

}
