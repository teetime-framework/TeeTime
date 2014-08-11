package teetime.variant.methodcallWithPorts.framework.core;

import java.util.LinkedList;
import java.util.List;

public class Configuration {

	private final List<StageWithPort> consumerStages = new LinkedList<StageWithPort>();
	private final List<StageWithPort> finiteProducerStages = new LinkedList<StageWithPort>();
	private final List<StageWithPort> infiniteProducerStages = new LinkedList<StageWithPort>();

	public List<StageWithPort> getConsumerStages() {
		return this.consumerStages;
	}

	public List<StageWithPort> getFiniteProducerStages() {
		return this.finiteProducerStages;
	}

	public List<StageWithPort> getInfiniteProducerStages() {
		return this.infiniteProducerStages;
	}

}
