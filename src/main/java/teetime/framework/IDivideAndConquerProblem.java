package teetime.framework;

import org.apache.commons.math3.util.Pair;

public interface IDivideAndConquerProblem<P, S> {
	/**
	 * Determines whether or not to split the input problem by examining the given element
	 *
	 * @param element
	 *            The element whose properties determine the split condition
	 */
	boolean isBaseCase();

	/**
	 * Method to divide the given input and send to the left and right output ports.
	 *
	 * @param element
	 *            An element to be split and further processed
	 */
	Pair<P, P> divide();

	/**
	 * Method to process the given input and send to the output port.
	 *
	 * @param element
	 *            An element to be processed
	 */
	S solve();
}
