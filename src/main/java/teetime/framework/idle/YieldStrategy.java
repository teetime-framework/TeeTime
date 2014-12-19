package teetime.framework.idle;

public final class YieldStrategy implements IdleStrategy {

	@Override
	public void execute() throws InterruptedException {
		Thread.yield();
	}

}
