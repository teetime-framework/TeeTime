package teetime.stage.quicksort;

import teetime.framework.*;

/**
 * A P&F implementation of the iterative Quicksort algorithm.
 * 
 * @author Christian Wulf
 * 
 * @see https://www.geeksforgeeks.org/iterative-quick-sort
 */
public class QuicksortStage extends CompositeStage {

	private final InputPort<int[]> inputPort;
	private final OutputPort<int[]> outputPort;

	public QuicksortStage() {
		InitStage initStage = new InitStage();
		IfStage ifStage = new IfStage();
		SetupRangeStage setupRangeStage = new SetupRangeStage();
		PartitionStage partitionStage = new PartitionStage();
		PushLeftSideStage pushLeftSideStage = new PushLeftSideStage();
		PushRightSideStage pushRightSideStage = new PushRightSideStage();

		inputPort = initStage.getInputPort();

		connectPorts(initStage.getOutputPort(), ifStage.getNewTaskInputPort());

		connectPorts(ifStage.getTrueOutputPort(), setupRangeStage.getInputPort());
		connectPorts(setupRangeStage.getOutputPort(), partitionStage.getInputPort());
		connectPorts(partitionStage.getOutputPort(), pushLeftSideStage.getInputPort());
		connectPorts(pushLeftSideStage.getOutputPort(), pushRightSideStage.getInputPort());
		connectPorts(pushRightSideStage.getOutputPort(), ifStage.getSubTaskInputPort());
		// -> feedback loop to the ifStage

		outputPort = ifStage.getFalseOutputPort();
	}

	public InputPort<int[]> getInputPort() {
		return inputPort;
	}

	public OutputPort<int[]> getOutputPort() {
		return outputPort;
	}
}
