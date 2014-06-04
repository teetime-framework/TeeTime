package teetime.stage.basic;

import teetime.framework.core.AbstractFilter;
import teetime.framework.core.Context;
import teetime.framework.core.IOutputPort;

/**
 *
 * The stage implements a clock that continuously outputs the current time as timestamp. This stage MUST be owned by an own thread since this stage calls <code>Thread.sleep()</code>.
 *
 * @author Christian Wulf
 *
 * @since 1.10
 *
 * @param <T>
 */
public class Clock<T> extends AbstractFilter<Clock<T>> {

	public final IOutputPort<Clock<T>, Long> timestampOutputPort = this.createOutputPort();

	private boolean initialDelayExceeded = false;

	private long initialDelayInMs;
	private long intervalDelayInMs;

	/**
	 * @since 1.10
	 */
	@Override
	protected boolean execute(final Context<Clock<T>> context) {
		if (!initialDelayExceeded) {
			initialDelayExceeded = true;
			sleep(initialDelayInMs);
		} else {
			sleep(intervalDelayInMs);
		}

		context.put(timestampOutputPort, getCurrentTimeInNs());

		return true;
	}

	@Override
	public void onPipelineStarts() throws Exception {
		if (getOwningPipeline().getStages().size() > 1) {
			throw new IllegalArgumentException("The clock stage must be the only stage within its owning pipeline since it must be executed by a separate thread.");
		}
		super.onPipelineStarts();
	}

	private void sleep(final long delayInMs) {
		try {
			Thread.sleep(delayInMs);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private long getCurrentTimeInNs() {
		return System.nanoTime();
	}

	public long getInitialDelayInMs() {
		return initialDelayInMs;
	}

	public void setInitialDelayInMs(final long initialDelayInMs) {
		this.initialDelayInMs = initialDelayInMs;
	}

	public long getIntervalDelayInMs() {
		return intervalDelayInMs;
	}

	public void setIntervalDelayInMs(final long intervalDelayInMs) {
		this.intervalDelayInMs = intervalDelayInMs;
	}

}
