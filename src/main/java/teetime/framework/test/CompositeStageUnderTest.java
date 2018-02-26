package teetime.framework.test;

import java.util.List;

import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

class CompositeStageUnderTest implements StageUnderTest {

	private final CompositeStage compositeStage;

	public CompositeStageUnderTest(final CompositeStage compositeStage) {
		this.compositeStage = compositeStage;
	}

	@Override
	public List<InputPort<?>> getInputPorts() {
		return compositeStage.getInputPorts();
	}

	@Override
	public List<OutputPort<?>> getOutputPorts() {
		return compositeStage.getOutputPorts();
	}

	@Override
	public void declareActive() {
		// TODO Auto-generated method stub

	}

}
