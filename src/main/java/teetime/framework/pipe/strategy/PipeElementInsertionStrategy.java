package teetime.framework.pipe.strategy;

import teetime.framework.pipe.IPipe;

public interface PipeElementInsertionStrategy {

	boolean add(IPipe<?> pipe, Object element);
}
