package teetime.framework;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;

public class MonitoringThread extends Thread {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MonitoringThread.class);

	private final List<IMonitorablePipe> monitoredPipes = new ArrayList<IMonitorablePipe>();

	private volatile boolean terminated;

	@Override
	public void run() {
		while (!terminated) {

			for (final IMonitorablePipe pipe : monitoredPipes) {
				final long pushThroughput = pipe.getPushThroughput();
				final long pullThroughput = pipe.getPullThroughput();
				final double ratio = (double) pushThroughput / pullThroughput;

				LOGGER.info("pipe: " + "size=" + pipe.size() + ", " + "ratio: " + String.format("%.1f", ratio));
				LOGGER.info("pushes: " + pushThroughput);
				LOGGER.info("pulls: " + pullThroughput);
			}
			LOGGER.info("------------------------------------");

			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				terminated = true;
			}
		}
	}

	public void addPipe(final IPipe pipe) {
		if (!(pipe instanceof IMonitorablePipe)) {
			throw new IllegalArgumentException("The given pipe does not implement IMonitorablePipe");
		}
		monitoredPipes.add((IMonitorablePipe) pipe);
	}

	/**
	 * Sets the <code>terminated</code> flag and interrupts this thread.
	 */
	public void terminate() {
		terminated = true;
		interrupt();
	}

}
