package util.test;

public abstract class ProfiledPerformanceAssertion {

	public abstract String getCorrespondingPerformanceProfile();

	public abstract void check();

	protected String buildTestMethodIdentifier(final Class<? extends PerformanceTest> testClass, final String methodName) {
		return testClass.getName() + "(" + methodName + ")";
	}
}
