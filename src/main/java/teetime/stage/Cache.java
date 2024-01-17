/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import teetime.stage.basic.AbstractFilter;
import teetime.util.StopWatch;

public class Cache<T> extends AbstractFilter<T> {

	private final List<T> cachedObjects = new LinkedList<>();

	@Override
	protected void execute(final T element) {
		this.cachedObjects.add(element);
	}

	@Override
	protected void onTerminating() {
		this.logger.debug("Emitting {} cached elements...", this.cachedObjects.size());
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for (T cachedElement : this.cachedObjects) {
			this.outputPort.send(cachedElement);
		}
		stopWatch.end();
		this.logger.debug("Emitting took {} ms", TimeUnit.NANOSECONDS.toMillis(stopWatch.getDurationInNs()));
		super.onTerminating();
	}

}
