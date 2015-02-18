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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;
import teetime.stage.util.CountingMap;

/**
 * Receives different CountingMap instances and merges them into a single one.
 * The result is sent upon termination.
 *
 * @since 1.1
 *
 * @author Nelson Tavares de Sousa
 *
 * @param <T>
 *            Key type of the map to be sent
 */
public final class CountingMapMerger<T> extends AbstractConsumerStage<CountingMap<T>> {

	private final CountingMap<T> result = new CountingMap<T>();
	private final OutputPort<Map<T, Integer>> port = createOutputPort();

	@Override
	protected void execute(final CountingMap<T> element) {
		Set<Map.Entry<T, Integer>> entries = element.entrySet();
		for (Entry<T, Integer> entry : entries) {
			result.add(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void onTerminating() throws Exception {
		port.send(result);
		super.onTerminating();
	}

	public CountingMap<T> getResult() {
		return result;
	}

}
