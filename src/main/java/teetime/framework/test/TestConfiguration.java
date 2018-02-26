package teetime.framework.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teetime.framework.Configuration;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.DummyPipe;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

class TestConfiguration extends Configuration {

	@SuppressWarnings({ "PMD.AvoidInstantiatingObjectsInLoops", "unchecked" })
	public TestConfiguration(final StageTester stageTester) {
		StageUnderTest stageUnderTest = stageTester.getStageUnderTest();

		Map<InputPort<Object>, List<Object>> inputElementsByPort = stageTester.getInputElementsByPort();
		for (InputPort<?> inputPort : stageUnderTest.getInputPorts()) {
			if (inputPort.getPipe() != null) {
				continue; // skip reflexive/loop pipe
			}
			List<Object> inputElements = inputElementsByPort.getOrDefault(inputPort, new ArrayList<>());
			final InitialElementProducer<?> producer = StageFactory.createProducer(inputElements);
			connectPorts(producer.getOutputPort(), (InputPort<Object>) inputPort);
		}

		// declareActive that works for both AbstractStage and CompositeStage
		for (InputPort<?> inputPort : stageUnderTest.getInputPorts()) {
			inputPort.getOwningStage().declareActive();
		}

		Map<OutputPort<Object>, List<Object>> outputElementsByPort = stageTester.getOutputElementsByPort();
		for (OutputPort<?> outputPort : stageUnderTest.getOutputPorts()) {
			if (outputPort.getPipe() != DummyPipe.INSTANCE) {
				continue; // skip reflexive/loop pipe
			}
			List<Object> outputElements = outputElementsByPort.getOrDefault(outputPort, new ArrayList<>());
			final CollectorSink<Object> sink = StageFactory.createSink(outputElements);
			connectPorts(outputPort, sink.getInputPort());
		}
	}

}
