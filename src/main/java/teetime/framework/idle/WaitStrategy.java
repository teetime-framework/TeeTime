package teetime.framework.idle;

import teetime.framework.Stage;

public final class WaitStrategy implements IdleStrategy {

	private final Stage stage;

	public WaitStrategy(final Stage stage) {
		super();
		this.stage = stage;
	}

	@Override
	public void execute() throws InterruptedException {
		synchronized (stage) {
			stage.wait();
		}
	}

}
