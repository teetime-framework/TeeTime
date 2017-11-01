package teetime.stage.basic.merger.strategy;

import org.junit.Test;

import teetime.framework.InputPort;
import teetime.stage.basic.merger.dynamic.DynamicMerger;

public class NonBlockingFiniteRoundRobinStrategyTest {

	@Test(expected = ArithmeticException.class)
	public void testWithZeroInputPorts() throws Exception {
		NonBlockingFiniteRoundRobinStrategy strategy = new NonBlockingFiniteRoundRobinStrategy();
		DynamicMerger<Object> merger = new DynamicMerger<>(strategy);

		InputPort<Object> inputPort = merger.getNewInputPort();
		merger.removeDynamicPort(inputPort);
	}
}
