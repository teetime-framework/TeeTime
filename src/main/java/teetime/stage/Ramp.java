package teetime.stage;

import teetime.stage.basic.AbstractFilter;

/**
 * Sends a sequence of numbers starting from 0 to the value 'n' (not included) received by the input port, i.e., (0,1,2,..,n-1).
 *
 * @author Christian Wulf (chw)
 *
 */
public class Ramp extends AbstractFilter<Integer> {

	@Override
	protected void execute(final Integer element) throws Exception {
		int count = element;
		for (int value = 0; value < count; value++) {
			outputPort.send(value);
		}
	}

}
