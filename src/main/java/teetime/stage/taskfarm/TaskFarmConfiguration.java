package teetime.stage.taskfarm;

import java.util.LinkedList;
import java.util.List;

import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.merger.dynamic.DynamicMerger;

public class TaskFarmConfiguration<I, O, TFS extends TaskFarmDuplicable<I, O>> {

	private final List<TaskFarmTriple<I, O, TFS>> triples = new LinkedList<TaskFarmTriple<I, O, TFS>>();

	private final DynamicDistributor<I> distributor = new DynamicDistributor<I>();
	private final DynamicMerger<O> merger = new DynamicMerger<O>();

	private final TFS firstStage;

	public TaskFarmConfiguration(final TFS firstStage) {
		this.firstStage = firstStage;
	}

	public List<TaskFarmTriple<I, O, TFS>> getTriples() {
		return triples;
	}

	public DynamicDistributor<I> getDistributor() {
		return distributor;
	}

	public DynamicMerger<O> getMerger() {
		return merger;
	}

	public TFS getFirstStage() {
		return firstStage;
	}
}
