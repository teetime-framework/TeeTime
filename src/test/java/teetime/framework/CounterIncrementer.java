package teetime.framework;

import teetime.stage.basic.AbstractFilter;

class CounterIncrementer extends AbstractFilter<CounterContainer> {

	@Override
	protected void execute(final CounterContainer element) {
		element.inc();
		getOutputPort().send(element);
	}

}
