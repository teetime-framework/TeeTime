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
package teetime.examples.experiment09pipeimpls;

import static org.junit.Assert.assertEquals;
import teetime.examples.HostName;

class ChwWorkPerformanceCheck extends AbstractPerformanceCheck {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return HostName.CHW_WORK.toString();
	}

	@Override
	public void check() {
		super.check();

		checkCommittablePipes();
		checkSingleElementPipes();
		checkOrderedGrowableArrayPipes();
	}

	private void checkCommittablePipes() {
		double medianSpeedup = (double) test09CommittablePipes.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("medianSpeedup (09): " + medianSpeedup);

		// until 25.06.2014 (incl.)
		// assertEquals(22, (double) test9.quantiles.get(0.5) / test1.quantiles.get(0.5), 2.1);
		// since 26.06.2014 (incl.)
		// assertEquals(36, value9, 2.1); // +14
		// since 04.07.2014 (incl.)
		// assertEquals(42, value9, 2.1); // +6
		// since 27.08.2014 (incl.)
		// assertEquals(77, value9, 2.1); // +35
		// since 14.10.2014 (incl.)
		// assertEquals(67, medianSpeedup, 3.1); // -10
		// since 19.12.2014 (incl.)
		assertEquals(53, medianSpeedup, 3.1); // -14
	}

	private void checkSingleElementPipes() {
		double medianSpeedup = (double) test09SingleElementPipes.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("medianSpeedup (09 single element pipes): " + medianSpeedup);

		// until 25.06.2014 (incl.)
		// assertEquals(14, (double) test10.quantiles.get(0.5) / test1.quantiles.get(0.5), 2.1);
		// since 26.06.2014 (incl.)
		// assertEquals(26, meanSpeedup, 2.1); // +14
		// since 04.07.2014 (incl.)
		// assertEquals(26, meanSpeedup, 2.1); // +0
		// since 27.08.2014 (incl.)
		// assertEquals(56, meanSpeedup, 2.1); // +30
		// since 14.10.2014 (incl.)
		assertEquals(25, medianSpeedup, 3.1); // -31
	}

	private void checkOrderedGrowableArrayPipes() {
		double medianSpeedup = (double) test09OrderedGrowableArrayPipes.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("medianSpeedup (09 ordered growable array pipes): " + medianSpeedup);

		// until 25.06.2014 (incl.)
		// assertEquals(60, (double) test14.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// since 26.06.2014 (incl.)
		// assertEquals(76, medianSpeedup, 5.1); // +16
		// since 04.07.2014 (incl.)
		// assertEquals(86, medianSpeedup, 5.1); // +16
		// since 27.08.2014 (incl.)
		// assertEquals(102, medianSpeedup, 5.1); // +16
		// since 14.10.2014 (incl.)
		// assertEquals(81, medianSpeedup, 5.1); // -21
		// since 19.12.2014 (incl.)
		assertEquals(56, medianSpeedup, 5.1); // -25
	}

}
