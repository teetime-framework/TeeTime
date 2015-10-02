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

import teetime.framework.AbstractProducerStage;
import teetime.util.ConstructorClosure;

/**
 * @author Christian Wulf
 *
 * @since 1.0
 */
public final class ObjectProducer<T> extends AbstractProducerStage<T> {

	private long numInputObjects;
	private ConstructorClosure<T> inputObjectCreator;

	/**
	 * @param numInputObjects
	 *            number of objects which should be instantiated and sent
	 * @param inputObjectCreator
	 *            a {@link ConstructorClosure} which creates the new instances
	 * @since 1.0
	 */
	public ObjectProducer(final long numInputObjects, final ConstructorClosure<T> inputObjectCreator) {
		if (numInputObjects < 0) {
			throw new IllegalArgumentException("4001 - numInputObjects must be non-negative.");
		}
		this.numInputObjects = numInputObjects;
		this.inputObjectCreator = inputObjectCreator;
	}

	public long getNumInputObjects() {
		return this.numInputObjects;
	}

	public void setNumInputObjects(final long numInputObjects) {
		this.numInputObjects = numInputObjects;
	}

	public ConstructorClosure<T> getInputObjectCreator() {
		return this.inputObjectCreator;
	}

	public void setInputObjectCreator(final ConstructorClosure<T> inputObjectCreator) {
		this.inputObjectCreator = inputObjectCreator;
	}

	@Override
	protected void execute() {
		for (int i = 0; i < numInputObjects; i++) {
			T newObject = this.inputObjectCreator.create();
			outputPort.send(newObject);
		}
		this.terminate();
	}

}
