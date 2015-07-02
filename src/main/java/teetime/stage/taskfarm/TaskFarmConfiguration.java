package teetime.stage.taskfarm;

import java.util.HashMap;
import java.util.Map;

import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.merger.Merger;
import teetime.stage.basic.merger.dynamic.DynamicMerger;

public class TaskFarmConfiguration<I, O, TFS extends TaskFarmDuplicable<I, O>> {

	private final Map<Integer, TaskFarmTriple<I, O, TFS>> triples = new HashMap<Integer, TaskFarmTriple<I, O, TFS>>();

	private final DynamicDistributor<I> distributor = new DynamicDistributor<I>();
	private final DynamicMerger<O> merger = new DynamicMerger<O>();

	private final TFS firstStage;

	public TaskFarmConfiguration(final TFS firstStage) {
		this.firstStage = firstStage;
	}

	public Map<Integer, TaskFarmTriple<I, O, TFS>> getTriples() {
		return triples;
	}

	public Distributor<I> getDistributor() {
		return distributor;
	}

	public Merger<O> getMerger() {
		return merger;
	}

	public TFS getFirstStage() {
		return firstStage;
	}
}
