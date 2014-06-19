package teetime.variant.methodcallWithPorts.framework.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import teetime.util.list.CommittableQueue;
import teetime.variant.methodcallWithPorts.stage.EndStage;

public class Pipeline<I, O> implements StageWithPort<I, O> {

	private StageWithPort<I, ?> firstStage;
	private final List<StageWithPort<?, ?>> intermediateStages = new LinkedList<StageWithPort<?, ?>>();
	private StageWithPort<?, O> lastStage;

	private StageWithPort<?, ?>[] stages;
	private StageWithPort<?, ?> parentStage;
	private int index;
	private int startIndex;

	private boolean reschedulable;
	private int firstStageIndex;

	public void setFirstStage(final StageWithPort<I, ?> stage) {
		this.firstStage = stage;
	}

	public void addIntermediateStages(final StageWithPort<?, ?>... stages) {
		this.intermediateStages.addAll(Arrays.asList(stages));
	}

	public void addIntermediateStage(final StageWithPort stage) {
		this.intermediateStages.add(stage);
	}

	public void setLastStage(final StageWithPort<?, O> stage) {
		this.lastStage = stage;
	}

	@Override
	public CommittableQueue<O> execute2(final CommittableQueue<I> elements) {
		// CommittableQueue queue = this.firstStage.execute2(elements);
		// for (Stage<?, ?> stage : this.intermediateStages) {
		// queue = stage.execute2(queue);
		// }
		// return this.lastStage.execute2(queue);

		// below is faster than above (probably because of the instantiation of a list iterator in each (!) execution)
		CommittableQueue queue = elements;

		// for (int i = this.startIndex; i < this.stages.length; i++) {
		// Stage<?, ?> stage = this.stages[i];
		// queue = stage.execute2(queue);
		// }

		this.firstStage.execute2(elements);
		this.setReschedulable(this.firstStage.isReschedulable());
		return queue;
	}

	@Override
	public void executeWithPorts() {
		StageWithPort<?, ?> firstStage = this.stages[this.firstStageIndex];
		firstStage.executeWithPorts();

		this.updateRescheduable(firstStage);
		// this.setReschedulable(stage.isReschedulable());
	}

	private final void updateRescheduable(final StageWithPort<?, ?> stage) {
		StageWithPort<?, ?> currentStage = stage;
		while (!currentStage.isReschedulable()) {
			this.firstStageIndex++;
			currentStage = currentStage.next();
			if (currentStage == null) { // loop reaches the last stage
				this.setReschedulable(false);
				this.cleanUp();
				return;
			}
			currentStage.onIsPipelineHead();
			// System.out.println("firstStageIndex: " + this.firstStageIndex + ", class:" + stage.getClass().getSimpleName());
		}
		this.setReschedulable(true);
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

	@Override
	public void onStart() {
		// Pipe pipe = new Pipe();
		// this.outputPort.pipe = pipe;
		// this.firstStage.getInputPort().pipe = pipe;

		// Pipe pipe = new Pipe();
		// this.firstStage.getOutputPort().pipe = pipe;
		// this.intermediateStages.get(0).getInputPort().pipe = pipe;
		//
		// for (int i = 0; i < this.intermediateStages.size() - 1; i++) {
		// Stage left = this.intermediateStages.get(i);
		// Stage right = this.intermediateStages.get(i + 1);
		//
		// pipe = new Pipe();
		// left.getOutputPort().pipe = pipe;
		// right.getInputPort().pipe = pipe;
		// }
		//
		// pipe = new Pipe();
		// this.intermediateStages.get(this.intermediateStages.size() - 1).getOutputPort().pipe = pipe;
		// this.lastStage.getInputPort().pipe = pipe;

		int size = 1 + this.intermediateStages.size() + 1;
		this.stages = new StageWithPort[size];
		this.stages[0] = this.firstStage;
		for (int i = 0; i < this.intermediateStages.size(); i++) {
			StageWithPort<?, ?> stage = this.intermediateStages.get(i);
			this.stages[1 + i] = stage;
		}
		this.stages[this.stages.length - 1] = this.lastStage;

		for (int i = 0; i < this.stages.length; i++) {
			// StageWithPort<?, ?> stage = this.stages[i];
			// stage.setParentStage(this, i);
			// stage.setListener(this);
		}

		for (int i = 0; i < this.stages.length - 1; i++) {
			StageWithPort stage = this.stages[i];
			stage.setSuccessor(this.stages[i + 1]);
		}
		this.stages[this.stages.length - 1].setSuccessor(new EndStage<Object>());

		for (StageWithPort<?, ?> stage : this.stages) {
			stage.onStart();
		}
	}

	@Override
	public StageWithPort<?, ?> getParentStage() {
		return this.parentStage;
	}

	@Override
	public void setParentStage(final StageWithPort<?, ?> parentStage, final int index) {
		this.index = index;
		this.parentStage = parentStage;
	}

	@Override
	public StageWithPort<?, ?> next() {
		throw new IllegalStateException();
	}

	@Override
	public void setSuccessor(final StageWithPort<? super O, ?> successor) {
		throw new IllegalStateException();
	}

	@Override
	public boolean isReschedulable() {
		return this.reschedulable;
	}

	public void setReschedulable(final boolean reschedulable) {
		this.reschedulable = reschedulable;
	}

	@Override
	public InputPort<I> getInputPort() {
		return this.firstStage.getInputPort();
	}

	@Override
	public OutputPort<O> getOutputPort() {
		return this.lastStage.getOutputPort();
	}

	// TODO remove since it does not increase performances
	private void cleanUp() {
		for (int i = 0; i < this.stages.length; i++) {
			StageWithPort<?, ?> stage = this.stages[i];
			stage.setParentStage(null, i);
			// stage.setListener(null);
			stage.setSuccessor(null);
		}

		this.firstStage = null;
		this.intermediateStages.clear();
		this.lastStage = null;

		System.out.println("cleaned up");
	}

}
