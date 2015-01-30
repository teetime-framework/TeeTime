package teetime.framework;

public class ExceptionTestStage extends AbstractProducerStage {

	public int loops = 0;

	@Override
	protected void execute() {
		if (loops % 1000 == 0) {
			throw new IllegalStateException("1000 loops");
		}
		loops++;
	}
}
