package teetime.framework;

public class ExceptionTestConfiguration extends AnalysisConfiguration {

	public ExceptionTestConfiguration() {
		this.addThreadableStage(new ExceptionTestStage());
	}
}
