package teetime.examples.experiment09;

import static org.junit.Assert.assertEquals;

public class ChwHomePerformanceCheck extends AbstractPerformanceCheck {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwHome";
	}

	@Override
	public void check() {
		super.check();

		double medianSpeedup = (double) test09.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("medianSpeedup (09): " + medianSpeedup);

		// until 25.06.2014 (incl.)
		// assertEquals(22, (double) test9.quantiles.get(0.5) / test1.quantiles.get(0.5), 2.1);
		// since 26.06.2014 (incl.)
		// assertEquals(36, value9, 2.1); // +14
		// // since 04.07.2014 (incl.)
		// assertEquals(42, value9, 2.1); // +6
		// since 11.08.2014 (incl.)
		// assertEquals(42, value9, 2.1); // +6
		// since 31.08.2014 (incl.)
		assertEquals(44, medianSpeedup, 2.1); // ??
	}
}
