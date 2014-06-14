package teetime.examples.throughput.methodcall;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import teetime.util.list.CommittableQueue;

public class Pipeline<I, O> implements Stage<I, O> {

	private Stage firstStage;
	private final List<Stage> intermediateStages = new LinkedList<Stage>();
	private Stage lastStage;

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
		CommittableQueue queue = this.firstStage.execute2(elements);
		for (Stage<?, ?> stage : this.intermediateStages) {
			queue = stage.execute2(queue);
		}
		return this.lastStage.execute2(queue);
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
	}
	//
	// @Override
	// public InputPort getInputPort() {
	// return this.firstStage.getInputPort();
	// }

	// @Override
	// public OutputPort getOutputPort() {
	// return this.lastStage.getOutputPort();
	// }

}
