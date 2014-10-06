package teetime.framework;

import java.util.LinkedList;
import java.util.List;

public class AnalysisConfiguration {

	private final List<HeadStage> consumerStages = new LinkedList<HeadStage>();
	private final List<HeadStage> finiteProducerStages = new LinkedList<HeadStage>();
	private final List<HeadStage> infiniteProducerStages = new LinkedList<HeadStage>();

	public List<HeadStage> getConsumerStages() {
		return this.consumerStages;
	}

	public List<HeadStage> getFiniteProducerStages() {
		return this.finiteProducerStages;
	}

	public List<HeadStage> getInfiniteProducerStages() {
		return this.infiniteProducerStages;
	}

}
