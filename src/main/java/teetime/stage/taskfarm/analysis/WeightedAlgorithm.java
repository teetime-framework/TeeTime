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

		// more recent entry means more weight
		for (int i = WINDOW; i > 0; i--) {
			double weight = getWeight(i - 1);
			totalWeights += weight;
			weightedSum += history.getEntries().get(i).getThroughput() * weight;
		}

		double prediction = weightedSum / totalWeights;
		return prediction;
	}

	private double getWeight(final double distance) {
		double tempWeight = WINDOW - distance;
		switch (weightMethod) {
		case LOGARITHMIC:
			return Math.log(tempWeight);
		case LINEAR:
			return tempWeight;
		case EXPONENTIAL:
			return Math.exp(tempWeight);
		default:
			return tempWeight;
		}
	}
}
