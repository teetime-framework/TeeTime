package util.test;

import java.util.HashMap;
import java.util.Map;

public class MeasurementRepository {

	public final Map<String, PerformanceResult> performanceResults = new HashMap<String, PerformanceResult>();

	public static final String buildTestMethodIdentifier(final Class<?> testClass, final String methodName) {
		return testClass.getName() + "(" + methodName + ")";
	}
}
