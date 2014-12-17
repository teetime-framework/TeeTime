package teetime.framework.idle;

public class YieldStrategy implements IdleStrategy {

	@Override
	public void execute() throws InterruptedException {
		Thread.yield();
	}

}
