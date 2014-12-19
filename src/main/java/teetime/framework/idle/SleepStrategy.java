package teetime.framework.idle;

public final class SleepStrategy implements IdleStrategy {

	private final long timeoutInMs;

	public SleepStrategy(final long timeoutInMs) {
		super();
		this.timeoutInMs = timeoutInMs;
	}

	@Override
	public void execute() throws InterruptedException {
		Thread.sleep(timeoutInMs);
	}

}
