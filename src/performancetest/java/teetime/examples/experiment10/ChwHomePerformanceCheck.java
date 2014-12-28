package teetime.examples.experiment10;

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

		double medianSpeedup = (double) test10.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("meanSpeedup (10): " + medianSpeedup);

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
}
