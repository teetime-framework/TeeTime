package teetime.examples.experiment09pipeimpls;

import static org.junit.Assert.assertEquals;
import teetime.examples.HostName;

class ChwHomePerformanceCheck extends AbstractPerformanceCheck {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return HostName.CHW_HOME.toString();
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

		System.out.println("medianSpeedup (09 committable pipes): " + medianSpeedup);

		// until 25.06.2014 (incl.)
		// assertEquals(22, (double) test9.quantiles.get(0.5) / test1.quantiles.get(0.5), 2.1);
		// since 26.06.2014 (incl.)
		// assertEquals(36, value9, 2.1); // +14
		// since 04.07.2014 (incl.)
		// assertEquals(42, value9, 2.1); // +6
		// since 11.08.2014 (incl.)
		// assertEquals(42, value9, 2.1); // +6
		// since 31.08.2014 (incl.)
		// assertEquals(44, medianSpeedup, 2.1); // +2
		// since 04.11.2014 (incl.)
		// assertEquals(71, medianSpeedup, 2.1); // +27
		// since 05.12.2014 (incl.)
		assertEquals(43, medianSpeedup, 2.1); // -28 (41-56)
	}

	private void checkSingleElementPipes() {
		double medianSpeedup = (double) test09SingleElementPipes.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("meanSpeedup (09 single element pipes): " + medianSpeedup);

		// since 26.06.2014 (incl.)
		// assertEquals(26, value10, 2.1); // +14
		// // since 04.07.2014 (incl.)
		// assertEquals(26, value10, 2.1); // +0
		// since 11.08.2014 (incl.)
		// assertEquals(47, value10, 2.1); // +21
		// since 31.08.2014 (incl.)
		// assertEquals(51, medianSpeedup, 3.2); // +4
		// since 13.12.2014 (incl.)
		// assertEquals(40, medianSpeedup, 3.2); // -11
		// since 28.12.2014 (incl.)
		assertEquals(24, medianSpeedup, 3.2); // -16
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
		// since 11.08.2014 (incl.)
		// assertEquals(103, medianSpeedup, 5.1); // +17
		// since 31.08.2014 (incl.)
		// assertEquals(62, medianSpeedup, 2.1); // -41
		// since 04.11.2014 (incl.)
		// assertEquals(84, medianSpeedup, 2.1); // +22
		// since 05.12.2014 (incl.)
		// assertEquals(75, medianSpeedup, 2.1); // -9
		// since 13.12.2014 (incl.)
		// assertEquals(44, medianSpeedup, 2.1); // -31
		// since 28.12.2014 (incl.)
		assertEquals(46, medianSpeedup, 2.1); // +2
	}

}
