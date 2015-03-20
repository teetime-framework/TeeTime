/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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
package teetime.stage;

import java.util.concurrent.TimeUnit;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IPipe;
import teetime.util.StopWatch;

public class InputPortSizePrinter<T> extends AbstractConsumerStage<T> {

	private final OutputPort<T> outputPort = createOutputPort();
	private final StopWatch stopWatch;

	private final long thresholdInNs = TimeUnit.SECONDS.toNanos(1);

	public InputPortSizePrinter() {
		stopWatch = new StopWatch();
		stopWatch.start();
	}

	@Override
	protected void execute(final T element) {
		stopWatch.end();
		if (stopWatch.getDurationInNs() >= thresholdInNs) {
			if (logger.isDebugEnabled()) {
				final IPipe pipe = inputPort.getPipe();
				logger.debug("pipe size: " + pipe.size());
			}
			stopWatch.start();
		}

		outputPort.send(element);
	}

	public OutputPort<T> getOutputPort() {
		return outputPort;
	}

}
