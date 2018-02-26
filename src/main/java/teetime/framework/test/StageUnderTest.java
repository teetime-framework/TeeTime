package teetime.framework.test;

import java.util.List;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public interface StageUnderTest {

	List<InputPort<?>> getInputPorts();

	List<OutputPort<?>> getOutputPorts();

	void declareActive();
}
