package teetime.stage.taskfarm.analysis;

import teetime.stage.taskfarm.history.ThroughputHistory;

public class WeightedAlgorithm extends ThroughputAnalysisAlgorithm {

	enum WeightMethod {
		LOGARITHMIC,
		LINEAR,
		EXPONENTIAL
	}

	private final WeightMethod weightMethod;

	public WeightedAlgorithm(final WeightMethod weightMethod) {
		this.weightMethod = weightMethod;
	}

	@Override
	protected double doAnalysis(final ThroughputHistory history) {
		double weightedSum = 0;
		double totalWeights = 0;
		int currentDistance = 0;

		// more recent entry means more weight
		for (int i = WINDOW; i > 0; i--) {
			double weight = getWeight(1 + currentDistance);
			totalWeights += weight;
			weightedSum += history.getEntries().get(i).getThroughput() * weight;
			currentDistance++;
		}

		double prediction = weightedSum / totalWeights;
		return prediction;
	}

	private double getWeight(final double distance) {
		switch (weightMethod) {
		case LOGARITHMIC:
			return Math.log(distance);
		case LINEAR:
			return distance;
		case EXPONENTIAL:
			return Math.exp(distance);
		default:
			return distance;
		}
	}
}
