/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import teetime.framework.pipe.IMonitorablePipe;

public class MonitoringThread extends Thread {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MonitoringThread.class);

	// private final List<IMonitorablePipe> monitoredPipes = new ArrayList<IMonitorablePipe>();

	private volatile boolean terminated;

	private final List<AbstractPort<?>> monitoredPorts = new ArrayList<AbstractPort<?>>();

	@Override
	public void run() {
		while (!terminated) {

			for (final AbstractPort<?> port : monitoredPorts) {

				if (LOGGER.isInfoEnabled()) {
					IMonitorablePipe pipe = (IMonitorablePipe) port.getPipe();
					final long pushThroughput = pipe.getPushThroughput();
					final long pullThroughput = pipe.getPullThroughput();
					final double ratio = (double) pushThroughput / pullThroughput;
					LOGGER.info("pipe: " + "size=" + pipe.size() + ", " + "ratio: " + String.format("%.1f", ratio));
					LOGGER.info("pushes: " + pushThroughput);
					LOGGER.info("pulls: " + pullThroughput);
				}
			}
			LOGGER.info("------------------------------------");

			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				terminated = true;
			}
		}
	}

	// public void addPipe(final IPipe pipe) {
	// if (!(pipe instanceof IMonitorablePipe)) {
	// throw new IllegalArgumentException("The given pipe does not implement IMonitorablePipe");
	// }
	// monitoredPipes.add((IMonitorablePipe) pipe);
	// }

	/**
	 * Sets the <code>terminated</code> flag and interrupts this thread.
	 */
	public void terminate() {
		terminated = true;
		interrupt();
	}

	public void addPort(final InputPort<String> inputPort) {
		monitoredPorts.add(inputPort);
	}

}
