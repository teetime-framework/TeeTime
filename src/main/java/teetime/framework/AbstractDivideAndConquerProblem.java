package teetime.framework;

import org.apache.commons.math3.util.Pair;

import teetime.util.divideAndConquer.Identifiable;

public abstract class AbstractDivideAndConquerProblem<P, S> extends Identifiable {

	protected AbstractDivideAndConquerProblem(final int id) {
		super(id);
	}

	/**
	 * Determines whether or not to split the input problem by examining the given element
	 *
	 * @param element
	 *            The element whose properties determine the split condition
	 */
	protected abstract boolean isBaseCase();

	/**
	 * Method to divide the given input and send to the left and right output ports.
	 *
	 * @param element
	 *            An element to be split and further processed
	 */
	protected abstract Pair<P, P> divide();

	/**
	 * Method to process the given input and send to the output port.
	 *
	 * @param element
	 *            An element to be processed
	 */
	protected abstract S solve();

}
