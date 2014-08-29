package teetime.variant.methodcallWithPorts.stage;

import teetime.variant.methodcallWithPorts.framework.core.ProducerStage;

public class Clock extends ProducerStage<Long> {

	private boolean initialDelayExceeded = false;

	private long initialDelayInMs;
	private long intervalDelayInMs;

	@Override
	protected void execute() {
		if (!this.initialDelayExceeded) {
			this.initialDelayExceeded = true;
			this.sleep(this.initialDelayInMs);
		} else {
			this.sleep(this.intervalDelayInMs);
		}

		// this.logger.debug("Emitting timestamp");
		this.send(this.outputPort, this.getCurrentTimeInNs());
	}

	private void sleep(final long delayInMs) {
		try {
			Thread.sleep(delayInMs);
		} catch (InterruptedException e) {
			this.terminate();
		}
	}

	private long getCurrentTimeInNs() {
		return System.nanoTime();
	}

	public long getInitialDelayInMs() {
		return this.initialDelayInMs;
	}

	public void setInitialDelayInMs(final long initialDelayInMs) {
		this.initialDelayInMs = initialDelayInMs;
	}

	public long getIntervalDelayInMs() {
		return this.intervalDelayInMs;
	}

	public void setIntervalDelayInMs(final long intervalDelayInMs) {
		this.intervalDelayInMs = intervalDelayInMs;
	}

}
