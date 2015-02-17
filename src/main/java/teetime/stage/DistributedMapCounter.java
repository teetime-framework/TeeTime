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

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;
import teetime.stage.util.CountingMap;

/**
 * This counts how many of different elements are sent to this stage. Nothing is forwarded.
 * On termination a CountingMap is sent to its outputport.
 *
 * @author Nelson Tavares de Sousa
 *
 * @param <T>
 *            Type to be count
 */
public class DistributedMapCounter<T> extends AbstractConsumerStage<T> {

	private final CountingMap<T> counter = new CountingMap<T>();
	private final OutputPort<CountingMap<T>> port = createOutputPort();

	public DistributedMapCounter() {

	}

	@Override
	protected void execute(final T element) {
		counter.increment(element);

	}

	@Override
	public void onTerminating() throws Exception {
		port.send(counter);
		super.onTerminating();
	}

}
