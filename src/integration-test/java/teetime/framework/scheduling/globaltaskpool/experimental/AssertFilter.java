package teetime.framework.scheduling.globaltaskpool.experimental;

import teetime.stage.basic.AbstractFilter;

public class AssertFilter extends AbstractFilter<Integer> {

	private Integer lastElement = -1;

	@Override
	protected void execute(final Integer element) throws Exception {
		Integer localLastElement = lastElement;
		if (localLastElement + 1 != element) {
			String message = String.format("lastElement=%d vs. element=%s", localLastElement, element);
			throw new IllegalStateException(message);
		}

		lastElement = element;

		outputPort.send(element);
	}

}
