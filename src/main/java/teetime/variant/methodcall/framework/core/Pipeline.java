package teetime.variant.methodcall.framework.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import teetime.util.list.CommittableQueue;
import teetime.variant.methodcall.stage.EndStage;

public class Pipeline<I, O> implements Stage<I, O> {

	private Stage<I, ?> firstStage;
	private final List<Stage<?, ?>> intermediateStages = new LinkedList<Stage<?, ?>>();
	private Stage<?, O> lastStage;

	private Stage<?, ?>[] stages;
	private Stage<?, ?> parentStage;
	private int index;

	private boolean reschedulable;

	public void setFirstStage(final Stage<I, ?> stage) {
		this.firstStage = stage;
	}

	public void addIntermediateStages(final Stage<?, ?>... stages) {
		this.intermediateStages.addAll(Arrays.asList(stages));
	}

	public void addIntermediateStage(final Stage<?, ?> stage) {
		this.intermediateStages.add(stage);
	}

	public void setLastStage(final Stage<?, O> stage) {
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

		for (int i = 0; i < this.stages.length; i++) {
			Stage<?, ?> stage = this.stages[i];
			queue = stage.execute2(queue);
			if (queue.isEmpty()) {
				break;
			}
		}

		// queue = this.firstStage.execute2(elements);

		this.setReschedulable(this.firstStage.isReschedulable());

		return queue;
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

	@Override
	public Object executeRecursively(final Object element) {
		return this.firstStage.executeRecursively(element);
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
		this.stages = new Stage[size];
		this.stages[0] = this.firstStage;
		for (int i = 0; i < this.intermediateStages.size(); i++) {
			Stage<?, ?> stage = this.intermediateStages.get(i);
			this.stages[1 + i] = stage;
		}
		this.stages[this.stages.length - 1] = this.lastStage;

		for (int i = 0; i < this.stages.length; i++) {
			// Stage<?, ?> stage = this.stages[i];
			// stage.setParentStage(this, i);
			// stage.setListener(this);
		}

		for (int i = 0; i < this.stages.length - 1; i++) {
			Stage stage = this.stages[i];
			stage.setSuccessor(this.stages[i + 1]);
		}
		this.stages[this.stages.length - 1].setSuccessor(new EndStage<Object>());

		for (Stage<?, ?> stage : this.stages) {
			stage.onStart();
		}
	}

	@Override
	public Stage<?, ?> getParentStage() {
		return this.parentStage;
	}

	@Override
	public void setParentStage(final Stage<?, ?> parentStage, final int index) {
		this.index = index;
		this.parentStage = parentStage;
	}

	@Override
	public O execute(final Object element) {
		throw new IllegalStateException();
	}

	@Override
	public Stage next() {
		throw new IllegalStateException();
	}

	@Override
	public void setSuccessor(final Stage<? super O, ?> successor) {
		throw new IllegalStateException();
	}

	public void setReschedulable(final boolean reschedulable) {
		this.reschedulable = reschedulable;
	}

	@Override
	public boolean isReschedulable() {
		return this.reschedulable;
	}

}
