package teetime.examples.experiment09;

import static org.junit.Assert.assertEquals;

public class ChwWorkPerformanceCheck extends AbstractPerformanceCheck {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwWork";
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
		// since 04.07.2014 (incl.)
		// assertEquals(42, value9, 2.1); // +6
		// since 27.08.2014 (incl.)
		// assertEquals(77, value9, 2.1); // +35
		// since 14.10.2014 (incl.)
		assertEquals(67, medianSpeedup, 3.1); // -10
	}
}
