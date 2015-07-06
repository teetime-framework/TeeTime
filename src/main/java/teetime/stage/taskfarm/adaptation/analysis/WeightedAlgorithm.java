package teetime.stage.taskfarm.adaptation.analysis;

import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.adaptation.monitoring.ThroughputHistory;

/**
 * WeightedAlgorithm analyzes the throughput of a certain amount of items
 * while giving more weight to more recent items. The weighting can
 * be calculated either logarithmically, linearly or exponentially.
 *
 * @author Christian Claus Wiechmann
 *
 */
public class WeightedAlgorithm extends AbstractThroughputAnalysisAlgorithm {

	/**
	 * This enumeration contains values for logarithmic, exponential
	 * or linear weighting.
	 *
	 * @author Christian Claus Wiechmann
	 *
	 */
	public enum WeightMethod {
		LOGARITHMIC,
		LINEAR,
		EXPONENTIAL
	}

	private final WeightMethod weightMethod;

	/**
	 * Constructor.
	 *
	 * @param weightMethod
	 *            weighting method to be used (see {@link WeightedAlgorithm.WeightMethod})
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this algorithm is used for
	 */
	public WeightedAlgorithm(final WeightMethod weightMethod, final TaskFarmConfiguration<?, ?, ?> configuration) {
		super(configuration);
		this.weightMethod = weightMethod;
	}

	@Override
	protected double doAnalysis(final ThroughputHistory history) {
		double weightedSum = 0;
		double totalWeights = 0;

		// more recent entry means more weight
		for (int i = window; i > 0; i--) {
			final double weight = this.getWeight(i - 1);
			totalWeights += weight;
			weightedSum += history.getThroughputOfEntry(i) * weight;
		}

		return weightedSum / totalWeights;
	}

	private double getWeight(final double distance) {
		final double tempWeight = window - distance;
		double finalWeight;

		switch (this.weightMethod) {
		case LOGARITHMIC:
			finalWeight = Math.log(tempWeight);
			break;
		case LINEAR:
			finalWeight = tempWeight;
			break;
		case EXPONENTIAL:
			finalWeight = Math.exp(tempWeight);
			break;
		default:
			finalWeight = tempWeight;
			break;
		}

		return finalWeight;
	}
}
