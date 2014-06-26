/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/
package teetime.variant.methodcallWithPorts.stage.stringBuffer;

import java.util.Collection;
import java.util.LinkedList;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;
import teetime.variant.methodcallWithPorts.stage.stringBuffer.handler.AbstractDataTypeHandler;
import teetime.variant.methodcallWithPorts.stage.stringBuffer.util.KiekerHashMap;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class StringBufferFilter<T> extends ConsumerStage<T, T> {

	// BETTER use a non shared data structure to avoid synchronization between threads
	private KiekerHashMap kiekerHashMap = new KiekerHashMap();

	private Collection<AbstractDataTypeHandler<?>> dataTypeHandlers = new LinkedList<AbstractDataTypeHandler<?>>();

	@Override
	protected void execute5(final T element) {
		final T returnedElement = this.handle(element);
		this.send(returnedElement);
	}

	@Override
	public void onStart() {
		for (final AbstractDataTypeHandler<?> handler : this.dataTypeHandlers) {
			handler.setLogger(this.logger);
			handler.setStringRepository(this.kiekerHashMap);
		}
		super.onStart();
	}

	private T handle(final T object) {
		for (final AbstractDataTypeHandler<?> handler : this.dataTypeHandlers) {
			if (handler.canHandle(object)) {
				@SuppressWarnings("unchecked")
				final T returnedObject = ((AbstractDataTypeHandler<T>) handler).handle(object);
				return returnedObject;
			}
		}
		return object; // else relay given object
	}

	public KiekerHashMap getKiekerHashMap() {
		return this.kiekerHashMap;
	}

	public void setKiekerHashMap(final KiekerHashMap kiekerHashMap) {
		this.kiekerHashMap = kiekerHashMap;
	}

	public Collection<AbstractDataTypeHandler<?>> getDataTypeHandlers() {
		return this.dataTypeHandlers;
	}

	public void setDataTypeHandlers(final Collection<AbstractDataTypeHandler<?>> dataTypeHandlers) {
		this.dataTypeHandlers = dataTypeHandlers;
	}

}
