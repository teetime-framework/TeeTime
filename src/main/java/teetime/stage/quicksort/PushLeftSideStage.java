package teetime.stage.quicksort;

import teetime.stage.basic.AbstractFilter;

class PushLeftSideStage extends AbstractFilter<QuicksortTaskContext> {

	@Override
	protected void execute(QuicksortTaskContext context) throws Exception {
		if (context.getPivotIndex() - 1 > context.getLowestIndex()) {
			context.push(context.getLowestIndex(), context.getPivotIndex() - 1);
		}

		outputPort.send(context);
	}

}
