package teetime.stage.quicksort;

import teetime.stage.basic.AbstractFilter;

class PushRightSideStage extends AbstractFilter<QuicksortTaskContext> {

	@Override
	protected void execute(QuicksortTaskContext context) throws Exception {
		if (context.getPivotIndex() + 1 < context.getHighestIndex()) {
			context.push(context.getPivotIndex() + 1, context.getHighestIndex());
		}

		outputPort.send(context);
	}

}
