package teetime.stage.quicksort;

import teetime.stage.basic.AbstractTransformation;

class InitStage extends AbstractTransformation<int[], QuicksortTaskContext> {

	@Override
	protected void execute(int[] elements) throws Exception {
		QuicksortTaskContext quicksortTaskContext = new QuicksortTaskContext(elements);
		quicksortTaskContext.push(0, elements.length - 1);

		outputPort.send(quicksortTaskContext);
	}
	
}
