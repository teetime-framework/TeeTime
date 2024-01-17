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

import teetime.framework.AbstractConsumerStage;
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

	private final CountingMap<T> mergedResult = new CountingMap<>();

	@Override
	protected void execute(final CountingMap<T> element) {
		mergedResult.add(element);
	}

	public CountingMap<T> getResult() {
		return mergedResult;
	}

}
