package teetime.examples.experiment10;

import static org.junit.Assert.assertEquals;

public class ChwHomePerformanceCheck extends AbstractPerformanceCheck {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwHome";
	}

	@Override
	public void check() {
		super.check();

		double meanSpeedup = (double) test10.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("meanSpeedup (10): " + meanSpeedup);

		// since 26.06.2014 (incl.)
		// assertEquals(26, value10, 2.1); // +14
		// // since 04.07.2014 (incl.)
		// assertEquals(26, value10, 2.1); // +0
		// since 11.08.2014 (incl.)
		// assertEquals(47, value10, 2.1); // +21
		// since 31.08.2014 (incl.)
		assertEquals(51, meanSpeedup, 2.1);
	}
}
