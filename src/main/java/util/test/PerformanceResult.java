package util.test;

import java.util.Map;

public class PerformanceResult {

	public long overallDurationInNs;
	public long sumInNs;
	public Map<Double, Long> quantiles;
	public long avgDurInNs;
	public long confidenceWidthInNs;

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("overallDurationInNs: ");
		stringBuilder.append(this.overallDurationInNs);
		stringBuilder.append("\n");

		stringBuilder.append("sumInNs: ");
		stringBuilder.append(this.sumInNs);
		stringBuilder.append("\n");

		stringBuilder.append("avgDurInNs: ");
		stringBuilder.append(this.avgDurInNs);
		stringBuilder.append("\n");

		stringBuilder.append("confidenceWidthInNs: ");
		stringBuilder.append(this.confidenceWidthInNs);
		stringBuilder.append("\n");

		stringBuilder.append(StatisticsUtil.getQuantilesString(this.quantiles));

		return stringBuilder.toString();
	}
}
