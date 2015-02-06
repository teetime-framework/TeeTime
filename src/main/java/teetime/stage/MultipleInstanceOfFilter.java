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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * @author Nils Christian Ehmke
 */
public final class MultipleInstanceOfFilter<I> extends AbstractConsumerStage<I> {

	private final Map<Class<? extends I>, OutputPort<? super I>> outputPortsMap = new HashMap<Class<? extends I>, OutputPort<? super I>>();
	private Entry<Class<? extends I>, OutputPort<? super I>>[] cachedOutputPortsMap;

	@SuppressWarnings("unchecked")
	public <T extends I> OutputPort<T> getOutputPortForType(final Class<T> clazz) {
		if (!this.outputPortsMap.containsKey(clazz)) {
			this.outputPortsMap.put(clazz, super.createOutputPort());
		}
		return (OutputPort<T>) this.outputPortsMap.get(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onStarting() throws Exception {
		super.onStarting();

		// We cache the map to avoid the creating of iterators during runtime
		cachedOutputPortsMap = (Entry<Class<? extends I>, OutputPort<? super I>>[]) outputPortsMap.entrySet().toArray(new Entry<?, ?>[outputPortsMap.size()]);
	}

	@Override
	protected void execute(final I element) {
		for (Entry<Class<? extends I>, OutputPort<? super I>> outputPortMapEntry : cachedOutputPortsMap) {
			if (outputPortMapEntry.getKey().isInstance(element)) {
				outputPortMapEntry.getValue().send(element);
			}
		}
	}
}
