package teetime.stage;

import teetime.stage.basic.AbstractFilter;

/**
 * Sends a sequence of numbers starting from 0 to the value 'n' (not included) received by the input port, i.e., (0,1,2,..,n-1).
 *
 * @author Christian Wulf (chw)
 *
 */
public class Ramp extends AbstractFilter<Long> {

	@Override
	protected void execute(final Long element) throws Exception {
		long count = element;
		for (long value = 0; value < count; value++) {
			outputPort.send(value);
		}
	}

}
