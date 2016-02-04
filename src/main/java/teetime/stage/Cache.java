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
package teetime.stage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;
import teetime.util.StopWatch;

public final class Cache<T> extends AbstractConsumerStage<T> {

	private final OutputPort<T> outputPort = this.createOutputPort();

	private final List<T> cachedObjects = new LinkedList<T>();

	@Override
	protected void execute(final T element) {
		this.cachedObjects.add(element);
	}

	@Override
	public void onTerminating() throws Exception { // NOPMD
		this.logger.debug("Emitting " + this.cachedObjects.size() + " cached elements...");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for (T cachedElement : this.cachedObjects) {
			outputPort.send(cachedElement);
		}
		stopWatch.end();
		this.logger.debug("Emitting took " + TimeUnit.NANOSECONDS.toMillis(stopWatch.getDurationInNs()) + " ms");
		super.onTerminating();
	}

	public OutputPort<T> getOutputPort() {
		return this.outputPort;
	}

}
