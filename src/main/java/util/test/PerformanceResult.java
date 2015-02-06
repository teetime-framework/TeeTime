/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package util.test;

import java.util.Map;

import util.test.eval.StatisticsUtil;

public class PerformanceResult {

	public long overallDurationInNs;
	public long sumInNs;
	public Map<Double, Long> quantiles;
	public long avgDurInNs;
	public long confidenceWidthInNs;

	public PerformanceResult() {}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
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
