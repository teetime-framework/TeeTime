package teetime.framework.pipe.strategy;

import teetime.framework.pipe.IPipe;

public class SkipIfFullStrategy implements PipeElementInsertionStrategy {

	@Override
	public boolean add(final IPipe<?> pipe, final Object element) {
		return pipe.addNonBlocking(element);
	}

}
