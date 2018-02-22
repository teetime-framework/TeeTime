package teetime.stage.quicksort;

import teetime.stage.basic.AbstractFilter;

class SetupRangeStage extends AbstractFilter<QuicksortTaskContext> {

	@Override
	protected void execute(QuicksortTaskContext context) throws Exception {
		context.setRange();

		outputPort.send(context);
	}

}
