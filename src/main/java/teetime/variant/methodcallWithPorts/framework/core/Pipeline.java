package teetime.variant.methodcallWithPorts.framework.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import teetime.variant.methodcallWithPorts.framework.core.signal.Signal;
import teetime.variant.methodcallWithPorts.framework.core.signal.StartingSignal;
import teetime.variant.methodcallWithPorts.framework.core.validation.InvalidPortConnection;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;

/**
 *
 * @author Christian Wulf
 *
 * @param <FirstStage>
 * @param <LastStage>
 */
// BETTER remove the pipeline since it does not add any new functionality
public class Pipeline<FirstStage extends StageWithPort, LastStage extends StageWithPort> implements StageWithPort {

	private final String id;
	/**
	 * A unique logger instance per stage instance
	 */
	protected Log logger;

	private FirstStage firstStage;
	private final List<StageWithPort> intermediateStages = new LinkedList<StageWithPort>();
	private LastStage lastStage;

	private StageWithPort parentStage;

	// private final Set<StageWithPort<?, ?>> currentHeads = new HashSet<StageWithPort<?, ?>>();

	public Pipeline() {
		this(UUID.randomUUID().toString());
	}

	public Pipeline(final String id) {
		this.id = id; // the id should only be represented by a UUID, not additionally by the class name
		this.logger = LogFactory.getLog(this.id);
	}

	@Override
	public String getId() {
		return this.id;
	}

	public void setFirstStage(final FirstStage stage) {
		this.firstStage = stage;
	}

	public void addIntermediateStages(final StageWithPort... stages) {
		this.intermediateStages.addAll(Arrays.asList(stages));
	}

	public void addIntermediateStage(final StageWithPort stage) {
		this.intermediateStages.add(stage);
	}

	public void setLastStage(final LastStage stage) {
		this.lastStage = stage;
	}

	@Override
	public void executeWithPorts() {
		StageWithPort headStage = this.firstStage;

		// do {
		headStage.executeWithPorts();
		// } while (headStage.isReschedulable());

		// headStage.sendFinishedSignalToAllSuccessorStages();

		// this.updateRescheduable(headStage);

		// this.setReschedulable(headStage.isReschedulable());
	}

	// private final void updateRescheduable(final StageWithPort<?, ?> stage) {
	// StageWithPort<?, ?> currentStage = stage;
	// do {
	// this.firstStageIndex++;
	// // currentStage = currentStage.getOutputPort().getPipe().getTargetStage(); // FIXME what to do with a stage with more than one output port?
	// // if (currentStage == null) { // loop reaches the last stage
	// if (this.firstStageIndex == this.stages.length) { // loop reaches the last stage
	// this.setReschedulable(false);
	// this.cleanUp();
	// return;
	// }
	// currentStage = this.stages[this.firstStageIndex];
	// currentStage.onIsPipelineHead();
	// } while (!currentStage.isReschedulable());
	//
	// this.setReschedulable(true);
	// }

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

	@Deprecated
	public void onStarting() {
		int size = 1 + this.intermediateStages.size() + 1;
		StageWithPort[] stages = new StageWithPort[size];
		stages[0] = this.firstStage;
		for (int i = 0; i < this.intermediateStages.size(); i++) {
			StageWithPort stage = this.intermediateStages.get(i);
			stages[1 + i] = stage;
		}
		stages[stages.length - 1] = this.lastStage;

		// for (int i = 0; i < this.stages.length; i++) {
		// StageWithPort<?, ?> stage = this.stages[i];
		// stage.setParentStage(this, i);
		// stage.setListener(this);
		// }

		// for (int i = 0; i < this.stages.length - 1; i++) {
		// StageWithPort stage = this.stages[i];
		// stage.setSuccessor(this.stages[i + 1]);
		// }
		// this.stages[this.stages.length - 1].setSuccessor(new EndStage<Object>());

		for (StageWithPort stage : stages) {
			stage.onSignal(new StartingSignal(), null);
		}
	}

	@Override
	public StageWithPort getParentStage() {
		return this.parentStage;
	}

	@Override
	public void setParentStage(final StageWithPort parentStage, final int index) {
		this.parentStage = parentStage;
	}

	@Override
	public boolean isReschedulable() {
		return this.firstStage.isReschedulable();
	}

	@Override
	public void onSignal(final Signal signal, final InputPort<?> inputPort) {
		this.firstStage.onSignal(signal, inputPort);
	}

	public FirstStage getFirstStage() {
		return this.firstStage;
	}

	public LastStage getLastStage() {
		return this.lastStage;
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		// do nothing
	}

}
