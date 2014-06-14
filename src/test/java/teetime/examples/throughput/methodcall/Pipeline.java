package teetime.examples.throughput.methodcall;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import teetime.util.list.CommittableQueue;

public class Pipeline<I, O> implements Stage<I, O>, OnDisableListener {

	private Stage firstStage;
	private final List<Stage> intermediateStages = new LinkedList<Stage>();
	private Stage lastStage;

	private final SchedulingInformation schedulingInformation = new SchedulingInformation();

	private Stage[] stages;
	private Stage parentStage;
	private int index;
	private int startIndex;
	private OnDisableListener listener;

	void setFirstStage(final Stage<I, ?> stage) {
		this.firstStage = stage;
	}

	void addIntermediateStages(final Stage... stages) {
		this.intermediateStages.addAll(Arrays.asList(stages));
	}

	void addIntermediateStage(final Stage stage) {
		this.intermediateStages.add(stage);
	}

	void setLastStage(final Stage<?, O> stage) {
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
		for (int i = this.startIndex; i < this.stages.length; i++) {
			Stage<?, ?> stage = this.stages[i];
			queue = stage.execute2(queue);
		}
		return queue;
	}

	void onStart() {
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
		this.stages = new Stage[size];
		this.stages[0] = this.firstStage;
		for (int i = 0; i < this.intermediateStages.size(); i++) {
			Stage<?, ?> stage = this.intermediateStages.get(i);
			this.stages[1 + i] = stage;
		}
		this.stages[this.stages.length - 1] = this.lastStage;

		for (int i = 0; i < this.stages.length; i++) {
			Stage<?, ?> stage = this.stages[i];
			stage.setParentStage(this, i);
			stage.setListener(this);
		}
	}

	//
	// @Override
	// public InputPort getInputPort() {
	// return this.firstStage.getInputPort();
	// }

	@Override
	public SchedulingInformation getSchedulingInformation() {
		return this.schedulingInformation;
	}

	@Override
	public Stage getParentStage() {
		return this.parentStage;
	}

	@Override
	public void setParentStage(final Stage parentStage, final int index) {
		this.index = index;
		this.parentStage = parentStage;
	}

	@Override
	public void onDisable(final Stage stage, final int index) {
		this.startIndex = index + 1;
		if (this.startIndex == this.stages.length) {
			this.disable();
		}
	}

	public void disable() {
		this.schedulingInformation.setActive(false);
		this.fireOnDisable();
	}

	private void fireOnDisable() {
		if (this.listener != null) {
			this.listener.onDisable(this, this.index);
		}
	}

	public OnDisableListener getListener() {
		return this.listener;
	}

	@Override
	public void setListener(final OnDisableListener listener) {
		this.listener = listener;
	}

	// @Override
	// public OutputPort getOutputPort() {
	// return this.lastStage.getOutputPort();
	// }

}
