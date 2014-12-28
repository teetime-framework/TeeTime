package teetime.examples.experiment09pipeimpls;

import static org.junit.Assert.assertEquals;

class ChwWorkPerformanceCheck extends AbstractPerformanceCheck {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwWork";
	}

	@Override
	public void check() {
		super.check();

		checkCommittablePipes();
		checkSingleElementPipes();
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
}
