package teetime.examples.throughput.methodcall.stage;

import teetime.util.list.CommittableQueue;

public class Clock extends ProducerStage<Void, Long> {

	private boolean initialDelayExceeded = false;

	private long initialDelayInMs;
	private long intervalDelayInMs;

	@Override
	public Long execute(final Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void execute4(final CommittableQueue<Void> elements) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void execute5(final Void element) {
		if (!this.initialDelayExceeded) {
			this.initialDelayExceeded = true;
			this.sleep(this.initialDelayInMs);
		} else {
			this.sleep(this.intervalDelayInMs);
		}

		// System.out.println("Emitting timestamp");
		this.send(this.getCurrentTimeInNs());
	}

	private void sleep(final long delayInMs) {
		try {
			Thread.sleep(delayInMs);
		} catch (InterruptedException e) {
			this.setReschedulable(false);
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
