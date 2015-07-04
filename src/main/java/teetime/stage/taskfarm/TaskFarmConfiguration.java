package teetime.stage.taskfarm;

import java.util.LinkedList;
import java.util.List;

import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.merger.dynamic.DynamicMerger;

/**
 * This class contains the configuration of a single Task Farm.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of enclosed stage
 */
public class TaskFarmConfiguration<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final List<TaskFarmTriple<I, O, T>> triples = new LinkedList<TaskFarmTriple<I, O, T>>();

	private final DynamicDistributor<I> distributor = new DynamicDistributor<I>();
	private final DynamicMerger<O> merger = new DynamicMerger<O>();

	private final T firstStage;

	private int analysisWindow = 3;

	/**
	 * Constructor.
	 *
	 * @param firstStage
	 *            first instance of the enclosed stage given to the task farm itself
	 */
	public TaskFarmConfiguration(final T firstStage) {
		this.firstStage = firstStage;
	}

	public List<TaskFarmTriple<I, O, T>> getTriples() {
		return this.triples;
	}

	public DynamicDistributor<I> getDistributor() {
		return this.distributor;
	}

	public DynamicMerger<O> getMerger() {
		return this.merger;
	}

	public T getFirstStage() {
		return this.firstStage;
	}

	public int getAnalysisWindow() {
		return this.analysisWindow;
	}

	public void setAnalysisWindow(final int analysisWindow) {
		this.analysisWindow = analysisWindow;
	}
}
