package teetime.variant.explicitScheduling.stage.basic;

import teetime.variant.explicitScheduling.framework.core.AbstractFilter;
import teetime.variant.explicitScheduling.framework.core.Context;
import teetime.variant.explicitScheduling.framework.core.IOutputPort;

/**
 *
 * The stage implements a clock that continuously outputs the current time as timestamp. This stage MUST be owned by an own thread since this stage calls <code>Thread.sleep()</code>.
 *
 * @author Christian Wulf
 *
 * @since 1.10
 *
 */
public class Clock extends AbstractFilter<Clock> {

	public final IOutputPort<Clock, Long> timestampOutputPort = this.createOutputPort();

	private boolean initialDelayExceeded = false;

	private long initialDelayInMs;
	private long intervalDelayInMs;

	/**
	 * @since 1.10
	 */
	@Override
	protected boolean execute(final Context<Clock> context) {
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
