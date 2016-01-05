package teetime.examples.filecounter;

import teetime.framework.AbstractConsumerStage;

class ResultPrinter extends AbstractConsumerStage<Integer> {

	@Override
	protected void execute(final Integer element) {
		System.out.println("Result: " + element);
	}

}
